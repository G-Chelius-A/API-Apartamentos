package com.blue.apartamentos.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blue.apartamentos.models.PagoModel;
import com.blue.apartamentos.models.ReservacionModel;
import com.blue.apartamentos.repositories.IPagoRepository;
import com.blue.apartamentos.repositories.IReservacionRepository;

// Lógica de negocio para pagos; valida saldos y confirma reservaciones automáticamente
@Service
public class PagoService {
    @Autowired
    private IPagoRepository pagoRepository;
    @Autowired
    private IReservacionRepository reservacionRepository; // para validar el total y cambiar estado de la reservación

    // Retorna todos los pagos del sistema con su reservación cargada
    public ArrayList<PagoModel> obtenerPagos() { return pagoRepository.findAllWithReservacion(); }

    // Guarda un nuevo pago; si queda aceptado y la reservación estaba pendiente, la confirma automáticamente
    @Transactional
    public PagoModel guardarPago(PagoModel pago) {
        prepararPago(pago, null);
        PagoModel guardado = pagoRepository.save(pago);
        confirmarReservacionSiCorresponde(guardado);
        return guardado;
    }

    // Busca un pago por ID; retorna Optional vacío si no existe
    public Optional<PagoModel> obtenerPorId(Long id) { return pagoRepository.findById(id); }

    // Actualiza monto, referencia, datos, método y estado; confirma la reservación si aplica
    @Transactional
    public PagoModel actualizarPago(PagoModel request, Long id) {
        PagoModel pago = pagoRepository.findById(id).orElseThrow(() -> new RuntimeException("Pago no encontrado con el ID: " + id));
        pago.setMonto(request.getMonto());
        pago.setReferenciaPago(request.getReferenciaPago());
        pago.setDatosPago(request.getDatosPago());
        pago.setMetodo(request.getMetodo());
        pago.setEstado(request.getEstado());
        prepararPago(pago, id);
        PagoModel actualizado = pagoRepository.save(pago);
        confirmarReservacionSiCorresponde(actualizado);
        return actualizado;
    }

    // Verifica si el total de pagos aceptados de una reservación cubre el precio total
    public boolean estaPagada(Long idReservacion) {
        ReservacionModel reservacion = obtenerReservacion(idReservacion);
        BigDecimal total = validarTotal(reservacion);
        BigDecimal totalPagado = BigDecimal.ZERO;
        for (PagoModel pago : pagoRepository.findByReservacion_IdReservacion(idReservacion)) {
            if (pago.getEstado() == PagoModel.Estado.aceptado) {
                totalPagado = totalPagado.add(pago.getMonto());
            }
        }
        return totalPagado.compareTo(total) >= 0;
    }

    // Valida los campos del pago y verifica que no exceda el saldo adeudado de la reservación
    private void prepararPago(PagoModel pago, Long idPago) {
        if (pago == null || pago.getReservacion() == null || pago.getReservacion().getIdReservacion() == null) {
            throw new IllegalArgumentException("La reservación del pago es obligatoria");
        }
        if (pago.getMonto() == null || pago.getMonto().signum() <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser mayor que cero");
        }
        if (pago.getReferenciaPago() == null || pago.getReferenciaPago().isBlank()) {
            throw new IllegalArgumentException("La referencia del pago es obligatoria");
        }
        if (pago.getDatosPago() == null || pago.getDatosPago().isBlank()) {
            throw new IllegalArgumentException("Los datos del pago son obligatorios");
        }
        if (pago.getMetodo() == null) {
            throw new IllegalArgumentException("El método de pago es obligatorio");
        }
        if (pago.getEstado() == null) {
            pago.setEstado(PagoModel.Estado.pendiente); // estado por defecto
        }

        ReservacionModel reservacion = obtenerReservacion(pago.getReservacion().getIdReservacion());
        BigDecimal total = validarTotal(reservacion);
        // Suma todos los pagos aceptados/pendientes existentes, excluyendo el pago actual si es una actualización
        BigDecimal comprometido = BigDecimal.ZERO;
        for (PagoModel existente : pagoRepository.findByReservacion_IdReservacion(reservacion.getIdReservacion())) {
            boolean esOtroPago = existente.getIdPago() == null || !existente.getIdPago().equals(idPago);
            boolean consumeSaldo = existente.getEstado() == PagoModel.Estado.aceptado
                || existente.getEstado() == PagoModel.Estado.pendiente;
            if (esOtroPago && consumeSaldo) {
            comprometido = comprometido.add(existente.getMonto());
            }
        }
        if (comprometido.add(pago.getMonto()).compareTo(total) > 0) {
            throw new IllegalArgumentException("El pago excede el saldo adeudado de la reservación");
        }
        pago.setReservacion(reservacion);
    }

    // Obtiene la reservación por ID; lanza excepción si no existe
    private ReservacionModel obtenerReservacion(Long idReservacion) {
        return reservacionRepository.findById(idReservacion)
                .orElseThrow(() -> new IllegalArgumentException("Reservación no encontrada con el ID: " + idReservacion));
    }

    // Si el pago fue aceptado y la reservación aún estaba pendiente, la confirma automáticamente
    private void confirmarReservacionSiCorresponde(PagoModel pago) {
        ReservacionModel reservacion = pago.getReservacion();
        if (pago.getEstado() == PagoModel.Estado.aceptado
                && reservacion.getEstado() == ReservacionModel.Estado.pendiente) {
            reservacion.setEstado(ReservacionModel.Estado.confirmada);
            reservacionRepository.save(reservacion);
        }
    }

    // Retorna el precio total de la reservación; lanza excepción si es nulo o negativo
    private BigDecimal validarTotal(ReservacionModel reservacion) {
        if (reservacion.getPrecioTotal() == null || reservacion.getPrecioTotal().signum() < 0) {
            throw new IllegalStateException("La reservación no tiene un total válido");
        }
        return reservacion.getPrecioTotal();
    }

    // Elimina un pago por ID; retorna false si falla
    public boolean eliminarPago(Long id) {
        try { pagoRepository.deleteById(id); return true; } catch (Exception e) { return false; }
    }

    // Filtra pagos por reservación
    public ArrayList<PagoModel> obtenerPorReservacion(Long idReservacion) { return pagoRepository.findByReservacion_IdReservacion(idReservacion); }
    // Filtra pagos por cliente (a través de la reservación)
    public ArrayList<PagoModel> obtenerPorCliente(Long idUsuario) { return pagoRepository.findByReservacion_Cliente_IdUsuario(idUsuario); }
    // Filtra por fecha/hora exacta de pago
    public ArrayList<PagoModel> obtenerPorFechaPago(LocalDateTime fecha) { return pagoRepository.findByFechaPago(fecha); }
    // Filtra por estado del pago
    public ArrayList<PagoModel> obtenerPorEstado(PagoModel.Estado estado) { return pagoRepository.findByEstado(estado); }
    // Filtra por referencia de transacción
    public ArrayList<PagoModel> obtenerPorReferencia(String referencia) { return pagoRepository.findByReferenciaPago(referencia); }
}
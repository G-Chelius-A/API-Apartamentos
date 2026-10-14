package com.blue.apartamentos.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blue.apartamentos.models.PagoModel;
import com.blue.apartamentos.models.ReembolsoModel;
import com.blue.apartamentos.models.ReservacionModel;
import com.blue.apartamentos.repositories.IPagoRepository;
import com.blue.apartamentos.repositories.IReembolsoRepository;

// Lógica de negocio para reembolsos; controla la máquina de estados y el monto reembolsable
@Service
public class ReembolsoService {
    @Autowired
    private IReembolsoRepository reembolsoRepository;
    @Autowired
    private IPagoRepository pagoRepository; // para acceder al pago origen y actualizar su estado al procesar

    // Retorna todos los reembolsos del sistema con su pago y reservación cargados
    public ArrayList<ReembolsoModel> obtenerReembolsos() { return reembolsoRepository.findAllWithPagoAndReservacion(); }

    // Guarda un reembolso directo; valida el pago, la reservación cancelada y el monto disponible
    @Transactional
    public ReembolsoModel guardarReembolso(ReembolsoModel reembolso) {
        prepararReembolso(reembolso, null);
        return reembolsoRepository.save(reembolso);
    }

    // Guarda un reembolso para una reservación cancelada; localiza automáticamente un pago aceptado
    @Transactional
    public ReembolsoModel guardarReembolsoParaReservacion(Long idReservacion, ReembolsoModel reembolso) {
        if (reembolso == null) {
            throw new IllegalArgumentException("El reembolso es obligatorio");
        }
        List<PagoModel> pagos = pagoRepository.findByReservacion_IdReservacion(idReservacion);
        Long idPagoSolicitado = reembolso.getPago() == null ? null : reembolso.getPago().getIdPago();
        // Busca el primer pago aceptado de la reservación (o el específico si se indica)
        PagoModel pago = pagos.stream()
                .filter(item -> item.getEstado() == PagoModel.Estado.aceptado)
                .filter(item -> idPagoSolicitado == null || item.getIdPago().equals(idPagoSolicitado))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No existe un pago aceptado para la reservación indicada"));
        reembolso.setPago(pago);
        return guardarReembolso(reembolso);
    }

    // Busca un reembolso por ID; retorna Optional vacío si no existe
    public Optional<ReembolsoModel> obtenerPorId(Long id) { return reembolsoRepository.findById(id); }

    // Actualiza solo el monto de un reembolso existente; revalida el saldo disponible
    @Transactional
    public ReembolsoModel actualizarReembolso(ReembolsoModel request, Long id) {
        ReembolsoModel reembolso = reembolsoRepository.findById(id).orElseThrow(() -> new RuntimeException("Reembolso no encontrado con el ID: " + id));
        reembolso.setMontoReembolso(request.getMontoReembolso());
        prepararReembolso(reembolso, id);
        return reembolsoRepository.save(reembolso);
    }

    // Cambia el estado del reembolso; al pasar a "procesado" marca el pago original como "reembolso"
    // Máquina de estados: pendiente → aprobado → procesado | rechazado
    @Transactional
    public ReembolsoModel cambiarEstadoReembolso(Long id, ReembolsoModel.Estado nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado del reembolso es obligatorio");
        }
        ReembolsoModel reembolso = reembolsoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reembolso no encontrado con el ID: " + id));
        validarCambioEstado(reembolso.getEstado(), nuevoEstado);
        if (nuevoEstado == ReembolsoModel.Estado.procesado) {
            prepararReembolso(reembolso, id);
            reembolso.getPago().setEstado(PagoModel.Estado.reembolso); // marca el pago como reembolsado
            pagoRepository.save(reembolso.getPago());
        }
        reembolso.setEstado(nuevoEstado);
        return reembolsoRepository.save(reembolso);
    }

    // Calcula cuánto queda disponible para reembolsar de un pago (monto - ya reembolsado)
    public BigDecimal obtenerMontoReembolsable(Long idPago) {
        PagoModel pago = obtenerPago(idPago);
        validarCondicionesReembolso(pago);
        return calcularMontoReembolsable(pago, null);
    }

    // Valida el pago, la reservación cancelada, el monto y asigna estado por defecto si no se indica
    private void prepararReembolso(ReembolsoModel reembolso, Long idReembolso) {
        if (reembolso == null || reembolso.getPago() == null || reembolso.getPago().getIdPago() == null) {
            throw new IllegalArgumentException("El pago del reembolso es obligatorio");
        }
        if (reembolso.getEstado() == null) {
            reembolso.setEstado(ReembolsoModel.Estado.pendiente); // estado inicial por defecto
        }
        PagoModel pago = obtenerPago(reembolso.getPago().getIdPago());
        validarCondicionesReembolso(pago);
        BigDecimal montoReembolsable = calcularMontoReembolsable(pago, idReembolso);
        if (reembolso.getMontoReembolso() == null) {
            reembolso.setMontoReembolso(montoReembolsable); // si no se indica, usa el máximo disponible
        }
        if (reembolso.getMontoReembolso().signum() <= 0
                || reembolso.getMontoReembolso().compareTo(montoReembolsable) > 0) {
            throw new IllegalArgumentException("El monto del reembolso excede el monto disponible del pago");
        }
        reembolso.setPago(pago);
    }

    // Suma todos los reembolsos activos (no rechazados) del pago y descuenta del monto original
    private BigDecimal calcularMontoReembolsable(PagoModel pago, Long idReembolso) {
        BigDecimal reembolsado = BigDecimal.ZERO;
        for (ReembolsoModel existente : reembolsoRepository.findByPago_IdPago(pago.getIdPago())) {
            boolean esOtroReembolso = existente.getIdReembolso() == null
                    || !existente.getIdReembolso().equals(idReembolso);
            if (esOtroReembolso && existente.getEstado() != ReembolsoModel.Estado.rechazado) {
                reembolsado = reembolsado.add(existente.getMontoReembolso());
            }
        }
        return pago.getMonto().subtract(reembolsado).max(BigDecimal.ZERO);
    }

    // Verifica que el pago esté aceptado y que la reservación esté cancelada
    private void validarCondicionesReembolso(PagoModel pago) {
        if (pago.getEstado() != PagoModel.Estado.aceptado) {
            throw new IllegalStateException("Solo se pueden reembolsar pagos aceptados");
        }
        ReservacionModel reservacion = pago.getReservacion();
        if (reservacion == null || reservacion.getEstado() != ReservacionModel.Estado.cancelada) {
            throw new IllegalStateException("La reservación debe estar cancelada para solicitar un reembolso");
        }
        if (pago.getMonto() == null || pago.getMonto().signum() <= 0) {
            throw new IllegalStateException("El pago no tiene un monto válido para reembolsar");
        }
    }

    // Obtiene el pago por ID; lanza excepción si no existe
    private PagoModel obtenerPago(Long idPago) {
        return pagoRepository.findById(idPago)
                .orElseThrow(() -> new IllegalArgumentException("Pago no encontrado con el ID: " + idPago));
    }

    // Valida que la transición de estado del reembolso sea permitida por la máquina de estados
    private void validarCambioEstado(ReembolsoModel.Estado estadoActual, ReembolsoModel.Estado nuevoEstado) {
        if (estadoActual == nuevoEstado) {
            return;
        }
        boolean valido = switch (estadoActual) {
            case pendiente -> nuevoEstado == ReembolsoModel.Estado.aprobado
                    || nuevoEstado == ReembolsoModel.Estado.rechazado;
            case aprobado -> nuevoEstado == ReembolsoModel.Estado.procesado
                    || nuevoEstado == ReembolsoModel.Estado.rechazado;
            case rechazado, procesado -> false; // estados finales: no hay transición posible
        };
        if (!valido) {
            throw new IllegalStateException("Cambio de estado de reembolso no permitido: "
                    + estadoActual + " -> " + nuevoEstado);
        }
    }

    // Elimina un reembolso por ID; retorna false si falla
    public boolean eliminarReembolso(Long id) {
        try { reembolsoRepository.deleteById(id); return true; } catch (Exception e) { return false; }
    }

    // Filtra reembolsos por pago origen
    public ArrayList<ReembolsoModel> obtenerPorPago(Long idPago) { return reembolsoRepository.findByPago_IdPago(idPago); }
    // Filtra reembolsos por cliente (a través del pago → reservación → cliente)
    public ArrayList<ReembolsoModel> obtenerPorCliente(Long idUsuario) { return reembolsoRepository.findByPago_Reservacion_Cliente_IdUsuario(idUsuario); }
    // Filtra reembolsos por fecha/hora exacta
    public ArrayList<ReembolsoModel> obtenerPorFecha(LocalDateTime fecha) { return reembolsoRepository.findByFechaReembolso(fecha); }
    // Filtra reembolsos por monto exacto
    public ArrayList<ReembolsoModel> obtenerPorMonto(BigDecimal monto) { return reembolsoRepository.findByMontoReembolso(monto); }
}
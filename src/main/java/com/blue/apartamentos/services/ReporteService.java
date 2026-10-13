package com.blue.apartamentos.services;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blue.apartamentos.models.PagoModel;
import com.blue.apartamentos.models.ReservacionModel;
import com.blue.apartamentos.repositories.IPagoRepository;
import com.blue.apartamentos.repositories.IReservacionRepository;

// Servicio de reportes de negocio (solo lectura, no modifica datos)
@Service
public class ReporteService {

    // Reporte de ocupación: total de reservaciones y conteo por estado
    public record OcupacionReporte(long totalReservaciones, long pendientes, long confirmadas,
            long enCurso, long completadas) { }

    // Reporte de ingresos: suma de pagos aceptados y su cantidad
    public record IngresosReporte(BigDecimal totalIngresos, long pagosAceptados) { }

    // Reporte de morosidad: deuda acumulada de reservaciones no saldadas
    public record MorosidadReporte(BigDecimal deudaTotal, long reservacionesMorosas) { }

    private final IPagoRepository pagoRepository;
    private final IReservacionRepository reservacionRepository;

    public ReporteService(IPagoRepository pagoRepository, IReservacionRepository reservacionRepository) {
        this.pagoRepository = pagoRepository;
        this.reservacionRepository = reservacionRepository;
    }

    // Retorna el reporte de ocupación con conteo total y por estado
    @Transactional(readOnly = true)
    public OcupacionReporte obtenerOcupacion() {
        return new OcupacionReporte(
                reservacionRepository.count(),
                contarEstado(ReservacionModel.Estado.pendiente),
                contarEstado(ReservacionModel.Estado.confirmada),
                contarEstado(ReservacionModel.Estado.en_curso),
                contarEstado(ReservacionModel.Estado.completada));
    }

    // Suma todos los pagos aceptados y retorna el total de ingresos
    @Transactional(readOnly = true)
    public IngresosReporte obtenerIngresos() {
        BigDecimal total = BigDecimal.ZERO;
        long pagosAceptados = 0;
        for (PagoModel pago : pagoRepository.findAll()) {
            if (pago.getEstado() == PagoModel.Estado.aceptado) {
                total = total.add(pago.getMonto());
                pagosAceptados++;
            }
        }
        return new IngresosReporte(total, pagosAceptados);
    }

    // Calcula la deuda acumulada de todas las reservaciones donde los pagos aceptados no cubren el total
    @Transactional(readOnly = true)
    public MorosidadReporte obtenerMorosidad() {
        BigDecimal deudaTotal = BigDecimal.ZERO;
        long reservacionesMorosas = 0;
        for (ReservacionModel reservacion : reservacionRepository.findAll()) {
            BigDecimal pagado = BigDecimal.ZERO;
            for (PagoModel pago : pagoRepository.findByReservacion_IdReservacion(reservacion.getIdReservacion())) {
                if (pago.getEstado() == PagoModel.Estado.aceptado) {
                    pagado = pagado.add(pago.getMonto());
                }
            }
            BigDecimal deuda = reservacion.getPrecioTotal().subtract(pagado).max(BigDecimal.ZERO);
            deudaTotal = deudaTotal.add(deuda);
            if (deuda.signum() > 0) reservacionesMorosas++;
        }
        return new MorosidadReporte(deudaTotal, reservacionesMorosas);
    }

    // Cuenta reservaciones en un estado específico usando el repositorio con query derivada
    private long contarEstado(ReservacionModel.Estado estado) {
        return reservacionRepository.countByEstado(estado);
    }
}
package com.blue.apartamentos.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.blue.apartamentos.services.ReporteService;
import com.blue.apartamentos.services.ReporteService.IngresosReporte;
import com.blue.apartamentos.services.ReporteService.MorosidadReporte;
import com.blue.apartamentos.services.ReporteService.OcupacionReporte;

// API REST de reportes de negocio (solo lectura, sin modificaciones de datos)
@RestController
@RequestMapping("/api/reportes")
public class ReporteController {
    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) { this.reporteService = reporteService; }

    // GET /api/reportes/ocupacion — total de reservaciones y desglose por estado
    @GetMapping("/ocupacion")
    public OcupacionReporte ocupacion() { return reporteService.obtenerOcupacion(); }

    // GET /api/reportes/ingresos — monto acumulado y número de pagos aceptados
    @GetMapping("/ingresos")
    public IngresosReporte ingresos() { return reporteService.obtenerIngresos(); }

    // GET /api/reportes/morosidad — deuda total acumulada y reservaciones sin saldar
    @GetMapping("/morosidad")
    public MorosidadReporte morosidad() { return reporteService.obtenerMorosidad(); }
}
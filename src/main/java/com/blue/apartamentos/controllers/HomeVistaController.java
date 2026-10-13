package com.blue.apartamentos.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.blue.apartamentos.services.ReporteService;
import com.blue.apartamentos.services.UnidadRentableService;
import com.blue.apartamentos.services.UsuarioService;

// Controlador Thymeleaf para el dashboard principal (panel de inicio) de la vista web
@Controller
@RequestMapping("/vista")
public class HomeVistaController {

    private final UnidadRentableService unidadService;
    private final UsuarioService usuarioService;
    private final ReporteService reporteService; // provee métricas de ocupación e ingresos

    public HomeVistaController(UnidadRentableService unidadService,
                               UsuarioService usuarioService,
                               ReporteService reporteService) {
        this.unidadService = unidadService;
        this.usuarioService = usuarioService;
        this.reporteService = reporteService;
    }

    // GET /vista y /vista/ — muestra el dashboard con métricas en tiempo real
    @GetMapping({"", "/"})
    public String index(Model model) {
        model.addAttribute("totalUnidades", unidadService.obtenerUnidades().size());
        model.addAttribute("totalUsuarios", usuarioService.obtenerUsuarios().size());
        model.addAttribute("reporteOcupacion", reporteService.obtenerOcupacion());
        model.addAttribute("reporteIngresos", reporteService.obtenerIngresos());
        return "index";
    }
}

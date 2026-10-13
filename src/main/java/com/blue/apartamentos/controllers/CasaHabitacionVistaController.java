package com.blue.apartamentos.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.blue.apartamentos.models.CasaModel;
import com.blue.apartamentos.models.HabitacionModel;
import com.blue.apartamentos.services.CasaService;
import com.blue.apartamentos.services.HabitacionService;
import com.blue.apartamentos.services.UnidadRentableService;

// Controlador Thymeleaf para las secciones de Casas y Habitaciones en la vista web
@Controller
@RequestMapping("/vista")
public class CasaHabitacionVistaController {

    private final CasaService casaService;
    private final HabitacionService habitacionService;
    private final UnidadRentableService unidadService; // necesario para poblar el selector de unidades

    public CasaHabitacionVistaController(CasaService casaService,
                                         HabitacionService habitacionService,
                                         UnidadRentableService unidadService) {
        this.casaService = casaService;
        this.habitacionService = habitacionService;
        this.unidadService = unidadService;
    }

    // --- CASAS ---

    // Muestra el listado completo de casas
    @GetMapping("/casas")
    public String listarCasas(Model model) {
        model.addAttribute("casas", casaService.obtenerCasas());
        return "casas/lista";
    }

    // Abre el formulario en blanco para registrar una nueva casa
    @GetMapping("/casas/nueva")
    public String formularioCasa(Model model) {
        model.addAttribute("casa", new CasaModel());
        model.addAttribute("unidades", unidadService.obtenerUnidades());
        return "casas/formulario";
    }

    // Carga el formulario de edición con los datos actuales de la casa indicada
    @GetMapping("/casas/{id}/editar")
    public String editarCasa(@PathVariable Long id, Model model, RedirectAttributes flash) {
        return casaService.obtenerPorId(id)
                .map(casa -> {
                    model.addAttribute("casa", casa);
                    model.addAttribute("unidades", unidadService.obtenerUnidades());
                    return "casas/formulario";
                })
                .orElseGet(() -> {
                    flash.addFlashAttribute("msgError", "Casa no encontrada");
                    return "redirect:/vista/casas";
                });
    }

    // Guarda o actualiza una casa según si ya tiene ID asignado
    @PostMapping("/casas/guardar")
    public String guardarCasa(@ModelAttribute CasaModel casa, RedirectAttributes flash) {
        try {
            if (casa.getIdCasa() != null && casaService.obtenerPorId(casa.getIdCasa()).isPresent()) {
                casaService.actualizarCasa(casa, casa.getIdCasa());
                flash.addFlashAttribute("msgExito", "Casa actualizada exitosamente");
            } else {
                casaService.guardarCasa(casa);
                flash.addFlashAttribute("msgExito", "Casa registrada exitosamente");
            }
            return "redirect:/vista/casas";
        } catch (Exception e) {
            flash.addFlashAttribute("msgError", "Error al guardar casa: " + e.getMessage());
            return "redirect:/vista/casas/nueva";
        }
    }

    // Elimina una casa; muestra error si tiene habitaciones asociadas
    @PostMapping("/casas/{id}/eliminar")
    public String eliminarCasa(@PathVariable Long id, RedirectAttributes flash) {
        if (casaService.eliminarCasa(id)) {
            flash.addFlashAttribute("msgExito", "Casa eliminada");
        } else {
            flash.addFlashAttribute("msgError", "No se pudo eliminar la casa (posibles habitaciones asociadas)");
        }
        return "redirect:/vista/casas";
    }

    // --- HABITACIONES ---

    // Muestra el listado de todas las habitaciones
    @GetMapping("/habitaciones")
    public String listarHabitaciones(Model model) {
        model.addAttribute("habitaciones", habitacionService.obtenerHabitaciones());
        return "habitaciones/lista";
    }

    // Abre el formulario en blanco para registrar una nueva habitación
    @GetMapping("/habitaciones/nueva")
    public String formularioHabitacion(Model model) {
        model.addAttribute("habitacion", new HabitacionModel());
        model.addAttribute("casas", casaService.obtenerCasas());
        model.addAttribute("tipos", HabitacionModel.TipoHabitacion.values()); // individual | matrimonial | familiar
        return "habitaciones/formulario";
    }

    // Carga el formulario de edición con los datos actuales de la habitación indicada
    @GetMapping("/habitaciones/{id}/editar")
    public String editarHabitacion(@PathVariable Long id, Model model, RedirectAttributes flash) {
        return habitacionService.obtenerPorId(id)
                .map(hab -> {
                    model.addAttribute("habitacion", hab);
                    model.addAttribute("casas", casaService.obtenerCasas());
                    model.addAttribute("tipos", HabitacionModel.TipoHabitacion.values());
                    return "habitaciones/formulario";
                })
                .orElseGet(() -> {
                    flash.addFlashAttribute("msgError", "Habitación no encontrada");
                    return "redirect:/vista/habitaciones";
                });
    }

    // Guarda o actualiza una habitación según si ya tiene ID asignado
    @PostMapping("/habitaciones/guardar")
    public String guardarHabitacion(@ModelAttribute HabitacionModel habitacion, RedirectAttributes flash) {
        try {
            if (habitacion.getIdHabitacion() != null) {
                habitacionService.actualizarHabitacion(habitacion, habitacion.getIdHabitacion());
                flash.addFlashAttribute("msgExito", "Habitación actualizada exitosamente");
            } else {
                habitacionService.guardarHabitacion(habitacion);
                flash.addFlashAttribute("msgExito", "Habitación creada exitosamente");
            }
            return "redirect:/vista/habitaciones";
        } catch (Exception e) {
            flash.addFlashAttribute("msgError", "Error al guardar habitación: " + e.getMessage());
            return "redirect:/vista/habitaciones/nueva";
        }
    }

    // Elimina una habitación por ID
    @PostMapping("/habitaciones/{id}/eliminar")
    public String eliminarHabitacion(@PathVariable Long id, RedirectAttributes flash) {
        if (habitacionService.eliminarHabitacion(id)) {
            flash.addFlashAttribute("msgExito", "Habitación eliminada correctamente");
        } else {
            flash.addFlashAttribute("msgError", "No se pudo eliminar la habitación");
        }
        return "redirect:/vista/habitaciones";
    }
}

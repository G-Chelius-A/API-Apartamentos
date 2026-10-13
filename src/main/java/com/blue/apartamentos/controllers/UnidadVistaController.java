package com.blue.apartamentos.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.blue.apartamentos.models.UnidadRentableModel;
import com.blue.apartamentos.services.UnidadRentableService;
import com.blue.apartamentos.services.UsuarioService;

// Controlador Thymeleaf para la sección de Unidades Rentables en la vista web
@Controller
@RequestMapping("/vista/unidades")
public class UnidadVistaController {

    private final UnidadRentableService unidadService;
    private final UsuarioService usuarioService; // para el selector de propietario en el formulario

    public UnidadVistaController(UnidadRentableService unidadService, UsuarioService usuarioService) {
        this.unidadService = unidadService;
        this.usuarioService = usuarioService;
    }

    // Muestra el catálogo completo de unidades rentables
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("unidades", unidadService.obtenerUnidades());
        return "unidades/lista";
    }

    // Abre el formulario en blanco para registrar una nueva unidad
    @GetMapping("/nueva")
    public String formularioNuevo(Model model) {
        model.addAttribute("unidad", new UnidadRentableModel());
        model.addAttribute("usuarios", usuarioService.obtenerUsuarios());
        return "unidades/formulario";
    }

    // Carga el formulario de edición con los datos actuales de la unidad indicada
    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        return unidadService.obtenerPorId(id)
                .map(unidad -> {
                    model.addAttribute("unidad", unidad);
                    model.addAttribute("usuarios", usuarioService.obtenerUsuarios());
                    return "unidades/formulario";
                })
                .orElseGet(() -> {
                    flash.addFlashAttribute("msgError", "Unidad rentable no encontrada");
                    return "redirect:/vista/unidades";
                });
    }

    // Guarda o actualiza una unidad; redirige al formulario correcto en caso de error
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute UnidadRentableModel unidad, RedirectAttributes flash) {
        try {
            if (unidad.getIdUnidad() != null) {
                unidadService.actualizarUnidad(unidad, unidad.getIdUnidad());
                flash.addFlashAttribute("msgExito", "Unidad rentable actualizada exitosamente");
            } else {
                unidadService.guardarUnidad(unidad);
                flash.addFlashAttribute("msgExito", "Unidad rentable creada exitosamente");
            }
            return "redirect:/vista/unidades";
        } catch (Exception e) {
            flash.addFlashAttribute("msgError", "Error al procesar la unidad: " + e.getMessage());
            return unidad.getIdUnidad() != null
                    ? "redirect:/vista/unidades/" + unidad.getIdUnidad() + "/editar"
                    : "redirect:/vista/unidades/nueva";
        }
    }

    // Elimina una unidad por ID; muestra error si tiene registros relacionados
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        if (unidadService.eliminarUnidad(id)) {
            flash.addFlashAttribute("msgExito", "Unidad eliminada correctamente");
        } else {
            flash.addFlashAttribute("msgError", "No se pudo eliminar la unidad (puede tener registros relacionados)");
        }
        return "redirect:/vista/unidades";
    }
}

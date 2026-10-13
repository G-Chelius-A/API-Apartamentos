package com.blue.apartamentos.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.blue.apartamentos.models.DisponibilidadModel;
import com.blue.apartamentos.models.PropiedadImagenModel;
import com.blue.apartamentos.services.DisponibilidadService;
import com.blue.apartamentos.services.PropiedadImagenService;
import com.blue.apartamentos.services.UnidadRentableService;

// Controlador Thymeleaf para las secciones de Disponibilidades e Imágenes en la vista web
@Controller
@RequestMapping("/vista")
public class DisponibilidadImagenVistaController {

    private final DisponibilidadService disponibilidadService;
    private final PropiedadImagenService imagenService;
    private final UnidadRentableService unidadService; // para poblar el selector de unidades en formularios

    public DisponibilidadImagenVistaController(DisponibilidadService disponibilidadService,
                                               PropiedadImagenService imagenService,
                                               UnidadRentableService unidadService) {
        this.disponibilidadService = disponibilidadService;
        this.imagenService = imagenService;
        this.unidadService = unidadService;
    }

    // --- DISPONIBILIDADES ---

    // Muestra el listado de todos los periodos de disponibilidad registrados
    @GetMapping("/disponibilidades")
    public String listarDisponibilidades(Model model) {
        model.addAttribute("disponibilidades", disponibilidadService.obtenerDisponibilidades());
        return "disponibilidades/lista";
    }

    // Abre el formulario en blanco para registrar un nuevo periodo de disponibilidad
    @GetMapping("/disponibilidades/nueva")
    public String formularioDisponibilidad(Model model) {
        model.addAttribute("disponibilidad", new DisponibilidadModel());
        model.addAttribute("unidades", unidadService.obtenerUnidades());
        return "disponibilidades/formulario";
    }

    // Carga el formulario de edición con los datos actuales del periodo indicado
    @GetMapping("/disponibilidades/{id}/editar")
    public String editarDisponibilidad(@PathVariable Long id, Model model, RedirectAttributes flash) {
        return disponibilidadService.obtenerPorId(id)
                .map(disp -> {
                    model.addAttribute("disponibilidad", disp);
                    model.addAttribute("unidades", unidadService.obtenerUnidades());
                    return "disponibilidades/formulario";
                })
                .orElseGet(() -> {
                    flash.addFlashAttribute("msgError", "Disponibilidad no encontrada");
                    return "redirect:/vista/disponibilidades";
                });
    }

    // Guarda o actualiza un periodo de disponibilidad según si ya tiene ID
    @PostMapping("/disponibilidades/guardar")
    public String guardarDisponibilidad(@ModelAttribute DisponibilidadModel disponibilidad, RedirectAttributes flash) {
        try {
            if (disponibilidad.getIdDisponibilidad() != null) {
                disponibilidadService.actualizarDisponibilidad(disponibilidad, disponibilidad.getIdDisponibilidad());
                flash.addFlashAttribute("msgExito", "Periodo de disponibilidad actualizado");
            } else {
                disponibilidadService.guardarDisponibilidad(disponibilidad);
                flash.addFlashAttribute("msgExito", "Periodo de disponibilidad registrado con éxito");
            }
            return "redirect:/vista/disponibilidades";
        } catch (Exception e) {
            flash.addFlashAttribute("msgError", "Error al guardar disponibilidad: " + e.getMessage());
            return "redirect:/vista/disponibilidades/nueva";
        }
    }

    // Elimina un periodo de disponibilidad por ID
    @PostMapping("/disponibilidades/{id}/eliminar")
    public String eliminarDisponibilidad(@PathVariable Long id, RedirectAttributes flash) {
        if (disponibilidadService.eliminarDisponibilidad(id)) {
            flash.addFlashAttribute("msgExito", "Disponibilidad eliminada");
        } else {
            flash.addFlashAttribute("msgError", "No se pudo eliminar la disponibilidad");
        }
        return "redirect:/vista/disponibilidades";
    }

    // --- IMÁGENES ---

    // Muestra la galería de imágenes vinculadas a unidades rentables
    @GetMapping("/imagenes")
    public String listarImagenes(Model model) {
        model.addAttribute("imagenes", imagenService.obtenerImagenes());
        return "imagenes/lista";
    }

    // Abre el formulario para agregar una nueva imagen a una unidad
    @GetMapping("/imagenes/nueva")
    public String formularioImagen(Model model) {
        model.addAttribute("imagen", new PropiedadImagenModel());
        model.addAttribute("unidades", unidadService.obtenerUnidades());
        return "imagenes/formulario";
    }

    // Guarda una nueva imagen vinculada a la unidad seleccionada
    @PostMapping("/imagenes/guardar")
    public String guardarImagen(@ModelAttribute PropiedadImagenModel imagen, RedirectAttributes flash) {
        try {
            imagenService.guardarImagen(imagen);
            flash.addFlashAttribute("msgExito", "Imagen agregada a la unidad rentable");
            return "redirect:/vista/imagenes";
        } catch (Exception e) {
            flash.addFlashAttribute("msgError", "Error al guardar imagen: " + e.getMessage());
            return "redirect:/vista/imagenes/nueva";
        }
    }

    // Elimina una imagen por ID
    @PostMapping("/imagenes/{id}/eliminar")
    public String eliminarImagen(@PathVariable Long id, RedirectAttributes flash) {
        if (imagenService.eliminarImagen(id)) {
            flash.addFlashAttribute("msgExito", "Imagen eliminada");
        } else {
            flash.addFlashAttribute("msgError", "No se pudo eliminar la imagen");
        }
        return "redirect:/vista/imagenes";
    }
}

package com.blue.apartamentos.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.blue.apartamentos.models.ReservacionModel;
import com.blue.apartamentos.services.ReservacionService;
import com.blue.apartamentos.services.UnidadRentableService;
import com.blue.apartamentos.services.UsuarioService;

// Controlador Thymeleaf para la sección de Reservaciones en la vista web
@Controller
@RequestMapping("/vista/reservaciones")
public class ReservacionVistaController {

    private final ReservacionService reservacionService;
    private final UnidadRentableService unidadService; // para el selector de unidades en el formulario
    private final UsuarioService usuarioService;        // para el selector de clientes en el formulario

    public ReservacionVistaController(ReservacionService reservacionService,
                                      UnidadRentableService unidadService,
                                      UsuarioService usuarioService) {
        this.reservacionService = reservacionService;
        this.unidadService = unidadService;
        this.usuarioService = usuarioService;
    }

    // Muestra el listado de todas las reservaciones con sus estados
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("reservaciones", reservacionService.obtenerReservaciones());
        model.addAttribute("estados", ReservacionModel.Estado.values());
        return "reservaciones/lista";
    }

    // Abre el formulario en blanco para crear una nueva reservación
    @GetMapping("/nueva")
    public String formularioNueva(Model model) {
        model.addAttribute("reservacion", new ReservacionModel());
        model.addAttribute("unidades", unidadService.obtenerUnidades());
        model.addAttribute("clientes", usuarioService.obtenerUsuarios());
        return "reservaciones/formulario";
    }

    // Carga el formulario de edición con los datos actuales de la reservación indicada
    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        return reservacionService.obtenerPorId(id)
                .map(r -> {
                    model.addAttribute("reservacion", r);
                    model.addAttribute("unidades", unidadService.obtenerUnidades());
                    model.addAttribute("clientes", usuarioService.obtenerUsuarios());
                    model.addAttribute("estados", ReservacionModel.Estado.values());
                    return "reservaciones/formulario";
                })
                .orElseGet(() -> {
                    flash.addFlashAttribute("msgError", "Reservación no encontrada");
                    return "redirect:/vista/reservaciones";
                });
    }

    // Guarda o actualiza una reservación; recalcula el precio en creación
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute ReservacionModel reservacion, RedirectAttributes flash) {
        try {
            if (reservacion.getIdReservacion() != null) {
                reservacionService.actualizarReservacion(reservacion, reservacion.getIdReservacion());
                flash.addFlashAttribute("msgExito", "Reservación actualizada correctamente");
            } else {
                reservacionService.guardarReservacion(reservacion);
                flash.addFlashAttribute("msgExito", "Reservación creada y calculada exitosamente");
            }
            return "redirect:/vista/reservaciones";
        } catch (Exception e) {
            flash.addFlashAttribute("msgError", "Error al procesar la reservación: " + e.getMessage());
            return reservacion.getIdReservacion() != null
                ? "redirect:/vista/reservaciones/" + reservacion.getIdReservacion() + "/editar"
                : "redirect:/vista/reservaciones/nueva";
        }
    }

    // Cambia el estado de una reservación siguiendo la máquina de estados definida
    @PostMapping("/{id}/cambiar-estado")
    public String cambiarEstado(@PathVariable Long id,
                                @RequestParam ReservacionModel.Estado nuevoEstado,
                                RedirectAttributes flash) {
        try {
            reservacionService.cambiarEstadoReservacion(id, nuevoEstado);
            flash.addFlashAttribute("msgExito", "Estado de la reservación actualizado a: " + nuevoEstado);
        } catch (Exception e) {
            flash.addFlashAttribute("msgError", "Error al cambiar estado: " + e.getMessage());
        }
        return "redirect:/vista/reservaciones";
    }

    // Elimina una reservación por ID; muestra error si tiene pagos asociados
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        if (reservacionService.eliminarReservacion(id)) {
            flash.addFlashAttribute("msgExito", "Reservación eliminada correctamente");
        } else {
            flash.addFlashAttribute("msgError", "No se pudo eliminar la reservación (puede contener pagos asociados)");
        }
        return "redirect:/vista/reservaciones";
    }
}

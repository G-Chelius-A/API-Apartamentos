package com.blue.apartamentos.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.blue.apartamentos.models.MensajeModel;
import com.blue.apartamentos.models.ResenaModel;
import com.blue.apartamentos.services.MensajeService;
import com.blue.apartamentos.services.ResenaService;
import com.blue.apartamentos.services.ReservacionService;
import com.blue.apartamentos.services.UsuarioService;

// Controlador Thymeleaf para las secciones de Reseñas y Mensajes en la vista web
@Controller
@RequestMapping("/vista")
public class ResenaMensajeVistaController {

    private final ResenaService resenaService;
    private final MensajeService mensajeService;
    private final ReservacionService reservacionService; // para poblar el selector de reservaciones
    private final UsuarioService usuarioService;         // para poblar el selector de usuarios en mensajes

    public ResenaMensajeVistaController(ResenaService resenaService,
                                        MensajeService mensajeService,
                                        ReservacionService reservacionService,
                                        UsuarioService usuarioService) {
        this.resenaService = resenaService;
        this.mensajeService = mensajeService;
        this.reservacionService = reservacionService;
        this.usuarioService = usuarioService;
    }

    // --- RESEÑAS ---

    // Muestra el listado de todas las reseñas
    @GetMapping("/resenas")
    public String listarResenas(Model model) {
        model.addAttribute("resenas", resenaService.obtenerResenas());
        return "resenas/lista";
    }

    // Abre el formulario en blanco para registrar una nueva reseña
    @GetMapping("/resenas/nueva")
    public String formularioResena(Model model) {
        model.addAttribute("resena", new ResenaModel());
        model.addAttribute("reservaciones", reservacionService.obtenerReservaciones()); // solo completadas son válidas
        return "resenas/formulario";
    }

    // Guarda una nueva reseña; redirige con error si la reservación no está completada o ya tiene reseña
    @PostMapping("/resenas/guardar")
    public String guardarResena(@ModelAttribute ResenaModel resena, RedirectAttributes flash) {
        try {
            resenaService.guardarResena(resena);
            flash.addFlashAttribute("msgExito", "Reseña registrada exitosamente");
            return "redirect:/vista/resenas";
        } catch (Exception e) {
            flash.addFlashAttribute("msgError", "Error al guardar reseña: " + e.getMessage());
            return "redirect:/vista/resenas/nueva";
        }
    }

    // Elimina una reseña por ID
    @PostMapping("/resenas/{id}/eliminar")
    public String eliminarResena(@PathVariable Long id, RedirectAttributes flash) {
        if (resenaService.eliminarResena(id)) {
            flash.addFlashAttribute("msgExito", "Reseña eliminada");
        } else {
            flash.addFlashAttribute("msgError", "No se pudo eliminar la reseña");
        }
        return "redirect:/vista/resenas";
    }

    // --- MENSAJES ---

    // Muestra el listado de todos los mensajes internos
    @GetMapping("/mensajes")
    public String listarMensajes(Model model) {
        model.addAttribute("mensajes", mensajeService.obtenerMensajes());
        return "mensajes/lista";
    }

    // Abre el formulario para redactar un nuevo mensaje
    @GetMapping("/mensajes/nuevo")
    public String formularioMensaje(Model model) {
        model.addAttribute("mensaje", new MensajeModel());
        model.addAttribute("usuarios", usuarioService.obtenerUsuarios());
        model.addAttribute("reservaciones", reservacionService.obtenerReservaciones());
        return "mensajes/formulario";
    }

    // Envía un nuevo mensaje; establece leído=false si no viene del formulario
    @PostMapping("/mensajes/guardar")
    public String guardarMensaje(@ModelAttribute MensajeModel mensaje, RedirectAttributes flash) {
        try {
            if (mensaje.getLeido() == null) {
                mensaje.setLeido(false); // los mensajes nuevos siempre inician como no leídos
            }
            mensajeService.guardarMensaje(mensaje);
            flash.addFlashAttribute("msgExito", "Mensaje enviado exitosamente");
            return "redirect:/vista/mensajes";
        } catch (Exception e) {
            flash.addFlashAttribute("msgError", "Error al enviar mensaje: " + e.getMessage());
            return "redirect:/vista/mensajes/nuevo";
        }
    }

    // Elimina un mensaje por ID
    @PostMapping("/mensajes/{id}/eliminar")
    public String eliminarMensaje(@PathVariable Long id, RedirectAttributes flash) {
        if (mensajeService.eliminarMensaje(id)) {
            flash.addFlashAttribute("msgExito", "Mensaje eliminado");
        } else {
            flash.addFlashAttribute("msgError", "No se pudo eliminar el mensaje");
        }
        return "redirect:/vista/mensajes";
    }
}

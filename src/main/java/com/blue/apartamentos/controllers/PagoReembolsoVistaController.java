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

import com.blue.apartamentos.models.PagoModel;
import com.blue.apartamentos.models.ReembolsoModel;
import com.blue.apartamentos.services.PagoService;
import com.blue.apartamentos.services.ReembolsoService;
import com.blue.apartamentos.services.ReservacionService;

// Controlador Thymeleaf para las secciones de Pagos y Reembolsos en la vista web
@Controller
@RequestMapping("/vista")
public class PagoReembolsoVistaController {

    private final PagoService pagoService;
    private final ReembolsoService reembolsoService;
    private final ReservacionService reservacionService; // necesario para poblar el selector de reservaciones

    public PagoReembolsoVistaController(PagoService pagoService,
                                        ReembolsoService reembolsoService,
                                        ReservacionService reservacionService) {
        this.pagoService = pagoService;
        this.reembolsoService = reembolsoService;
        this.reservacionService = reservacionService;
    }

    // --- PAGOS ---

    // Muestra el listado completo de pagos registrados
    @GetMapping("/pagos")
    public String listarPagos(Model model) {
        model.addAttribute("pagos", pagoService.obtenerPagos());
        return "pagos/lista";
    }

    // Abre el formulario para registrar un nuevo pago
    @GetMapping("/pagos/nuevo")
    public String formularioPago(Model model) {
        model.addAttribute("pago", new PagoModel());
        model.addAttribute("reservaciones", reservacionService.obtenerReservaciones());
        model.addAttribute("metodos", PagoModel.Metodo.values());   // credito | debito | paypal | transferencia
        model.addAttribute("estados", PagoModel.Estado.values());   // pendiente | aceptado | rechazado | cancelado | reembolso
        return "pagos/formulario";
    }

    // Guarda o actualiza un pago según si ya tiene ID asignado
    @PostMapping("/pagos/guardar")
    public String guardarPago(@ModelAttribute PagoModel pago, RedirectAttributes flash) {
        try {
            if (pago.getIdPago() != null) {
                pagoService.actualizarPago(pago, pago.getIdPago());
                flash.addFlashAttribute("msgExito", "Pago actualizado exitosamente");
            } else {
                pagoService.guardarPago(pago);
                flash.addFlashAttribute("msgExito", "Pago registrado y procesado exitosamente");
            }
            return "redirect:/vista/pagos";
        } catch (Exception e) {
            flash.addFlashAttribute("msgError", "Error al procesar pago: " + e.getMessage());
            return "redirect:/vista/pagos/nuevo";
        }
    }

    // Elimina un pago por ID; muestra error si tiene reembolsos asociados
    @PostMapping("/pagos/{id}/eliminar")
    public String eliminarPago(@PathVariable Long id, RedirectAttributes flash) {
        if (pagoService.eliminarPago(id)) {
            flash.addFlashAttribute("msgExito", "Pago eliminado");
        } else {
            flash.addFlashAttribute("msgError", "No se pudo eliminar el pago (posiblemente tenga reembolsos)");
        }
        return "redirect:/vista/pagos";
    }

    // --- REEMBOLSOS ---

    // Muestra el listado completo de reembolsos registrados
    @GetMapping("/reembolsos")
    public String listarReembolsos(Model model) {
        model.addAttribute("reembolsos", reembolsoService.obtenerReembolsos());
        return "reembolsos/lista";
    }

    // Abre el formulario para solicitar un nuevo reembolso
    @GetMapping("/reembolsos/nuevo")
    public String formularioReembolso(Model model) {
        model.addAttribute("reembolso", new ReembolsoModel());
        model.addAttribute("pagos", pagoService.obtenerPagos()); // solo pagos aceptados serán elegibles
        return "reembolsos/formulario";
    }

    // Guarda una nueva solicitud de reembolso
    @PostMapping("/reembolsos/guardar")
    public String guardarReembolso(@ModelAttribute ReembolsoModel reembolso, RedirectAttributes flash) {
        try {
            reembolsoService.guardarReembolso(reembolso);
            flash.addFlashAttribute("msgExito", "Solicitud de reembolso registrada");
            return "redirect:/vista/reembolsos";
        } catch (Exception e) {
            flash.addFlashAttribute("msgError", "Error al registrar reembolso: " + e.getMessage());
            return "redirect:/vista/reembolsos/nuevo";
        }
    }

    // Cambia el estado de un reembolso (pendiente → aprobado → procesado / rechazado)
    @PostMapping("/reembolsos/{id}/cambiar-estado")
    public String cambiarEstadoReembolso(@PathVariable Long id,
                                        @RequestParam ReembolsoModel.Estado nuevoEstado,
                                        RedirectAttributes flash) {
        try {
            reembolsoService.cambiarEstadoReembolso(id, nuevoEstado);
            flash.addFlashAttribute("msgExito", "Estado del reembolso actualizado a: " + nuevoEstado);
        } catch (Exception e) {
            flash.addFlashAttribute("msgError", "Error al actualizar estado del reembolso: " + e.getMessage());
        }
        return "redirect:/vista/reembolsos";
    }

    // Elimina un reembolso por ID
    @PostMapping("/reembolsos/{id}/eliminar")
    public String eliminarReembolso(@PathVariable Long id, RedirectAttributes flash) {
        if (reembolsoService.eliminarReembolso(id)) {
            flash.addFlashAttribute("msgExito", "Reembolso eliminado correctamente");
        } else {
            flash.addFlashAttribute("msgError", "No se pudo eliminar el reembolso");
        }
        return "redirect:/vista/reembolsos";
    }
}

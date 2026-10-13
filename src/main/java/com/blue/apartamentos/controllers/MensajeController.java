package com.blue.apartamentos.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blue.apartamentos.models.MensajeModel;
import com.blue.apartamentos.services.MensajeService;

// API REST para la mensajería interna entre usuarios del sistema
@RestController
@RequestMapping("/api/mensajes")
public class MensajeController {
    private final MensajeService mensajeService;

    public MensajeController(MensajeService mensajeService) {
        this.mensajeService = mensajeService;
    }

    // GET /api/mensajes — lista todos los mensajes del sistema
    @GetMapping
    public List<MensajeModel> obtenerMensajes() {
        return mensajeService.obtenerMensajes();
    }

    // GET /api/mensajes/{id} — obtiene un mensaje por ID; 404 si no existe
    @GetMapping("/{id}")
    public ResponseEntity<MensajeModel> obtenerPorId(@PathVariable Long id) {
        return mensajeService.obtenerPorId(id).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET /api/mensajes/remitente/{idUsuario} — filtra mensajes enviados por un usuario
    @GetMapping("/remitente/{idUsuario}")
    public List<MensajeModel> obtenerPorRemitente(@PathVariable Long idUsuario) {
        return mensajeService.obtenerPorRemitente(idUsuario);
    }

    // GET /api/mensajes/destinatario/{idUsuario} — filtra mensajes recibidos por un usuario
    @GetMapping("/destinatario/{idUsuario}")
    public List<MensajeModel> obtenerPorDestinatario(@PathVariable Long idUsuario) {
        return mensajeService.obtenerPorDestinatario(idUsuario);
    }

    // GET /api/mensajes/reservacion/{idReservacion} — filtra mensajes de una reservación
    @GetMapping("/reservacion/{idReservacion}")
    public List<MensajeModel> obtenerPorReservacion(@PathVariable Long idReservacion) {
        return mensajeService.obtenerPorReservacion(idReservacion);
    }

    // GET /api/mensajes/fecha-envio?fecha=... — filtra mensajes por fecha/hora exacta de envío
    @GetMapping("/fecha-envio")
    public List<MensajeModel> obtenerPorFechaEnvio(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fecha) {
        return mensajeService.obtenerPorFechaEnvio(fecha);
    }

    // POST /api/mensajes — registra y envía un nuevo mensaje; devuelve 201 Created
    @PostMapping
    public ResponseEntity<MensajeModel> guardar(@RequestBody MensajeModel mensaje) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(mensajeService.guardarMensaje(mensaje));
    }

    // PUT /api/mensajes/{id} — actualiza asunto, contenido y estado de leído
    @PutMapping("/{id}")
    public ResponseEntity<MensajeModel> actualizar(@RequestBody MensajeModel mensaje, @PathVariable Long id) {
        return ResponseEntity.ok(mensajeService.actualizarMensaje(mensaje, id));
    }

    // DELETE /api/mensajes/{id} — elimina un mensaje; 204 si tuvo éxito, 404 si no existe
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return mensajeService.eliminarMensaje(id) ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}

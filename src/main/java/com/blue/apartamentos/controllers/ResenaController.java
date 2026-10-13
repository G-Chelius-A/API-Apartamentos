package com.blue.apartamentos.controllers;

import java.util.List;
import java.time.LocalDateTime;
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
import com.blue.apartamentos.models.ResenaModel;
import com.blue.apartamentos.services.ResenaService;

// API REST para la gestión de reseñas de reservaciones completadas
// Base path: /api — comparte prefijo con unidades y reservaciones para rutas anidadas
@RestController
@RequestMapping("/api")
public class ResenaController {
    private final ResenaService resenaService;

    public ResenaController(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    // GET /api/unidades/{id}/resenas — lista reseñas de una unidad rentable
    // específica
    @GetMapping("/unidades/{id}/resenas")
    public List<ResenaModel> obtenerPorUnidad(@PathVariable Long id) {
        return resenaService.obtenerPorUnidad(id);
    }

    // POST /api/resenas — registra una nueva reseña; devuelve 201 Created
    @PostMapping("/resenas")
    public ResponseEntity<ResenaModel> guardar(@RequestBody ResenaModel resena) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
                .body(resenaService.guardarResena(resena));
    }

    // GET /api/resenas — lista todas las reseñas
    @GetMapping("/resenas")
    public List<ResenaModel> obtenerResenas() {
        return resenaService.obtenerResenas();
    }

    // GET /api/resenas/{id} — obtiene una reseña por ID; 404 si no existe
    @GetMapping("/resenas/{id}")
    public ResponseEntity<ResenaModel> obtenerPorId(@PathVariable Long id) {
        return resenaService.obtenerPorId(id).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET /api/reservaciones/{idReservacion}/resenas — lista reseñas de una
    // reservación
    @GetMapping("/reservaciones/{idReservacion}/resenas")
    public List<ResenaModel> obtenerPorReservacion(@PathVariable Long idReservacion) {
        return resenaService.obtenerPorReservacion(idReservacion);
    }

    // GET /api/usuarios/{idUsuario}/resenas — lista reseñas escritas por un cliente
    @GetMapping("/usuarios/{idUsuario}/resenas")
    public List<ResenaModel> obtenerPorCliente(@PathVariable Long idUsuario) {
        return resenaService.obtenerPorCliente(idUsuario);
    }

    // GET /api/resenas/calificacion/{calificacion} — filtra por calificación
    // general (1-10)
    @GetMapping("/resenas/calificacion/{calificacion}")
    public List<ResenaModel> obtenerPorCalificacion(@PathVariable Integer calificacion) {
        return resenaService.obtenerPorCalificacion(calificacion);
    }

    // GET /api/resenas/fecha?fecha=... — filtra reseñas por fecha/hora exacta
    @GetMapping("/resenas/fecha")
    public List<ResenaModel> obtenerPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha) {
        return resenaService.obtenerPorFecha(fecha);
    }

    // PUT /api/resenas/{id} — actualiza calificaciones y comentario de una reseña
    @PutMapping("/resenas/{id}")
    public ResponseEntity<ResenaModel> actualizar(@RequestBody ResenaModel resena, @PathVariable Long id) {
        return ResponseEntity.ok(resenaService.actualizarResena(resena, id));
    }

    // DELETE /api/resenas/{id} — elimina una reseña; 204 si tuvo éxito, 404 si no
    // existe
    @DeleteMapping("/resenas/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return resenaService.eliminarResena(id) ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
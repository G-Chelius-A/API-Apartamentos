package com.blue.apartamentos.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.blue.apartamentos.models.ReservacionModel;
import com.blue.apartamentos.services.ReservacionService;

// API REST para la gestión de reservaciones con validación de disponibilidad y cálculo de precio
@RestController
@RequestMapping("/api/reservaciones")
public class ReservacionController {
    private final ReservacionService reservacionService;

    public ReservacionController(ReservacionService reservacionService) { this.reservacionService = reservacionService; }

    // GET /api/reservaciones — lista todas las reservaciones
    @GetMapping
    public List<ReservacionModel> obtenerReservaciones() { return reservacionService.obtenerReservaciones(); }

    // GET /api/reservaciones/{id} — obtiene una reservación por ID; 404 si no existe
    @GetMapping("/{id}")
    public ResponseEntity<ReservacionModel> obtenerPorId(@PathVariable Long id) {
        return reservacionService.obtenerPorId(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /api/reservaciones — registra una nueva reservación con validación de disponibilidad y cálculo de precio; devuelve 201 Created
    @PostMapping
    public ResponseEntity<ReservacionModel> guardar(@RequestBody ReservacionModel reservacion) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(reservacionService.guardarReservacion(reservacion));
    }

    // PUT /api/reservaciones/{id} — actualiza fechas, huéspedes y notas de la reservación
    @PutMapping("/{id}")
    public ResponseEntity<ReservacionModel> actualizar(@RequestBody ReservacionModel reservacion, @PathVariable Long id) { return ResponseEntity.ok(reservacionService.actualizarReservacion(reservacion, id)); }

    // DELETE /api/reservaciones/{id} — elimina una reservación; 204 si tuvo éxito, 404 si no existe
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { return reservacionService.eliminarReservacion(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build(); }

    // PATCH /api/reservaciones/{id}/estado — cambia el estado según la máquina de estados definida
    @PatchMapping("/{id}/estado")
    public ResponseEntity<ReservacionModel> cambiarEstado(@PathVariable Long id, @RequestBody ReservacionModel request) { return ResponseEntity.ok(reservacionService.cambiarEstadoReservacion(id, request.getEstado())); }
}
package com.blue.apartamentos.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blue.apartamentos.models.HabitacionModel;
import com.blue.apartamentos.services.HabitacionService;

// API REST para la gestión de habitaciones dentro de casas
@RestController
@RequestMapping("/api/habitaciones")
public class HabitacionController {
    private final HabitacionService habitacionService;

    public HabitacionController(HabitacionService habitacionService) {
        this.habitacionService = habitacionService;
    }

    // GET /api/habitaciones — lista todas las habitaciones
    @GetMapping
    public List<HabitacionModel> obtenerHabitaciones() {
        return habitacionService.obtenerHabitaciones();
    }

    // GET /api/habitaciones/{id} — busca una habitación por ID; 404 si no existe
    @GetMapping("/{id}")
    public ResponseEntity<HabitacionModel> obtenerPorId(@PathVariable Long id) {
        return habitacionService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /api/habitaciones — registra una nueva habitación en una casa; devuelve 201 Created
    @PostMapping
    public ResponseEntity<HabitacionModel> guardar(@RequestBody HabitacionModel habitacion) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(habitacionService.guardarHabitacion(habitacion));
    }

    // PUT /api/habitaciones/{id} — actualiza número, piso, metros y tipo de una habitación
    @PutMapping("/{id}")
    public ResponseEntity<HabitacionModel> actualizar(
            @RequestBody HabitacionModel habitacion, @PathVariable Long id) {
        return ResponseEntity.ok(habitacionService.actualizarHabitacion(habitacion, id));
    }

    // DELETE /api/habitaciones/{id} — elimina una habitación; 204 si tuvo éxito, 404 si no existe
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return habitacionService.eliminarHabitacion(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
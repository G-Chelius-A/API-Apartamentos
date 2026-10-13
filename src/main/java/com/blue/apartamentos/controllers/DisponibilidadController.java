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

import com.blue.apartamentos.models.DisponibilidadModel;
import com.blue.apartamentos.services.DisponibilidadService;

// API REST para gestionar periodos de disponibilidad de unidades rentables
@RestController
@RequestMapping("/api/disponibilidades")
public class DisponibilidadController {
    private final DisponibilidadService disponibilidadService;

    public DisponibilidadController(DisponibilidadService disponibilidadService) {
        this.disponibilidadService = disponibilidadService;
    }

    // GET /api/disponibilidades — lista todos los periodos de disponibilidad
    @GetMapping
    public List<DisponibilidadModel> obtenerDisponibilidades() {
        return disponibilidadService.obtenerDisponibilidades();
    }

    // GET /api/disponibilidades/{id} — busca un periodo por ID; 404 si no existe
    @GetMapping("/{id}")
    public ResponseEntity<DisponibilidadModel> obtenerPorId(@PathVariable Long id) {
        return disponibilidadService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /api/disponibilidades — registra un nuevo periodo; devuelve 201 Created
    @PostMapping
    public ResponseEntity<DisponibilidadModel> guardar(@RequestBody DisponibilidadModel disponibilidad) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(disponibilidadService.guardarDisponibilidad(disponibilidad));
    }

    // PUT /api/disponibilidades/{id} — actualiza fechas y precio especial de un periodo
    @PutMapping("/{id}")
    public ResponseEntity<DisponibilidadModel> actualizar(
            @RequestBody DisponibilidadModel disponibilidad, @PathVariable Long id) {
        return ResponseEntity.ok(disponibilidadService.actualizarDisponibilidad(disponibilidad, id));
    }

    // DELETE /api/disponibilidades/{id} — elimina un periodo; 204 si tuvo éxito, 404 si no existe
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return disponibilidadService.eliminarDisponibilidad(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
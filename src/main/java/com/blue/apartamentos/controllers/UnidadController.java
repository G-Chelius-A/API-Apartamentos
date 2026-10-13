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
import com.blue.apartamentos.models.UnidadRentableModel;
import com.blue.apartamentos.services.UnidadRentableService;

// API REST para la gestión de unidades rentables (apartamentos, casas, habitaciones)
@RestController
@RequestMapping("/api/unidades")
public class UnidadController {
    private final UnidadRentableService unidadService;

    public UnidadController(UnidadRentableService unidadService) { this.unidadService = unidadService; }

    // GET /api/unidades — lista todas las unidades rentables
    @GetMapping
    public List<UnidadRentableModel> obtenerUnidades() { return unidadService.obtenerUnidades(); }

    // GET /api/unidades/{id} — obtiene una unidad por ID; 404 si no existe
    @GetMapping("/{id}")
    public ResponseEntity<UnidadRentableModel> obtenerPorId(@PathVariable Long id) {
        return unidadService.obtenerPorId(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /api/unidades — registra una nueva unidad rentable; devuelve 201 Created
    @PostMapping
    public ResponseEntity<UnidadRentableModel> guardar(@RequestBody UnidadRentableModel unidad) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(unidadService.guardarUnidad(unidad));
    }

    // PUT /api/unidades/{id} — actualiza todos los campos de una unidad existente
    @PutMapping("/{id}")
    public ResponseEntity<UnidadRentableModel> actualizar(@RequestBody UnidadRentableModel unidad, @PathVariable Long id) { return ResponseEntity.ok(unidadService.actualizarUnidad(unidad, id)); }

    // DELETE /api/unidades/{id} — elimina una unidad; 204 si tuvo éxito, 404 si no existe
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { return unidadService.eliminarUnidad(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build(); }
}
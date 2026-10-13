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

import com.blue.apartamentos.models.CasaModel;
import com.blue.apartamentos.services.CasaService;

// API REST para la gestión de casas (datos arquitectónicos de unidades rentables de tipo casa)
@RestController
@RequestMapping("/api/casas")
public class CasaController {
    private final CasaService casaService;

    public CasaController(CasaService casaService) {
        this.casaService = casaService;
    }

    // GET /api/casas — lista todas las casas registradas
    @GetMapping
    public List<CasaModel> obtenerCasas() {
        return casaService.obtenerCasas();
    }

    // GET /api/casas/{id} — obtiene una casa por ID; 404 si no existe
    @GetMapping("/{id}")
    public ResponseEntity<CasaModel> obtenerPorId(@PathVariable Long id) {
        return casaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /api/casas — registra una nueva casa; devuelve 201 Created
    @PostMapping
    public ResponseEntity<CasaModel> guardar(@RequestBody CasaModel casa) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(casaService.guardarCasa(casa));
    }

    // PUT /api/casas/{id} — actualiza los datos arquitectónicos de una casa existente
    @PutMapping("/{id}")
    public ResponseEntity<CasaModel> actualizar(@RequestBody CasaModel casa, @PathVariable Long id) {
        return ResponseEntity.ok(casaService.actualizarCasa(casa, id));
    }

    // DELETE /api/casas/{id} — elimina una casa; 204 si tuvo éxito, 404 si no existe
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return casaService.eliminarCasa(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
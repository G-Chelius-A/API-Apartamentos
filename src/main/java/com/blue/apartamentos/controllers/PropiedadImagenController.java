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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blue.apartamentos.models.PropiedadImagenModel;
import com.blue.apartamentos.services.PropiedadImagenService;

// API REST para gestionar imágenes asociadas a unidades rentables
@RestController
@RequestMapping("/api/imagenes")
public class PropiedadImagenController {
    private final PropiedadImagenService propiedadImagenService;

    public PropiedadImagenController(PropiedadImagenService propiedadImagenService) {
        this.propiedadImagenService = propiedadImagenService;
    }

    // GET /api/imagenes — lista todas las imágenes registradas
    @GetMapping
    public List<PropiedadImagenModel> obtenerImagenes() {
        return propiedadImagenService.obtenerImagenes();
    }

    // GET /api/imagenes/{id} — obtiene una imagen por ID; 404 si no existe
    @GetMapping("/{id}")
    public ResponseEntity<PropiedadImagenModel> obtenerPorId(@PathVariable Long id) {
        return propiedadImagenService.obtenerPorId(id).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET /api/imagenes/unidad/{idUnidad} — lista todas las imágenes de una unidad
    @GetMapping("/unidad/{idUnidad}")
    public List<PropiedadImagenModel> obtenerPorUnidad(@PathVariable Long idUnidad) {
        return propiedadImagenService.obtenerPorUnidad(idUnidad);
    }

    // GET /api/imagenes/principal?esPrincipal=true|false — filtra imágenes por
    // bandera principal
    @GetMapping("/principal")
    public List<PropiedadImagenModel> obtenerPorPrincipal(@RequestParam Boolean esPrincipal) {
        return propiedadImagenService.obtenerPorPrincipal(esPrincipal);
    }

    // POST /api/imagenes — vincula una nueva imagen a una unidad; devuelve 201
    // Created
    @PostMapping
    public ResponseEntity<PropiedadImagenModel> guardar(@RequestBody PropiedadImagenModel imagen) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
                .body(propiedadImagenService.guardarImagen(imagen));
    }

    // PUT /api/imagenes/{id} — actualiza URL, orden y bandera de imagen principal
    @PutMapping("/{id}")
    public ResponseEntity<PropiedadImagenModel> actualizar(@RequestBody PropiedadImagenModel imagen,
            @PathVariable Long id) {
        return ResponseEntity.ok(propiedadImagenService.actualizarImagen(imagen, id));
    }

    // DELETE /api/imagenes/{id} — elimina una imagen; 204 si tuvo éxito, 404 si no
    // existe
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return propiedadImagenService.eliminarImagen(id) ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

}

    // 
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

import com.blue.apartamentos.models.OrganizacionModel;
import com.blue.apartamentos.services.OrganizacionService;

// API REST para la gestión de organizaciones (personas morales / empresas)
@RestController
@RequestMapping("/api/organizaciones")
public class OrganizacionController {
    private final OrganizacionService organizacionService;

    public OrganizacionController(OrganizacionService organizacionService) {
        this.organizacionService = organizacionService;
    }

    // GET /api/organizaciones — lista todas las organizaciones registradas
    @GetMapping
    public List<OrganizacionModel> obtenerOrganizaciones() {
        return organizacionService.obtenerOrganizaciones();
    }

    // GET /api/organizaciones/{id} — obtiene una organización por ID; 404 si no existe
    @GetMapping("/{id}")
    public ResponseEntity<OrganizacionModel> obtenerPorId(@PathVariable Long id) {
        return organizacionService.obtenerPorId(id).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET /api/organizaciones/buscar/razon-social?razonSocial=... — búsqueda por razón social
    @GetMapping("/buscar/razon-social")
    public List<OrganizacionModel> obtenerPorRazonSocial(@RequestParam String razonSocial) {
        return organizacionService.obtenerPorRazonSocial(razonSocial);
    }

    // GET /api/organizaciones/buscar/rfc?rfc=... — búsqueda por RFC
    @GetMapping("/buscar/rfc")
    public List<OrganizacionModel> obtenerPorRfc(@RequestParam String rfc) {
        return organizacionService.obtenerPorRfc(rfc);
    }

    // POST /api/organizaciones — registra una nueva organización; devuelve 201 Created
    @PostMapping
    public ResponseEntity<OrganizacionModel> guardar(@RequestBody OrganizacionModel organizacion) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(organizacionService.guardarOrganizacion(organizacion));
    }

    // PUT /api/organizaciones/{id} — actualiza los datos de una organización existente
    @PutMapping("/{id}")
    public ResponseEntity<OrganizacionModel> actualizar(@RequestBody OrganizacionModel organizacion,
            @PathVariable Long id) {
        return ResponseEntity.ok(organizacionService.actualizarOrganizacion(organizacion, id));
    }

    // DELETE /api/organizaciones/{id} — elimina una organización; 204 si tuvo éxito, 404 si no existe
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return organizacionService.eliminarOrganizacion(id) ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}

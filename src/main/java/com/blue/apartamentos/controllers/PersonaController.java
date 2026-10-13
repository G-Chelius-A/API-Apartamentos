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

import com.blue.apartamentos.models.PersonaModel;
import com.blue.apartamentos.services.PersonaService;

// API REST para la gestión de perfiles de persona física
@RestController
@RequestMapping("/api/personas")
public class PersonaController {
    private final PersonaService personaService;

    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    // GET /api/personas — lista todos los perfiles de persona registrados
    @GetMapping
    public List<PersonaModel> obtenerPersonas() {
        return personaService.obtenerPersonas();
    }

    // GET /api/personas/{id} — obtiene una persona por ID; 404 si no existe
    @GetMapping("/{id}")
    public ResponseEntity<PersonaModel> obtenerPorId(@PathVariable Long id) {
        return personaService.obtenerPorId(id).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET /api/personas/buscar/nombre?nombre=... — búsqueda por nombre
    @GetMapping("/buscar/nombre")
    public List<PersonaModel> obtenerPorNombre(@RequestParam String nombre) {
        return personaService.obtenerPorNombre(nombre);
    }

    // GET /api/personas/buscar/apellido?apellido=... — búsqueda por apellido
    @GetMapping("/buscar/apellido")
    public List<PersonaModel> obtenerPorApellido(@RequestParam String apellido) {
        return personaService.obtenerPorApellido(apellido);
    }

    // GET /api/personas/buscar/documento?documento=... — búsqueda por documento de identidad
    @GetMapping("/buscar/documento")
    public List<PersonaModel> obtenerPorDocumento(@RequestParam String documento) {
        return personaService.obtenerPorDocumento(documento);
    }

    // POST /api/personas — registra un nuevo perfil de persona; devuelve 201 Created
    @PostMapping
    public ResponseEntity<PersonaModel> guardar(@RequestBody PersonaModel persona) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(personaService.guardarPersona(persona));
    }

    // PUT /api/personas/{id} — actualiza los datos de una persona existente
    @PutMapping("/{id}")
    public ResponseEntity<PersonaModel> actualizar(@RequestBody PersonaModel persona, @PathVariable Long id) {
        return ResponseEntity.ok(personaService.actualizarPersona(persona, id));
    }

    // DELETE /api/personas/{id} — elimina un perfil; 204 si tuvo éxito, 404 si no existe
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return personaService.eliminarPersona(id) ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}

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
import com.blue.apartamentos.models.UsuarioModel;
import com.blue.apartamentos.services.UsuarioService;

// API REST para la gestión de cuentas de usuario del sistema
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) { this.usuarioService = usuarioService; }

    // GET /api/usuarios — lista todos los usuarios
    @GetMapping
    public List<UsuarioModel> obtenerUsuarios() { return usuarioService.obtenerUsuarios(); }

    // GET /api/usuarios/{id} — obtiene un usuario por ID; 404 si no existe
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioModel> obtenerPorId(@PathVariable Long id) {
        return usuarioService.obtenerPorId(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /api/usuarios — registra un nuevo usuario; devuelve 201 Created
    @PostMapping
    public ResponseEntity<UsuarioModel> guardar(@RequestBody UsuarioModel usuario) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(usuarioService.guardarUsuario(usuario));
    }

    // PUT /api/usuarios/{id} — actualiza username, email, teléfono y último login
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioModel> actualizar(@RequestBody UsuarioModel usuario, @PathVariable Long id) { return ResponseEntity.ok(usuarioService.actualizarUsuario(usuario, id)); }

    // DELETE /api/usuarios/{id} — elimina un usuario; 204 si tuvo éxito, 404 si no existe
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { return usuarioService.eliminarUsuario(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build(); }
}
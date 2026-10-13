package com.blue.apartamentos.services;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blue.apartamentos.models.UsuarioModel;
import com.blue.apartamentos.repositories.IUsuarioRepository;

// Lógica de negocio para cuentas de usuario del sistema
@Service
public class UsuarioService {
    @Autowired
    private IUsuarioRepository usuarioRepository;

    // Retorna todos los usuarios del sistema
    public ArrayList<UsuarioModel> obtenerUsuarios() { return (ArrayList<UsuarioModel>) usuarioRepository.findAll(); }

    // Guarda un nuevo usuario; valida campos obligatorios y estado
    public UsuarioModel guardarUsuario(UsuarioModel usuario) {
        validarUsuario(usuario);
        return usuarioRepository.save(usuario);
    }

    // Busca un usuario por ID; retorna Optional vacío si no existe
    public Optional<UsuarioModel> obtenerPorId(Long id) { return usuarioRepository.findById(id); }

    // Actualiza username, email, teléfono y último login del usuario
    public UsuarioModel actualizarUsuario(UsuarioModel request, Long id) {
        UsuarioModel usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado con el ID: " + id));
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        usuario.setTelefono(request.getTelefono());
        usuario.setUltimoLogin(request.getUltimoLogin());
        validarUsuario(usuario);
        return usuarioRepository.save(usuario);
    }

    // Valida que los campos obligatorios del usuario estén presentes
    private void validarUsuario(UsuarioModel usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }
        validarTexto(usuario.getUsername(), "El nombre de usuario");
        validarTexto(usuario.getEmail(), "El correo electrónico");
        validarTexto(usuario.getContrasenaHash(), "La contraseña");
        if (usuario.getEstado() == null) {
            throw new IllegalArgumentException("El estado del usuario es obligatorio");
        }
    }

    // Valida que un campo de texto no sea nulo ni vacío
    private void validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " es obligatorio");
        }
    }

    // Actualiza solo el hash de la contraseña sin revalidar otros campos
    public UsuarioModel actualizarContrasena(UsuarioModel request, Long id) {
        UsuarioModel usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado con el ID: " + id));
        usuario.setContrasenaHash(request.getContrasenaHash());
        return usuarioRepository.save(usuario);
    }

    // Actualiza solo el estado del usuario (activo, inactivo, suspendido) sin revalidar otros campos
    public UsuarioModel actualizarEstado(UsuarioModel request, Long id) {
        UsuarioModel usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado con el ID: " + id));
        usuario.setEstado(request.getEstado());
        return usuarioRepository.save(usuario);
    }

    // Elimina un usuario por ID; retorna false si falla (por ejemplo, si tiene propiedades o reservaciones)
    public boolean eliminarUsuario(Long id) {
        try { usuarioRepository.deleteById(id); return true; } catch (Exception e) { return false; }
    }

    // Filtra usuarios por username exacto
    public ArrayList<UsuarioModel> obtenerPorUsername(String username) { return usuarioRepository.findByUsername(username); }
    // Filtra usuarios por email exacto
    public ArrayList<UsuarioModel> obtenerPorEmail(String email) { return usuarioRepository.findByEmail(email); }
}
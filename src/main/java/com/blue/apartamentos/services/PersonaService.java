package com.blue.apartamentos.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blue.apartamentos.models.PersonaModel;
import com.blue.apartamentos.models.UsuarioModel;
import com.blue.apartamentos.repositories.IPersonaRepository;

// Lógica de negocio para perfiles de persona física (vinculados 1:1 a un usuario)
@Service
public class PersonaService {
    @Autowired
    private IPersonaRepository personaRepository;
    @Autowired
    private UsuarioService usuarioService; // para crear el usuario si viene embebido

    // Retorna todos los perfiles de persona física
    public ArrayList<PersonaModel> obtenerPersonas() { return (ArrayList<PersonaModel>) personaRepository.findAll(); }

    // Guarda un perfil de persona; si el usuario viene embebido y sin ID, lo crea primero
    @Transactional
    public PersonaModel guardarPersona(PersonaModel persona) {
        if (persona != null && persona.getIdUsuario() == null && persona.getUsuario() != null) {
            UsuarioModel usuario = usuarioService.guardarUsuario(persona.getUsuario());
            persona.setUsuario(usuario);
            persona.setIdUsuario(usuario.getIdUsuario());
        }
        validarPersona(persona);
        return personaRepository.save(persona);
    }

    // Busca un perfil por ID; retorna Optional vacío si no existe
    public Optional<PersonaModel> obtenerPorId(Long id) { return personaRepository.findById(id); }

    // Actualiza nombre, apellido, fecha de nacimiento, documento de identidad y dirección
    public PersonaModel actualizarPersona(PersonaModel request, Long id) {
        PersonaModel persona = personaRepository.findById(id).orElseThrow(() -> new RuntimeException("Persona no encontrada con el ID: " + id));
        persona.setNombre(request.getNombre());
        persona.setApellido(request.getApellido());
        persona.setFechaNacimiento(request.getFechaNacimiento());
        persona.setDocumentoIdentidad(request.getDocumentoIdentidad());
        persona.setDireccion(request.getDireccion());
        validarPersona(persona);
        return personaRepository.save(persona);
    }

    // Valida campos obligatorios, usuario asociado, fecha de nacimiento (no futura) y mayoría de edad (18+)
    private void validarPersona(PersonaModel persona) {
        if (persona == null) {
            throw new IllegalArgumentException("La persona es obligatoria");
        }
        validarTexto(persona.getNombre(), "El nombre");
        validarTexto(persona.getApellido(), "El apellido");
        validarTexto(persona.getDocumentoIdentidad(), "El documento de identidad");
        validarTexto(persona.getDireccion(), "La dirección");
        if (persona.getIdUsuario() == null) {
            throw new IllegalArgumentException("El usuario de la persona es obligatorio");
        }
        if (persona.getFechaNacimiento() == null || persona.getFechaNacimiento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento debe existir y no puede ser futura");
        }
        if (persona.getFechaNacimiento().plusYears(18).isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La persona debe tener al menos 18 años");
        }
    }

    // Valida que un campo de texto no sea nulo ni vacío
    private void validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " es obligatorio");
        }
    }

    // Elimina un perfil de persona por ID; retorna false si falla
    public boolean eliminarPersona(Long id) {
        try { personaRepository.deleteById(id); return true; } catch (Exception e) { return false; }
    }

    // Filtra perfiles por nombre exacto
    public ArrayList<PersonaModel> obtenerPorNombre(String nombre) { return personaRepository.findByNombre(nombre); }
    // Filtra perfiles por apellido exacto
    public ArrayList<PersonaModel> obtenerPorApellido(String apellido) { return personaRepository.findByApellido(apellido); }
    // Filtra perfiles por documento de identidad exacto
    public ArrayList<PersonaModel> obtenerPorDocumento(String documento) { return personaRepository.findByDocumentoIdentidad(documento); }
}
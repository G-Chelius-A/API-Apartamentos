package com.blue.apartamentos.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blue.apartamentos.models.OrganizacionModel;
import com.blue.apartamentos.models.UsuarioModel;
import com.blue.apartamentos.repositories.IOrganizacionRepository;

// Lógica de negocio para organizaciones (personas morales o empresas vinculadas a un usuario)
@Service
public class OrganizacionService {
    @Autowired
    private IOrganizacionRepository organizacionRepository;
    @Autowired
    private UsuarioService usuarioService; // para crear el usuario si se pasa embebido en la organización

    // Retorna todas las organizaciones registradas
    public ArrayList<OrganizacionModel> obtenerOrganizaciones() { return (ArrayList<OrganizacionModel>) organizacionRepository.findAll(); }

    // Guarda una organización; si el usuario viene embebido y sin ID, lo crea primero en la misma transacción
    @Transactional
    public OrganizacionModel guardarOrganizacion(OrganizacionModel organizacion) {
        if (organizacion != null && organizacion.getIdUsuario() == null && organizacion.getUsuario() != null) {
            UsuarioModel usuario = usuarioService.guardarUsuario(organizacion.getUsuario());
            organizacion.setUsuario(usuario);
            organizacion.setIdUsuario(usuario.getIdUsuario());
        }
        validarOrganizacion(organizacion);
        return organizacionRepository.save(organizacion);
    }

    // Busca una organización por ID; retorna Optional vacío si no existe
    public Optional<OrganizacionModel> obtenerPorId(Long id) { return organizacionRepository.findById(id); }

    // Actualiza razón social, nombre comercial, RFC, dirección fiscal y fecha de constitución
    public OrganizacionModel actualizarOrganizacion(OrganizacionModel request, Long id) {
        OrganizacionModel organizacion = organizacionRepository.findById(id).orElseThrow(() -> new RuntimeException("Organizacion no encontrada con el ID: " + id));
        organizacion.setRazonSocial(request.getRazonSocial());
        organizacion.setNombreComercial(request.getNombreComercial());
        organizacion.setRfc(request.getRfc());
        organizacion.setDireccionFiscal(request.getDireccionFiscal());
        organizacion.setFechaConstitucion(request.getFechaConstitucion());
        validarOrganizacion(organizacion);
        return organizacionRepository.save(organizacion);
    }

    // Valida que todos los campos obligatorios estén presentes y que la fecha de constitución no sea futura
    private void validarOrganizacion(OrganizacionModel organizacion) {
        if (organizacion == null) {
            throw new IllegalArgumentException("La organización es obligatoria");
        }
        validarTexto(organizacion.getRazonSocial(), "La razón social");
        validarTexto(organizacion.getNombreComercial(), "El nombre comercial");
        validarTexto(organizacion.getRfc(), "El RFC");
        validarTexto(organizacion.getDireccionFiscal(), "La dirección fiscal");
        if (organizacion.getIdUsuario() == null) {
            throw new IllegalArgumentException("El usuario de la organización es obligatorio");
        }
        if (organizacion.getFechaConstitucion() == null
                || organizacion.getFechaConstitucion().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de constitución debe existir y no puede ser futura");
        }
    }

    // Valida que un campo de texto no sea nulo ni vacío
    private void validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " es obligatorio");
        }
    }

    // Elimina una organización por ID; retorna false si falla
    public boolean eliminarOrganizacion(Long id) {
        try { organizacionRepository.deleteById(id); return true; } catch (Exception e) { return false; }
    }

    // Filtra organizaciones por razón social exacta
    public ArrayList<OrganizacionModel> obtenerPorRazonSocial(String razonSocial) { return organizacionRepository.findByRazonSocial(razonSocial); }
    // Filtra organizaciones por RFC exacto
    public ArrayList<OrganizacionModel> obtenerPorRfc(String rfc) { return organizacionRepository.findByRfc(rfc); }
}
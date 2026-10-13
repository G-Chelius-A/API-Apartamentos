package com.blue.apartamentos.services;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blue.apartamentos.models.PropiedadImagenModel;
import com.blue.apartamentos.repositories.IPropiedadImagenRepository;

// Lógica de negocio para imágenes asociadas a unidades rentables
@Service
public class PropiedadImagenService {
    @Autowired
    private IPropiedadImagenRepository propiedadImagenRepository;

    // Retorna todas las imágenes registradas
    public ArrayList<PropiedadImagenModel> obtenerImagenes() { return (ArrayList<PropiedadImagenModel>) propiedadImagenRepository.findAll(); }

    // Guarda una nueva imagen; no realiza validaciones adicionales
    public PropiedadImagenModel guardarImagen(PropiedadImagenModel imagen) { return propiedadImagenRepository.save(imagen); }

    // Busca una imagen por ID; retorna Optional vacío si no existe
    public Optional<PropiedadImagenModel> obtenerPorId(Long id) { return propiedadImagenRepository.findById(id); }

    // Actualiza URL, orden de visualización y bandera de imagen principal
    public PropiedadImagenModel actualizarImagen(PropiedadImagenModel request, Long id) {
        PropiedadImagenModel imagen = propiedadImagenRepository.findById(id).orElseThrow(() -> new RuntimeException("Imagen no encontrada con el ID: " + id));
        imagen.setUrlImagen(request.getUrlImagen());
        imagen.setOrden(request.getOrden());
        imagen.setEsPrincipal(request.getEsPrincipal());
        return propiedadImagenRepository.save(imagen);
    }

    // Elimina una imagen por ID; retorna false si falla
    public boolean eliminarImagen(Long id) {
        try { propiedadImagenRepository.deleteById(id); return true; } catch (Exception e) { return false; }
    }

    // Filtra imágenes por unidad rentable
    public ArrayList<PropiedadImagenModel> obtenerPorUnidad(Long idUnidad) { return propiedadImagenRepository.findByUnidad_IdUnidad(idUnidad); }
    // Filtra imágenes por bandera de imagen principal (true = es portada)
    public ArrayList<PropiedadImagenModel> obtenerPorPrincipal(Boolean esPrincipal) { return propiedadImagenRepository.findByEsPrincipal(esPrincipal); }
}
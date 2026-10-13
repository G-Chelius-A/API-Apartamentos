package com.blue.apartamentos.services;

import java.util.ArrayList;
import java.util.Optional;
import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blue.apartamentos.models.CasaModel;
import com.blue.apartamentos.models.HabitacionModel;
import com.blue.apartamentos.models.UnidadRentableModel;
import com.blue.apartamentos.repositories.ICasaRepository;
import com.blue.apartamentos.repositories.IHabitacionRepository;
import com.blue.apartamentos.repositories.IUnidadRentableRepository;

// Lógica de negocio para unidades rentables (apartamentos, casas con sus habitaciones)
// Incluye la validación de publicación, que verifica que la unidad tenga casa, habitaciones y capacidad coherente
@Service
public class UnidadRentableService {
    @Autowired
    private IUnidadRentableRepository unidadRentableRepository;
    @Autowired
    private ICasaRepository casaRepository;         // para validar que la unidad tenga casa registrada
    @Autowired
    private IHabitacionRepository habitacionRepository; // para validar el número y tipo de habitaciones

    // Retorna todas las unidades rentables
    public ArrayList<UnidadRentableModel> obtenerUnidades() { return (ArrayList<UnidadRentableModel>) unidadRentableRepository.findAll(); }

    // Guarda una unidad sin validaciones adicionales (la validación completa se hace al publicar)
    public UnidadRentableModel guardarUnidad(UnidadRentableModel unidad) { return unidadRentableRepository.save(unidad); }

    // Busca una unidad por ID; retorna Optional vacío si no existe
    public Optional<UnidadRentableModel> obtenerPorId(Long id) { return unidadRentableRepository.findById(id); }

    // Verifica si una unidad cumple todos los requisitos de publicación sin lanzar excepción
    public boolean puedePublicarse(Long id) {
        return unidadRentableRepository.findById(id).map(unidad -> {
            try {
                validarPublicacion(unidad);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }).orElse(false);
    }

    // Lanza excepción si la unidad no cumple los requisitos de publicación; usada por DisponibilidadService
    public UnidadRentableModel publicarUnidad(Long id) {
        UnidadRentableModel unidad = unidadRentableRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Unidad no encontrada con el ID: " + id));
        validarPublicacion(unidad);
        return unidad;
    }

    // Valida todos los campos obligatorios, la existencia de la casa, el número de habitaciones
    // y que la capacidad declarada no supere la capacidad máxima real de las habitaciones
    public void validarPublicacion(UnidadRentableModel unidad) {
        if (unidad == null) {
            throw new IllegalArgumentException("La unidad rentable es obligatoria");
        }
        validarTexto(unidad.getTitulo(), "El título");
        validarTexto(unidad.getDescripcion(), "La descripción");
        validarTexto(unidad.getDireccion(), "La dirección");
        validarTexto(unidad.getCiudad(), "La ciudad");
        validarTexto(unidad.getEntidad(), "La entidad");
        validarTexto(unidad.getCodigoPostal(), "El código postal");
        validarTexto(unidad.getPais(), "El país");
        validarTexto(unidad.getComodidades(), "Las comodidades");
        validarTexto(unidad.getNormasUnidad(), "Las normas de la unidad");
        if (unidad.getPropietario() == null || unidad.getPropietario().getIdUsuario() == null) {
            throw new IllegalArgumentException("El propietario es obligatorio");
        }
        if (unidad.getCapacidadPersonas() == null || unidad.getCapacidadPersonas() < 1) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que cero");
        }
        if (unidad.getPetFriendly() == null || unidad.getAccesibilidad() == null) {
            throw new IllegalArgumentException("Debe indicar si acepta mascotas y si tiene accesibilidad");
        }
        if (unidad.getPrecioNoche() == null || unidad.getPrecioNoche().signum() <= 0) {
            throw new IllegalArgumentException("El precio por noche debe ser mayor que cero");
        }
        if (unidad.getIdUnidad() == null) {
            throw new IllegalArgumentException("La unidad debe estar guardada antes de publicarse");
        }

        CasaModel casa = casaRepository.findById(unidad.getIdUnidad())
                .orElseThrow(() -> new IllegalArgumentException("La unidad debe tener una casa registrada"));
        validarPositivo(casa.getNumeroHabitaciones(), "El número de habitaciones");
        validarPositivo(casa.getNumeroPisos(), "El número de pisos");
        validarPositivo(casa.getNumeroBanos(), "El número de baños");
        validarPositivo(casa.getMetrosCuadrados(), "Los metros cuadrados");

        // Verifica que el número de habitaciones reales coincida con el declarado en la casa
        var habitaciones = habitacionRepository.findByCasa_IdCasa(casa.getIdCasa());
        if (habitaciones.size() != casa.getNumeroHabitaciones()) {
            throw new IllegalArgumentException("La casa debe tener exactamente el número de habitaciones declarado");
        }
        // Calcula la capacidad máxima sumando la capacidad de cada tipo de habitación
        int capacidadMaxima = habitaciones.stream()
                .mapToInt(habitacion -> capacidadMaxima(habitacion.getTipoHabitacion()))
                .sum();
        if (unidad.getCapacidadPersonas() > capacidadMaxima) {
            throw new IllegalArgumentException("La capacidad de la unidad supera la capacidad máxima de sus habitaciones");
        }
    }

    // Valida que un campo de texto no sea nulo ni vacío
    private void validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " es obligatorio");
        }
    }

    // Valida que un valor entero sea positivo (>= 1)
    private void validarPositivo(Integer valor, String campo) {
        if (valor == null || valor < 1) {
            throw new IllegalArgumentException(campo + " debe ser mayor que cero");
        }
    }

    // Valida que un valor decimal sea positivo (> 0)
    private void validarPositivo(BigDecimal valor, String campo) {
        if (valor == null || valor.signum() <= 0) {
            throw new IllegalArgumentException(campo + " deben ser mayores que cero");
        }
    }

    // Retorna la capacidad máxima de personas por tipo de habitación
    // individual=1, matrimonial=2, familiar=4
    private int capacidadMaxima(HabitacionModel.TipoHabitacion tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de habitación es obligatorio");
        }
        return switch (tipo) {
            case individual -> 1;
            case matrimonial -> 2;
            case familiar -> 4;
        };
    }

    // Actualiza todos los campos descriptivos y de precio de una unidad
    public UnidadRentableModel actualizarUnidad(UnidadRentableModel request, Long id) {
        UnidadRentableModel unidad = unidadRentableRepository.findById(id).orElseThrow(() -> new RuntimeException("Unidad no encontrada con el ID: " + id));
        unidad.setTitulo(request.getTitulo());
        unidad.setDescripcion(request.getDescripcion());
        unidad.setDireccion(request.getDireccion());
        unidad.setCiudad(request.getCiudad());
        unidad.setEntidad(request.getEntidad());
        unidad.setCodigoPostal(request.getCodigoPostal());
        unidad.setPais(request.getPais());
        unidad.setCapacidadPersonas(request.getCapacidadPersonas());
        unidad.setComodidades(request.getComodidades());
        unidad.setNormasUnidad(request.getNormasUnidad());
        unidad.setPetFriendly(request.getPetFriendly());
        unidad.setAccesibilidad(request.getAccesibilidad());
        unidad.setPrecioNoche(request.getPrecioNoche());
        return unidadRentableRepository.save(unidad);
    }

    // Elimina una unidad por ID; retorna false si falla (por ejemplo, si tiene reservaciones asociadas)
    public boolean eliminarUnidad(Long id) {
        try { unidadRentableRepository.deleteById(id); return true; } catch (Exception e) { return false; }
    }

    // Filtra unidades por título exacto
    public ArrayList<UnidadRentableModel> obtenerPorTitulo(String titulo) { return unidadRentableRepository.findByTitulo(titulo); }
    // Filtra unidades por ciudad
    public ArrayList<UnidadRentableModel> obtenerPorCiudad(String ciudad) { return unidadRentableRepository.findByCiudad(ciudad); }
    // Filtra unidades por país
    public ArrayList<UnidadRentableModel> obtenerPorPais(String pais) { return unidadRentableRepository.findByPais(pais); }
}
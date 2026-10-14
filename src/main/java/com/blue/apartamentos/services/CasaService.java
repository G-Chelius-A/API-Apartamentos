package com.blue.apartamentos.services;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blue.apartamentos.models.CasaModel;
import com.blue.apartamentos.repositories.ICasaRepository;
import com.blue.apartamentos.repositories.IUnidadRentableRepository;

// Lógica de negocio para casas (datos arquitectónicos de una unidad rentable de tipo casa)
@Service
public class CasaService {
    @Autowired
    private ICasaRepository casaRepository;
    @Autowired
    private IUnidadRentableRepository unidadRentableRepository; // para resolver/crear la unidad asociada

    // Retorna todas las casas registradas en el sistema con su unidad cargada
    public ArrayList<CasaModel> obtenerCasas() {
        return casaRepository.findAllWithUnidad();
    }

    // Guarda una nueva casa; si la unidad no tiene ID la crea, si ya existe la busca y valida
    // La casa hereda el mismo ID de la unidad rentable (relación 1:1 PK compartida)
    @org.springframework.transaction.annotation.Transactional
    public CasaModel guardarCasa(CasaModel casa) {
        if (casa == null || casa.getUnidad() == null) {
            throw new IllegalArgumentException("La unidad rentable es obligatoria");
        }

        com.blue.apartamentos.models.UnidadRentableModel unidad = casa.getUnidad();
        if (unidad.getIdUnidad() == null) {
            unidad = unidadRentableRepository.save(unidad);
        } else {
            unidad = unidadRentableRepository.findById(unidad.getIdUnidad())
                    .orElseThrow(() -> new IllegalArgumentException("La unidad rentable no existe"));
        }

        casa.setUnidad(unidad);
        casa.setIdCasa(unidad.getIdUnidad()); // el ID de la casa == ID de la unidad
        return casaRepository.save(casa);
    }

    // Busca una casa por ID; retorna Optional vacío si no existe
    public Optional<CasaModel> obtenerPorId(Long id) {
        return casaRepository.findById(id);
    }

    // Actualiza solo los campos arquitectónicos de la casa (habitaciones, pisos, baños, m²)
    public CasaModel actualizarCasa(CasaModel request, Long id) {
        CasaModel casa = casaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Casa no encontrada con el ID: " + id));
        casa.setNumeroHabitaciones(request.getNumeroHabitaciones());
        casa.setNumeroPisos(request.getNumeroPisos());
        casa.setNumeroBanos(request.getNumeroBanos());
        casa.setMetrosCuadrados(request.getMetrosCuadrados());
        return casaRepository.save(casa);
    }

    // Elimina una casa por ID; retorna false si falla (por ejemplo, si tiene habitaciones asociadas)
    public boolean eliminarCasa(Long id) {
        try {
            casaRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Filtra casas por número de habitaciones exacto
    public ArrayList<CasaModel> obtenerPorNumeroHabitaciones(Integer valor) {
        return casaRepository.findByNumeroHabitaciones(valor);
    }

    // Filtra casas por número de pisos exacto
    public ArrayList<CasaModel> obtenerPorNumeroPisos(Integer valor) {
        return casaRepository.findByNumeroPisos(valor);
    }
}
package com.blue.apartamentos.services;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blue.apartamentos.models.HabitacionModel;
import com.blue.apartamentos.repositories.ICasaRepository;
import com.blue.apartamentos.repositories.IHabitacionRepository;

// Lógica de negocio para habitaciones dentro de casas
@Service
public class HabitacionService {
    @Autowired
    private IHabitacionRepository habitacionRepository;
    @Autowired
    private ICasaRepository casaRepository; // para verificar que la casa exista antes de guardar

    // Retorna todas las habitaciones registradas
    public ArrayList<HabitacionModel> obtenerHabitaciones() { return (ArrayList<HabitacionModel>) habitacionRepository.findAll(); }

    // Guarda una habitación; valida que la casa asociada exista en BD
    public HabitacionModel guardarHabitacion(HabitacionModel habitacion) {
        if (habitacion == null || habitacion.getCasa() == null || habitacion.getCasa().getIdCasa() == null) {
            throw new IllegalArgumentException("La casa es obligatoria");
        }
        var casa = casaRepository.findById(habitacion.getCasa().getIdCasa())
                .orElseThrow(() -> new IllegalArgumentException("La casa no existe"));
        habitacion.setCasa(casa);
        return habitacionRepository.save(habitacion);
    }

    // Busca una habitación por ID; retorna Optional vacío si no existe
    public Optional<HabitacionModel> obtenerPorId(Long id) { return habitacionRepository.findById(id); }

    // Actualiza número, piso, metros cuadrados y tipo de la habitación
    public HabitacionModel actualizarHabitacion(HabitacionModel request, Long id) {
        HabitacionModel habitacion = habitacionRepository.findById(id).orElseThrow(() -> new RuntimeException("Habitacion no encontrada con el ID: " + id));
        habitacion.setNumero(request.getNumero());
        habitacion.setPiso(request.getPiso());
        habitacion.setMetrosCuadrados(request.getMetrosCuadrados());
        habitacion.setTipoHabitacion(request.getTipoHabitacion());
        return habitacionRepository.save(habitacion);
    }

    // Elimina una habitación por ID; retorna false si falla
    public boolean eliminarHabitacion(Long id) {
        try { habitacionRepository.deleteById(id); return true; } catch (Exception e) { return false; }
    }

    // Filtra por número de habitación
    public ArrayList<HabitacionModel> obtenerPorNumero(Integer numero) { return habitacionRepository.findByNumero(numero); }
    // Filtra por piso
    public ArrayList<HabitacionModel> obtenerPorPiso(Integer piso) { return habitacionRepository.findByPiso(piso); }
    // Filtra por tipo: individual, matrimonial o familiar
    public ArrayList<HabitacionModel> obtenerPorTipo(HabitacionModel.TipoHabitacion tipo) { return habitacionRepository.findByTipoHabitacion(tipo); }
}
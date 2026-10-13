package com.blue.apartamentos.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blue.apartamentos.models.DisponibilidadModel;
import com.blue.apartamentos.repositories.IDisponibilidadRepository;

// Lógica de negocio para periodos de disponibilidad de unidades rentables
@Service
public class DisponibilidadService {
    @Autowired
    private IDisponibilidadRepository disponibilidadRepository;
    @Autowired
    private UnidadRentableService unidadRentableService; // se usa para validar que la unidad pueda publicarse

    // Retorna todos los periodos de disponibilidad
    public ArrayList<DisponibilidadModel> obtenerDisponibilidades() { return (ArrayList<DisponibilidadModel>) disponibilidadRepository.findAll(); }

    // Guarda un nuevo periodo; valida la unidad, las fechas y el precio especial
    public DisponibilidadModel guardarDisponibilidad(DisponibilidadModel disponibilidad) {
        prepararDisponibilidad(disponibilidad);
        return disponibilidadRepository.save(disponibilidad);
    }

    // Busca un periodo por ID; retorna Optional vacío si no existe
    public Optional<DisponibilidadModel> obtenerPorId(Long id) { return disponibilidadRepository.findById(id); }

    // Actualiza la unidad, fechas y precio especial de un periodo existente
    public DisponibilidadModel actualizarDisponibilidad(DisponibilidadModel request, Long id) {
        DisponibilidadModel disponibilidad = disponibilidadRepository.findById(id).orElseThrow(() -> new RuntimeException("Disponibilidad no encontrada con el ID: " + id));
        if (request.getUnidad() != null) {
            disponibilidad.setUnidad(request.getUnidad());
        }
        disponibilidad.setFechaInicio(request.getFechaInicio());
        disponibilidad.setFechaFin(request.getFechaFin());
        disponibilidad.setPrecioEspecial(request.getPrecioEspecial());
        prepararDisponibilidad(disponibilidad);
        return disponibilidadRepository.save(disponibilidad);
    }

    // Valida que la unidad exista y sea publicable, que las fechas sean coherentes
    // y que el precio especial (si se indica) sea positivo. Marca disponible=true si no se especifica.
    private void prepararDisponibilidad(DisponibilidadModel disponibilidad) {
        if (disponibilidad == null || disponibilidad.getUnidad() == null
                || disponibilidad.getUnidad().getIdUnidad() == null) {
            throw new IllegalArgumentException("La unidad rentable es obligatoria");
        }
        unidadRentableService.publicarUnidad(disponibilidad.getUnidad().getIdUnidad());
        LocalDate fechaInicio = disponibilidad.getFechaInicio();
        LocalDate fechaFin = disponibilidad.getFechaFin();
        if (fechaInicio == null || fechaFin == null || !fechaInicio.isBefore(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin");
        }
        if (disponibilidad.getDisponible() == null) {
            disponibilidad.setDisponible(true);
        }
        BigDecimal precioEspecial = disponibilidad.getPrecioEspecial();
        if (precioEspecial != null && precioEspecial.signum() <= 0) {
            throw new IllegalArgumentException("El precio especial debe ser mayor que cero");
        }
    }

    // Elimina un periodo por ID; retorna false si falla
    public boolean eliminarDisponibilidad(Long id) {
        try { disponibilidadRepository.deleteById(id); return true; } catch (Exception e) { return false; }
    }

    // Filtra periodos por unidad rentable
    public ArrayList<DisponibilidadModel> obtenerPorUnidad(Long idUnidad) { return disponibilidadRepository.findByUnidad_IdUnidad(idUnidad); }
    // Filtra por fecha de inicio exacta
    public ArrayList<DisponibilidadModel> obtenerPorFechaInicio(LocalDate fecha) { return disponibilidadRepository.findByFechaInicio(fecha); }
    // Filtra por precio especial exacto
    public ArrayList<DisponibilidadModel> obtenerPorPrecioEspecial(BigDecimal precio) { return disponibilidadRepository.findByPrecioEspecial(precio); }
}
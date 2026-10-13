package com.blue.apartamentos.repositories;

import java.time.LocalDate;
import java.util.ArrayList;
import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blue.apartamentos.models.DisponibilidadModel;

@Repository
public interface IDisponibilidadRepository extends JpaRepository<DisponibilidadModel, Long> {
    ArrayList<DisponibilidadModel> findByUnidad_IdUnidad(Long idUnidad);
    ArrayList<DisponibilidadModel> findByFechaInicio(LocalDate fechaInicio);
    ArrayList<DisponibilidadModel> findByPrecioEspecial(BigDecimal precioEspecial);
}

package com.blue.apartamentos.repositories;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blue.apartamentos.models.ResenaModel;

@Repository
public interface IResenaRepository extends JpaRepository<ResenaModel, Long> {
    ArrayList<ResenaModel> findByReservacion_IdReservacion(Long idReservacion);
    ArrayList<ResenaModel> findByReservacion_Propiedad_IdUnidad(Long idUnidad);
    ArrayList<ResenaModel> findByReservacion_Cliente_IdUsuario(Long idUsuario);
    ArrayList<ResenaModel> findByCalificacionGeneral(Integer calificacionGeneral);
    ArrayList<ResenaModel> findByFechaResena(LocalDateTime fechaResena);
}

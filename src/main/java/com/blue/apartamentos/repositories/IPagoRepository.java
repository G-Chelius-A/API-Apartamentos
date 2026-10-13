package com.blue.apartamentos.repositories;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blue.apartamentos.models.PagoModel;

@Repository
public interface IPagoRepository extends JpaRepository<PagoModel, Long> {
    ArrayList<PagoModel> findByReservacion_IdReservacion(Long idReservacion);
    ArrayList<PagoModel> findByReservacion_Cliente_IdUsuario(Long idUsuario);
    ArrayList<PagoModel> findByFechaPago(LocalDateTime fechaPago);
    ArrayList<PagoModel> findByEstado(PagoModel.Estado estado);
    ArrayList<PagoModel> findByReferenciaPago(String referenciaPago);
}

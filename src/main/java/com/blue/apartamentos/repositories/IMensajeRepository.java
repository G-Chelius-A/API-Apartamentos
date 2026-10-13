package com.blue.apartamentos.repositories;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blue.apartamentos.models.MensajeModel;

@Repository
public interface IMensajeRepository extends JpaRepository<MensajeModel, Long> {
    ArrayList<MensajeModel> findByRemitente_IdUsuario(Long idUsuario);
    ArrayList<MensajeModel> findByDestinatario_IdUsuario(Long idUsuario);
    ArrayList<MensajeModel> findByReservacion_IdReservacion(Long idReservacion);
    ArrayList<MensajeModel> findByFechaEnvio(LocalDateTime fechaEnvio);
}

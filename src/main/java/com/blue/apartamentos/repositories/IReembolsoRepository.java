package com.blue.apartamentos.repositories;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blue.apartamentos.models.ReembolsoModel;

@Repository
public interface IReembolsoRepository extends JpaRepository<ReembolsoModel, Long> {
    ArrayList<ReembolsoModel> findByPago_IdPago(Long idPago);
    ArrayList<ReembolsoModel> findByPago_Reservacion_Cliente_IdUsuario(Long idUsuario);
    ArrayList<ReembolsoModel> findByFechaReembolso(LocalDateTime fechaReembolso);
    ArrayList<ReembolsoModel> findByMontoReembolso(BigDecimal montoReembolso);
}

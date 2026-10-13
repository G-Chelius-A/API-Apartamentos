package com.blue.apartamentos.repositories;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.blue.apartamentos.models.ReservacionModel;

@Repository
public interface IReservacionRepository extends JpaRepository<ReservacionModel, Long> {
    ArrayList<ReservacionModel> findByCliente_IdUsuario(Long idUsuario);
    ArrayList<ReservacionModel> findByPropiedad_IdUnidad(Long idUnidad);
    ArrayList<ReservacionModel> findByFechaEntrada(LocalDate fechaEntrada);
    ArrayList<ReservacionModel> findByEstado(ReservacionModel.Estado estado);
    long countByEstado(ReservacionModel.Estado estado);
    ArrayList<ReservacionModel> findByCodigoReserva(String codigoReserva);

        @Query("""
                select case when count(r) > 0 then true else false end
                from ReservacionModel r
                where r.propiedad.idUnidad = :idUnidad
                    and r.estado in :estados
                    and r.fechaEntrada < :fechaSalida
                    and r.fechaSalida > :fechaEntrada
                    and (:idReservacion is null or r.idReservacion <> :idReservacion)
                """)
        boolean existeTraslape(
                        @Param("idUnidad") Long idUnidad,
                        @Param("estados") Collection<ReservacionModel.Estado> estados,
                        @Param("fechaEntrada") LocalDate fechaEntrada,
                        @Param("fechaSalida") LocalDate fechaSalida,
                        @Param("idReservacion") Long idReservacion);
}

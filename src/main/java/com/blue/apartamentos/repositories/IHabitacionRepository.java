package com.blue.apartamentos.repositories;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blue.apartamentos.models.HabitacionModel;

@Repository
public interface IHabitacionRepository extends JpaRepository<HabitacionModel, Long> {
    @org.springframework.data.jpa.repository.Query("SELECT h FROM HabitacionModel h JOIN FETCH h.casa c LEFT JOIN FETCH c.unidad")
    ArrayList<HabitacionModel> findAllWithCasaAndUnidad();

    ArrayList<HabitacionModel> findByCasa_IdCasa(Long idCasa);
    ArrayList<HabitacionModel> findByNumero(Integer numero);
    ArrayList<HabitacionModel> findByPiso(Integer piso);
    ArrayList<HabitacionModel> findByTipoHabitacion(HabitacionModel.TipoHabitacion tipoHabitacion);
}

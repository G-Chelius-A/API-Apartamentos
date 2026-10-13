package com.blue.apartamentos.repositories;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blue.apartamentos.models.CasaModel;

@Repository
public interface ICasaRepository extends JpaRepository<CasaModel, Long> {
    ArrayList<CasaModel> findByNumeroHabitaciones(Integer numeroHabitaciones);
    ArrayList<CasaModel> findByNumeroPisos(Integer numeroPisos);
}

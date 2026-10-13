package com.blue.apartamentos.repositories;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blue.apartamentos.models.UnidadRentableModel;

@Repository
public interface IUnidadRentableRepository extends JpaRepository<UnidadRentableModel, Long> {
    ArrayList<UnidadRentableModel> findByTitulo(String titulo);
    ArrayList<UnidadRentableModel> findByCiudad(String ciudad);
    ArrayList<UnidadRentableModel> findByPais(String pais);
}

package com.blue.apartamentos.repositories;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blue.apartamentos.models.PersonaModel;

@Repository
public interface IPersonaRepository extends JpaRepository<PersonaModel, Long> {
    ArrayList<PersonaModel> findByNombre(String nombre);
    ArrayList<PersonaModel> findByApellido(String apellido);
    ArrayList<PersonaModel> findByDocumentoIdentidad(String documentoIdentidad);
}

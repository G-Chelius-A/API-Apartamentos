package com.blue.apartamentos.repositories;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blue.apartamentos.models.UsuarioModel;

@Repository
public interface IUsuarioRepository extends JpaRepository<UsuarioModel, Long> {
    ArrayList<UsuarioModel> findByUsername(String username);
    ArrayList<UsuarioModel> findByEmail(String email);
}

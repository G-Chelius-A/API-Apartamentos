package com.blue.apartamentos.repositories;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blue.apartamentos.models.OrganizacionModel;

@Repository
public interface IOrganizacionRepository extends JpaRepository<OrganizacionModel, Long> {
    ArrayList<OrganizacionModel> findByRazonSocial(String razonSocial);
    ArrayList<OrganizacionModel> findByRfc(String rfc);
}

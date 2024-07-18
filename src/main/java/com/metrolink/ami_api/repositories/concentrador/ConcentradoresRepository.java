package com.metrolink.ami_api.repositories.concentrador;

import com.metrolink.ami_api.models.concentrador.Concentradores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConcentradoresRepository extends JpaRepository<Concentradores, String> {
}

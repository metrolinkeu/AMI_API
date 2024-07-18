package com.metrolink.ami_api.repositories.medidor;

import com.metrolink.ami_api.models.medidor.Medidores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedidoresRepository extends JpaRepository<Medidores, String> {
}

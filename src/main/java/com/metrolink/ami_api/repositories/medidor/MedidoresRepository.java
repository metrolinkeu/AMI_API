package com.metrolink.ami_api.repositories.medidor;

import com.metrolink.ami_api.models.medidor.Medidores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedidoresRepository extends JpaRepository<Medidores, String> {
    List<Medidores> findByConcentradorVcnoSerie(String vcnoSerie);

    List<Medidores> findByVcsic(String vcsic);
}

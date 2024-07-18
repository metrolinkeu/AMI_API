package com.metrolink.ami_api.repositories.medidor;

import com.metrolink.ami_api.models.medidor.TipoParamAdvMed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoParamAdvMedRepository extends JpaRepository<TipoParamAdvMed, Long> {
}

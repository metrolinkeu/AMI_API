package com.metrolink.ami_api.repositories.procesos.ejecucionesLecturas;

import com.metrolink.ami_api.models.procesos.ejecucionesLecturas.EjecucionesLecturas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EjecucionesLecturasRepository extends JpaRepository<EjecucionesLecturas, Long> {
}
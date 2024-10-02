package com.metrolink.ami_api.repositories.procesos.ejecucionesLecturas;

import com.metrolink.ami_api.models.procesos.ejecucionesLecturas.EjecucionesLecturas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface EjecucionesLecturasRepository extends JpaRepository<EjecucionesLecturas, Long> {    
    // Consulta para obtener la última ejecución basada en la descripción de la programación
    @Query("SELECT el FROM EjecucionesLecturas el JOIN el.ejecucionLecturaProg ep WHERE ep.vcdescripcionProg = :descripcion ORDER BY el.nidEjecucionLectura DESC")
    List<EjecucionesLecturas> findLastByDescripcionProg(@Param("descripcion") String descripcion, Pageable pageable);
}
package com.metrolink.ami_api.repositories.procesos.programacionesAmi;

import com.metrolink.ami_api.models.procesos.programacionesAmi.AgendaProgramacionesAMI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgendaProgramacionesAMIRepository extends JpaRepository<AgendaProgramacionesAMI, Long> {
}

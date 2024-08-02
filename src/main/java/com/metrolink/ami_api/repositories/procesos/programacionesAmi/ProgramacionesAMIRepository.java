package com.metrolink.ami_api.repositories.procesos.programacionesAmi;

import com.metrolink.ami_api.models.procesos.programacionesAmi.ProgramacionesAMI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramacionesAMIRepository extends JpaRepository<ProgramacionesAMI, Long> {
}

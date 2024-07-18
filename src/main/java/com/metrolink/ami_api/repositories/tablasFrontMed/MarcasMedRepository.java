package com.metrolink.ami_api.repositories.tablasFrontMed;

import com.metrolink.ami_api.models.tablasFrontMed.MarcasMed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarcasMedRepository extends JpaRepository<MarcasMed, Long> {
}

package com.metrolink.ami_api.repositories.tablasFront;

import com.metrolink.ami_api.models.tablasFront.NodeBytesdeDireccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeBytesdeDireccionRepository extends JpaRepository<NodeBytesdeDireccion, Long> {
}

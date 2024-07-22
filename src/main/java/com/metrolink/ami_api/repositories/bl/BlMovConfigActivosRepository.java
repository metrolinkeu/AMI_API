package com.metrolink.ami_api.repositories.bl;

import com.metrolink.ami_api.models.bl.BlMovConfigActivos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlMovConfigActivosRepository extends JpaRepository<BlMovConfigActivos, Integer> {
}

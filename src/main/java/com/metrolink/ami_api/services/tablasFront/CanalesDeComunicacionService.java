package com.metrolink.ami_api.services.tablasFront;

import com.metrolink.ami_api.models.tablasFront.CanalesDeComunicacion;
import com.metrolink.ami_api.repositories.tablasFront.CanalesDeComunicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
public class CanalesDeComunicacionService {

    @Autowired
    private CanalesDeComunicacionRepository canalesDeComunicacionRepository;

    @Transactional
    public CanalesDeComunicacion save(CanalesDeComunicacion canal, boolean isUpdate) {
        Optional<CanalesDeComunicacion> existingCanal = canalesDeComunicacionRepository.findById(canal.getNcodigo());
        if (existingCanal.isPresent() && !isUpdate) {
            throw new IllegalArgumentException("CanalDeComunicacion with ncodigo " + canal.getNcodigo() + " already exists.");
        }
        return canalesDeComunicacionRepository.save(canal);
    }

    public List<CanalesDeComunicacion> findAll() {
        return canalesDeComunicacionRepository.findAll();
    }

    public CanalesDeComunicacion findById(Long id) {
        return canalesDeComunicacionRepository.findById(id).orElseThrow(() -> new RuntimeException("CanalDeComunicacion not found"));
    }

    public CanalesDeComunicacion update(Long id, CanalesDeComunicacion canalDetails) {
        CanalesDeComunicacion canal = findById(id);
        canal.setVccanalDeComunicacion(canalDetails.getVccanalDeComunicacion());
        canal.setVcconcat(canalDetails.getVcconcat());
        return canalesDeComunicacionRepository.save(canal);
    }

    public void deleteById(Long id) {
        canalesDeComunicacionRepository.deleteById(id);
    }
}

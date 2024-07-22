package com.metrolink.ami_api.services.bl;

import com.metrolink.ami_api.models.bl.BlMovConfigActivos;
import com.metrolink.ami_api.repositories.bl.BlMovConfigActivosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
public class BlMovConfigActivosService {

    @Autowired
    private BlMovConfigActivosRepository configActivosRepository;

    @Transactional
    public BlMovConfigActivos save(BlMovConfigActivos configActivo, boolean isUpdate) {
        Optional<BlMovConfigActivos> existingConfigActivo = configActivosRepository.findById(configActivo.getIdConfiguracionActivo());
        if (existingConfigActivo.isPresent() && !isUpdate) {
            throw new IllegalArgumentException("ConfigActivo with idConfiguracionActivo " + configActivo.getIdConfiguracionActivo() + " already exists.");
        }
        return configActivosRepository.save(configActivo);
    }

    public List<BlMovConfigActivos> findAll() {
        return configActivosRepository.findAll();
    }

    public BlMovConfigActivos findById(Integer id) {
        return configActivosRepository.findById(id).orElseThrow(() -> new RuntimeException("ConfigActivo not found"));
    }

    public BlMovConfigActivos update(Integer id, BlMovConfigActivos configActivoDetails) {
        BlMovConfigActivos configActivo = findById(id);
        configActivo.setTipoActivo(configActivoDetails.getTipoActivo());
        configActivo.setVcNombre(configActivoDetails.getVcNombre());
        configActivo.setVcDescripcion(configActivoDetails.getVcDescripcion());
        configActivo.setVcZona(configActivoDetails.getVcZona());
        configActivo.setVcSubzona(configActivoDetails.getVcSubzona());
        configActivo.setVcSubestacion(configActivoDetails.getVcSubestacion());
        configActivo.setVcBarra(configActivoDetails.getVcBarra());
        configActivo.setNivelTension(configActivoDetails.getNivelTension());
        configActivo.setClaseTransformador(configActivoDetails.getClaseTransformador());
        configActivo.setVcCapacidadMaxima(configActivoDetails.getVcCapacidadMaxima());
        configActivo.setVcNivelTensionPrimario(configActivoDetails.getVcNivelTensionPrimario());
        configActivo.setVcNivelTensionSecundario(configActivoDetails.getVcNivelTensionSecundario());
        configActivo.setVcNivelTensionTerciario(configActivoDetails.getVcNivelTensionTerciario());
        return configActivosRepository.save(configActivo);
    }

    public void deleteById(Integer id) {
        configActivosRepository.deleteById(id);
    }
}

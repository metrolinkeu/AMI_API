package com.metrolink.ami_api.services.tablasFrontMed;

import com.metrolink.ami_api.models.tablasFrontMed.ViasObtencionDatos;
import com.metrolink.ami_api.repositories.tablasFrontMed.ViasObtencionDatosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
public class ViasObtencionDatosService {

    @Autowired
    private ViasObtencionDatosRepository viasObtencionDatosRepository;

    @Transactional
    public ViasObtencionDatos save(ViasObtencionDatos viaObtencionDatos, boolean isUpdate) {
        Optional<ViasObtencionDatos> existingViaObtencionDatos = viasObtencionDatosRepository.findById(viaObtencionDatos.getNcodigo());
        if (existingViaObtencionDatos.isPresent() && !isUpdate) {
            throw new IllegalArgumentException("ViasObtencionDatos with ncodigo " + viaObtencionDatos.getNcodigo() + " already exists.");
        }
        return viasObtencionDatosRepository.save(viaObtencionDatos);
    }

    public List<ViasObtencionDatos> findAll() {
        return viasObtencionDatosRepository.findAll();
    }

    public ViasObtencionDatos findById(Long id) {
        return viasObtencionDatosRepository.findById(id).orElseThrow(() -> new RuntimeException("ViaObtencionDatos not found"));
    }

    public ViasObtencionDatos update(Long id, ViasObtencionDatos viaObtencionDatosDetails) {
        ViasObtencionDatos viaObtencionDatos = findById(id);
        viaObtencionDatos.setVcviaObtencionDatos(viaObtencionDatosDetails.getVcviaObtencionDatos());
        viaObtencionDatos.setVcconcat(viaObtencionDatosDetails.getVcconcat());
        return viasObtencionDatosRepository.save(viaObtencionDatos);
    }

    public void deleteById(Long id) {
        viasObtencionDatosRepository.deleteById(id);
    }
}

package com.metrolink.ami_api.services.tablasFront;


import com.metrolink.ami_api.models.tablasFront.Estados;
import com.metrolink.ami_api.repositories.tablasFront.EstadosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
public class EstadosService {

    @Autowired
    private EstadosRepository estadosRepository;

    @Transactional
    public Estados save(Estados estado, boolean isUpdate) {
        Optional<Estados> existingEstado = estadosRepository.findById(estado.getNcodigo());
        if (existingEstado.isPresent()&& !isUpdate) {
            throw new IllegalArgumentException("Estados with ncodigo " + estado.getNcodigo() + " already exists.");
        }
        return estadosRepository.save(estado);
    }

 
    public List<Estados> findAll() {
        return estadosRepository.findAll();
    }

    public Estados findById(Long id) {
        return estadosRepository.findById(id).orElseThrow(() -> new RuntimeException("Estado not found"));
    }

    public Estados update(Long id, Estados estadoDetails) {
        Estados estado = findById(id);
        estado.setVcestado(estadoDetails.getVcestado());
        estado.setVcconcat(estadoDetails.getVcconcat());
        return estadosRepository.save(estado);
    }

    public void deleteById(Long id) {
        estadosRepository.deleteById(id);
    }
}

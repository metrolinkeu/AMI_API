package com.metrolink.ami_api.services.tablasFront;

import com.metrolink.ami_api.models.tablasFront.Marcas;
import com.metrolink.ami_api.repositories.tablasFront.MarcasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
public class MarcasService {

    @Autowired
    private MarcasRepository marcasRepository;

    @Transactional
    public Marcas save(Marcas marca, boolean isUpdate) {
        // Verificar si ya existe una marca con el mismo ncodigo
        Optional<Marcas> existingMarca = marcasRepository.findById(marca.getNcodigo());
        if (existingMarca.isPresent() && !isUpdate) {
            throw new IllegalArgumentException("Marcas with ncodigo " + marca.getNcodigo() + " already exists.");
        }
        return marcasRepository.save(marca);
    }

    public List<Marcas> findAll() {
        return marcasRepository.findAll();
    }

    public Marcas findById(Long id) {
        return marcasRepository.findById(id).orElseThrow(() -> new RuntimeException("Marca not found"));
    }

    public Marcas update(Long id, Marcas marcaDetails) {
        Marcas marca = findById(id);
        marca.setVcmarca(marcaDetails.getVcmarca());
        marca.setVcconcat(marcaDetails.getVcconcat());
        return marcasRepository.save(marca);
    }

    public void deleteById(Long id) {
        marcasRepository.deleteById(id);
    }
}

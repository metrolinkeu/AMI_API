package com.metrolink.ami_api.services.tablasFrontMed;

import com.metrolink.ami_api.models.tablasFrontMed.TiposDeModuloDeCom;
import com.metrolink.ami_api.repositories.tablasFrontMed.TiposDeModuloDeComRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
public class TiposDeModuloDeComService {

    @Autowired
    private TiposDeModuloDeComRepository tiposDeModuloDeComRepository;

    @Transactional
    public TiposDeModuloDeCom save(TiposDeModuloDeCom tiposDeModuloDeCom, boolean isUpdate) {
        Optional<TiposDeModuloDeCom> existingTiposDeModuloDeCom = tiposDeModuloDeComRepository.findById(tiposDeModuloDeCom.getNcodigo());
        if (existingTiposDeModuloDeCom.isPresent() && !isUpdate) {
            throw new IllegalArgumentException("TiposDeModuloDeCom with ncodigo " + tiposDeModuloDeCom.getNcodigo() + " already exists.");
        }
        return tiposDeModuloDeComRepository.save(tiposDeModuloDeCom);
    }

    public List<TiposDeModuloDeCom> findAll() {
        return tiposDeModuloDeComRepository.findAll();
    }

    public TiposDeModuloDeCom findById(Long id) {
        return tiposDeModuloDeComRepository.findById(id).orElseThrow(() -> new RuntimeException("TiposDeModuloDeCom not found"));
    }

    public TiposDeModuloDeCom update(Long id, TiposDeModuloDeCom tiposDeModuloDeComDetails) {
        TiposDeModuloDeCom tiposDeModuloDeCom = findById(id);
        tiposDeModuloDeCom.setVctiposDeModuloDeCom(tiposDeModuloDeComDetails.getVctiposDeModuloDeCom());
        tiposDeModuloDeCom.setVcconcat(tiposDeModuloDeComDetails.getVcconcat());
        return tiposDeModuloDeComRepository.save(tiposDeModuloDeCom);
    }

    public void deleteById(Long id) {
        tiposDeModuloDeComRepository.deleteById(id);
    }
}

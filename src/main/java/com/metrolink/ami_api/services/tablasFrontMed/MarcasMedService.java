package com.metrolink.ami_api.services.tablasFrontMed;

import com.metrolink.ami_api.models.tablasFrontMed.MarcasMed;
import com.metrolink.ami_api.repositories.tablasFrontMed.MarcasMedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
public class MarcasMedService {

    @Autowired
    private MarcasMedRepository marcasMedRepository;

    @Transactional
    public MarcasMed save(MarcasMed marcaMed, boolean isUpdate) {
        Optional<MarcasMed> existingMarcaMed = marcasMedRepository.findById(marcaMed.getNcodigo());
        if (existingMarcaMed.isPresent() && !isUpdate) {
            throw new IllegalArgumentException("MarcasMed with ncodigo " + marcaMed.getNcodigo() + " already exists.");
        }
        return marcasMedRepository.save(marcaMed);
    }

    public List<MarcasMed> findAll() {
        return marcasMedRepository.findAll();
    }

    public MarcasMed findById(Long id) {
        return marcasMedRepository.findById(id).orElseThrow(() -> new RuntimeException("MarcaMed not found"));
    }

    public MarcasMed update(Long id, MarcasMed marcaMedDetails) {
        MarcasMed marcaMed = findById(id);
        marcaMed.setVcmarcaMed(marcaMedDetails.getVcmarcaMed());
        marcaMed.setVcconcat(marcaMedDetails.getVcconcat());
        return marcasMedRepository.save(marcaMed);
    }

    public void deleteById(Long id) {
        marcasMedRepository.deleteById(id);
    }
}

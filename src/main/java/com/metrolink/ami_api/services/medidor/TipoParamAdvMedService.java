package com.metrolink.ami_api.services.medidor;

import com.metrolink.ami_api.models.medidor.TipoParamAdvMed;
import com.metrolink.ami_api.repositories.medidor.TipoParamAdvMedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoParamAdvMedService {

    @Autowired
    private TipoParamAdvMedRepository tipoParamAdvMedRepository;

    public List<TipoParamAdvMed> findAll() {
        return tipoParamAdvMedRepository.findAll();
    }

    public TipoParamAdvMed findById(Long ncod) {
        return tipoParamAdvMedRepository.findById(ncod)
                .orElseThrow(() -> new RuntimeException("TipoParamAdvMed not found"));
    }
}

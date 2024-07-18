package com.metrolink.ami_api.services.concentrador;

import com.metrolink.ami_api.models.concentrador.TipoParamAdvCon;
import com.metrolink.ami_api.repositories.concentrador.TipoParamAdvConRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoParamAdvConService {

    @Autowired
    private TipoParamAdvConRepository tipoParamAdvConRepository;

    public List<TipoParamAdvCon> findAll() {
        return tipoParamAdvConRepository.findAll();
    }

    public TipoParamAdvCon findById(Long ncod) {
        return tipoParamAdvConRepository.findById(ncod)
                .orElseThrow(() -> new RuntimeException("TipoParamAdvCon not found"));
    }
}

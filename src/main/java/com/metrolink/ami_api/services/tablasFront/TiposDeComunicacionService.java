package com.metrolink.ami_api.services.tablasFront;

import com.metrolink.ami_api.models.tablasFront.TiposDeComunicacion;
import com.metrolink.ami_api.repositories.tablasFront.TiposDeComunicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
public class TiposDeComunicacionService {

    @Autowired
    private TiposDeComunicacionRepository tiposDeComunicacionRepository;

    @Transactional
    public TiposDeComunicacion save(TiposDeComunicacion tipo, boolean isUpdate) {
        Optional<TiposDeComunicacion> existingTipo = tiposDeComunicacionRepository.findById(tipo.getNcodigo());
        if (existingTipo.isPresent() && !isUpdate) {
            throw new IllegalArgumentException("TiposDeComunicacion with ncodigo " + tipo.getNcodigo() + " already exists.");
        }
        return tiposDeComunicacionRepository.save(tipo);
    }

    public List<TiposDeComunicacion> findAll() {
        return tiposDeComunicacionRepository.findAll();
    }

    public TiposDeComunicacion findById(Long id) {
        return tiposDeComunicacionRepository.findById(id).orElseThrow(() -> new RuntimeException("TiposDeComunicacion not found"));
    }

    public TiposDeComunicacion update(Long id, TiposDeComunicacion tipoDetails) {
        TiposDeComunicacion tipo = findById(id);
        tipo.setVctiposDeComunicacion(tipoDetails.getVctiposDeComunicacion());
        tipo.setVcconcat(tipoDetails.getVcconcat());
        return tiposDeComunicacionRepository.save(tipo);
    }

    public void deleteById(Long id) {
        tiposDeComunicacionRepository.deleteById(id);
    }
}

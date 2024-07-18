package com.metrolink.ami_api.services.concentrador;

import com.metrolink.ami_api.models.concentrador.Concentradores;
import com.metrolink.ami_api.repositories.concentrador.ConcentradoresRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
public class ConcentradoresService {

    @Autowired
    private ConcentradoresRepository concentradoresRepository;

    @Transactional
    public Concentradores save(Concentradores concentrador, boolean isUpdate) {

        Optional<Concentradores> existingConcentrador = concentradoresRepository.findById(concentrador.getVcnoSerie());
        if (existingConcentrador.isPresent() && !isUpdate) {
            throw new IllegalArgumentException(
                    "Concentrador with vcnoSerie " + concentrador.getVcnoSerie() + " already exists.");
        }
        System.out.println("Saving Concentrador: " + concentrador);
        return concentradoresRepository.save(concentrador);
    }

    public List<Concentradores> findAll() {
        return concentradoresRepository.findAll();
    }

    public Concentradores findById(String vcnoSerie) {
        return concentradoresRepository.findById(vcnoSerie)
                .orElseThrow(() -> new RuntimeException("Concentrador not found"));
    }

    public Concentradores update(String vcnoSerie, Concentradores concentradorDetails) {
        Concentradores concentrador = findById(vcnoSerie);
        concentrador.setVcdescripcion(concentradorDetails.getVcdescripcion());
        concentrador.setMarca(concentradorDetails.getMarca());
        concentrador.setEmpresa(concentradorDetails.getEmpresa());
        concentrador.setVccodigoCaja(concentradorDetails.getVccodigoCaja());
        concentrador.setVclongitudLatitud(concentradorDetails.getVclongitudLatitud());
        concentrador.setVcfechaInstalacion(concentradorDetails.getVcfechaInstalacion());
        concentrador.setEstado(concentradorDetails.getEstado());
        concentrador.setCanalDeComunicacion(concentradorDetails.getCanalDeComunicacion());
        concentrador.setParamTiposDeComunicacion(concentradorDetails.getParamTiposDeComunicacion());
        concentrador.setConfiguracionProtocolo(concentradorDetails.getConfiguracionProtocolo());
        concentrador.setParamAdvCon(concentradorDetails.getParamAdvCon());
        return concentradoresRepository.save(concentrador);
    }

    public void deleteById(String vcnoSerie) {
        concentradoresRepository.deleteById(vcnoSerie);
    }
}

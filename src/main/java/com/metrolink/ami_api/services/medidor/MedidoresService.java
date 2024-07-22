package com.metrolink.ami_api.services.medidor;

import com.metrolink.ami_api.models.medidor.Medidores;
import com.metrolink.ami_api.repositories.medidor.MedidoresRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
public class MedidoresService {

    @Autowired
    private MedidoresRepository medidoresRepository;

    @Transactional
    public Medidores save(Medidores medidor, boolean isUpdate) {
        Optional<Medidores> existingMedidor = medidoresRepository.findById(medidor.getVcSerie());
        if (existingMedidor.isPresent() && !isUpdate) {
            throw new IllegalArgumentException("Medidores with vcSerie " + medidor.getVcSerie() + " already exists.");
        }
        return medidoresRepository.save(medidor);
    }

    public List<Medidores> findAll() {
        return medidoresRepository.findAll();
    }

    public Medidores findById(String vcSerie) {
        return medidoresRepository.findById(vcSerie).orElseThrow(() -> new RuntimeException("Medidor not found"));
    }

    public Medidores update(String vcSerie, Medidores medidorDetails) {
        Medidores medidor = findById(vcSerie);
        medidor.setVcidCliente(medidorDetails.getVcidCliente());
        medidor.setVcdescripcion(medidorDetails.getVcdescripcion());
        medidor.setMarcaMed(medidorDetails.getMarcaMed());
        medidor.setLisMacro(medidorDetails.isLisMacro());
        medidor.setVclongitudLatitud(medidorDetails.getVclongitudLatitud());
        medidor.setVcfechaInstalacion(medidorDetails.getVcfechaInstalacion());
        medidor.setEstado(medidorDetails.getEstado());
        medidor.setVcfechaHoraUltimaLectura(medidorDetails.getVcfechaHoraUltimaLectura());
        medidor.setVcperiodoIntegracion(medidorDetails.getVcperiodoIntegracion());
        medidor.setVcultimoEstadoRele(medidorDetails.getVcultimoEstadoRele());
        medidor.setVcfirmware(medidorDetails.getVcfirmware());
        medidor.setViaObtencionDatos(medidorDetails.getViaObtencionDatos());
        medidor.setConcentrador(medidorDetails.getConcentrador());
        medidor.setCanalDeComunicacion(medidorDetails.getCanalDeComunicacion());
        medidor.setVcip(medidorDetails.getVcip());
        medidor.setVcpuerto(medidorDetails.getVcpuerto());
        medidor.setTipoDeModuloDeCom(medidorDetails.getTipoDeModuloDeCom());
        medidor.setConfiguracionProtocolo(medidorDetails.getConfiguracionProtocolo());
        medidor.setParamAdvMed(medidorDetails.getParamAdvMed());
        medidor.setConfiguracionActivo(medidorDetails.getConfiguracionActivo());
        return medidoresRepository.save(medidor);
    }

    public void deleteById(String vcSerie) {
        medidoresRepository.deleteById(vcSerie);
    }
}

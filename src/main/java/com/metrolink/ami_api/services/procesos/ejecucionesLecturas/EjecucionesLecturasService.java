package com.metrolink.ami_api.services.procesos.ejecucionesLecturas;

import com.metrolink.ami_api.models.procesos.ejecucionesLecturas.EjecucionesLecturas;
import com.metrolink.ami_api.repositories.procesos.ejecucionesLecturas.EjecucionesLecturasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class EjecucionesLecturasService {

    @Autowired
    private EjecucionesLecturasRepository ejecucionesLecturasRepository;

    @Transactional
    public EjecucionesLecturas save(EjecucionesLecturas ejecucion, boolean isUpdate) {
        Optional<EjecucionesLecturas> existingEjecucion = ejecucionesLecturasRepository.findById(ejecucion.getIdEjecucionLectura());
        if (existingEjecucion.isPresent() && !isUpdate) {
            throw new IllegalArgumentException("Ejecucion with id " + ejecucion.getIdEjecucionLectura() + " already exists.");
        }
        return ejecucionesLecturasRepository.save(ejecucion);
    }

    public List<EjecucionesLecturas> findAll() {
        return ejecucionesLecturasRepository.findAll();
    }

    public EjecucionesLecturas findById(Long id) {
        return ejecucionesLecturasRepository.findById(id).orElseThrow(() -> new RuntimeException("Ejecucion not found"));
    }

    public EjecucionesLecturas update(Long id, EjecucionesLecturas ejecucionDetails) {
        EjecucionesLecturas ejecucion = findById(id);
        ejecucion.setIdAnteriorIntentoEjecucionLectura(ejecucionDetails.getIdAnteriorIntentoEjecucionLectura());
        ejecucion.setDinicioEjecucionLectura(ejecucionDetails.getDinicioEjecucionLectura());
        ejecucion.setDFinEjecucionLectura(ejecucionDetails.getDFinEjecucionLectura());
        ejecucion.setNIntentoLecturaNumero(ejecucionDetails.getNIntentoLecturaNumero());
        ejecucion.setProgramacionAMI(ejecucionDetails.getProgramacionAMI());
        ejecucion.setEjecucionLecturaDetect(ejecucionDetails.getEjecucionLecturaDetect());
        return ejecucionesLecturasRepository.save(ejecucion);
    }

    public void deleteById(Long id) {
        ejecucionesLecturasRepository.deleteById(id);
    }
}
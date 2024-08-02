package com.metrolink.ami_api.services.procesos.programacionesAmi;

import com.metrolink.ami_api.models.procesos.programacionesAmi.ProgramacionesAMI;
import com.metrolink.ami_api.repositories.procesos.programacionesAmi.ProgramacionesAMIRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
public class ProgramacionesAMIService {

    @Autowired
    private ProgramacionesAMIRepository programacionesAMIRepository;

    @Transactional
    public ProgramacionesAMI save(ProgramacionesAMI programacionAMI, boolean isUpdate) {
        Optional<ProgramacionesAMI> existingProgramacionAMI = programacionesAMIRepository.findById(programacionAMI.getNcodigo());
        if (existingProgramacionAMI.isPresent() && !isUpdate) {
            throw new IllegalArgumentException("ProgramacionAMI with ncodigo " + programacionAMI.getNcodigo() + " already exists.");
        }
        return programacionesAMIRepository.save(programacionAMI);
    }

    public List<ProgramacionesAMI> findAll() {
        return programacionesAMIRepository.findAll();
    }

    public ProgramacionesAMI findById(Long id) {
        return programacionesAMIRepository.findById(id).orElseThrow(() -> new RuntimeException("ProgramacionAMI not found"));
    }

    public ProgramacionesAMI update(Long id, ProgramacionesAMI programacionAMIDetails) {
        ProgramacionesAMI programacionAMI = findById(id);
        programacionAMI.setVcestado(programacionAMIDetails.getVcestado());
        programacionAMI.setGrupoMedidores(programacionAMIDetails.getGrupoMedidores());
        programacionAMI.setListaPeticiones(programacionAMIDetails.getListaPeticiones());
        programacionAMI.setParametrizacionProg(programacionAMIDetails.getParametrizacionProg());
        return programacionesAMIRepository.save(programacionAMI);
    }

    public void deleteById(Long id) {
        programacionesAMIRepository.deleteById(id);
    }
}

package com.metrolink.ami_api.services.procesos.programacionesAmi;

import com.metrolink.ami_api.models.procesos.programacionesAmi.AgendaProgramacionesAMI;
import com.metrolink.ami_api.repositories.procesos.programacionesAmi.AgendaProgramacionesAMIRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class AgendaProgramacionesAMIService {

    @Autowired
    private AgendaProgramacionesAMIRepository agendaProgramacionesAMIRepository;

    @Autowired
    private AsignacionAgendaAMedidoresService asignacionAgendaAMedidores;

    @Transactional
    public AgendaProgramacionesAMI save(AgendaProgramacionesAMI agendaProgramacionesAMI, boolean isUpdate) {
        if (!isUpdate && agendaProgramacionesAMI.getNcodigo() != null) {
            throw new IllegalArgumentException("ID should be null for new entities.");
        }

        // Guardar la agenda en la base de datos
        AgendaProgramacionesAMI agenda = agendaProgramacionesAMIRepository.save(agendaProgramacionesAMI);

        // Verificar y procesar la agenda mediante AsignacionAgendaAMedidores
        asignacionAgendaAMedidores.verificarYProcesar(agenda);

        return agenda;
    }

    public List<AgendaProgramacionesAMI> findAll() {
        return agendaProgramacionesAMIRepository.findAll();
    }

    public AgendaProgramacionesAMI findById(Long id) {
        return agendaProgramacionesAMIRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AgendaProgramacionesAMI not found"));
    }

    public AgendaProgramacionesAMI update(Long id, AgendaProgramacionesAMI agendaProgramacionesAMIDetails) {
        AgendaProgramacionesAMI agendaProgramacionesAMI = findById(id);
        agendaProgramacionesAMI.setProgramacionAMI(agendaProgramacionesAMIDetails.getProgramacionAMI());
        agendaProgramacionesAMI.setEstadoHoy(agendaProgramacionesAMIDetails.getEstadoHoy());
        return agendaProgramacionesAMIRepository.save(agendaProgramacionesAMI);
    }

    public void deleteById(Long id) {

        AgendaProgramacionesAMI agenda = agendaProgramacionesAMIRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AgendaProgramacionesAMI not found with id " + id));

        asignacionAgendaAMedidores.verificarYRemover(agenda); // Remueve de los medidores la agenda que se va a eliminar

        agendaProgramacionesAMIRepository.deleteById(id);
    }
}

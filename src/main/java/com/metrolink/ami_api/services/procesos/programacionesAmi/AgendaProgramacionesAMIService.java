package com.metrolink.ami_api.services.procesos.programacionesAmi;

import com.metrolink.ami_api.models.procesos.programacionesAmi.AgendaProgramacionesAMI;
import com.metrolink.ami_api.models.procesos.programacionesAmi.ProgramacionesAMI;
import com.metrolink.ami_api.repositories.procesos.programacionesAmi.AgendaProgramacionesAMIRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import java.util.List;

@Service
public class AgendaProgramacionesAMIService {

    @Autowired
    private AgendaProgramacionesAMIRepository agendaProgramacionesAMIRepository;

    @Autowired
    private AsignacionAgendaAMedidoresService asignacionAgendaAMedidores;

    @Autowired
    private ProgramacionesAMIService programacionesAMIService;

    @Autowired
    private ProgramacionHandlerService programacionHandler;

    @Transactional
    public AgendaProgramacionesAMI save(AgendaProgramacionesAMI agendaProgramacionesAMI, boolean isUpdate) {
        if (!isUpdate && agendaProgramacionesAMI.getNcodigo() != null) {
            throw new IllegalArgumentException("ID should be null for new entities.");
        }

        // Intentar guardar la agenda en la base de datos
        AgendaProgramacionesAMI agenda;
        try {
            agenda = agendaProgramacionesAMIRepository.save(agendaProgramacionesAMI);
        } catch (Exception e) {
            // Si hay una excepción al guardar, manejarla aquí
            System.err.println("Error al guardar la agenda: " + e.getMessage());
            return null; // Devuelve null o lanza una excepción personalizada si prefieres
        }

        // Verificar si el guardado fue exitoso
        if (agenda == null) {
            System.out.println("Fallo en la creación de la agenda.");
            return null; // O lanza una excepción personalizada
        }

        // Verificar y procesar la agenda mediante AsignacionAgendaAMedidores; esto
        // relacionla la agenda con cada medidor incluido en la programacion
        asignacionAgendaAMedidores.verificarYProcesar(agenda);

        // Inicio de proceso de puesta en marcha de la programación de acuerdo a
        // cualquiera de los doce casos
        ProgramacionesAMI programacionAMI = programacionesAMIService.findById(agenda.getProgramacionAMI().getNcodigo());

        // Usar la  clase para manejar la lógica de los casos
        programacionHandler.manejarProgramacion(programacionAMI);

   

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

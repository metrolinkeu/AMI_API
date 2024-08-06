package com.metrolink.ami_api.services.procesos.programacionesAmi;

import com.metrolink.ami_api.models.medidor.Medidores;
import com.metrolink.ami_api.models.procesos.programacionesAmi.AgendaProgramacionesAMI;
import com.metrolink.ami_api.models.procesos.programacionesAmi.ProgramacionesAMI;
import com.metrolink.ami_api.services.medidor.MedidoresService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AsignacionAgendaAMedidoresService {

    @Autowired
    private ProgramacionesAMIService programacionesAMIService;

    @Autowired
    private MedidoresService medidoresService;

    public void verificarYProcesar(AgendaProgramacionesAMI agenda) {
        // Verificar que la agenda se haya guardado correctamente
        if (agenda == null || agenda.getNcodigo() == null) {
            throw new RuntimeException("Error al guardar la agenda. La operación de guardado no fue exitosa.");
        }

        if (agenda.getProgramacionAMI() == null || agenda.getProgramacionAMI().getNcodigo() == null) {
            throw new IllegalArgumentException("Programación AMI no puede ser nulo.");
        }

        // Buscar la programación AMI asociada
        ProgramacionesAMI programacionAMI = programacionesAMIService.findById(agenda.getProgramacionAMI().getNcodigo());
        if (programacionAMI == null) {
            throw new IllegalArgumentException("Programación AMI no encontrada.");
        }

        // Verificar si el grupo de medidores y las series de medidores existen
        if (programacionAMI.getGrupoMedidores() == null
                || programacionAMI.getGrupoMedidores().getVcSeriesMed() == null) {
            throw new IllegalArgumentException("Grupo de Medidores o Series de Medidores no encontrados.");
        }

        String seriesmed = programacionAMI.getGrupoMedidores().getVcSeriesMed();

        // Dividir la cadena por comas para obtener cada número de serie individualmente
        String[] serialNumbers = seriesmed.split(",");

        // Recorrer el arreglo resultante y verificar cada número de serie
        for (String serial : serialNumbers) {
            System.out.println("Número de serie: " + serial);
            try {
                Medidores medidor = medidoresService.findById(serial);
                if (medidor != null) {
                    System.out.println("Medidor encontrado: " + medidor.getVcSerie());

                    medidor.setEnAgendaProgramacionesAMI(agenda);
                    medidor.setEstadoEnAgenda("pendiente");




                } else {
                    System.out.println("Medidor no encontrado para el número de serie: " + serial);
                }
            } catch (RuntimeException e) {
                System.out.println("Medidor no encontrado para el número de serie: " + serial);
            }
        }
    }
}

package com.metrolink.ami_api.services.procesos.programacionesAmi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metrolink.ami_api.models.medidor.Medidores;
import com.metrolink.ami_api.services.medidor.MedidorUtils;

import com.metrolink.ami_api.models.procesos.programacionesAmi.AgendaProgramacionesAMI;
import com.metrolink.ami_api.models.procesos.programacionesAmi.ProgramacionesAMI;
import com.metrolink.ami_api.services.medidor.MedidoresService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AsignacionAgendaAMedidoresService {

    @Autowired
    private ProgramacionesAMIService programacionesAMIService;

    @Autowired
    private MedidoresService medidoresService;

    public void verificarYProcesar(AgendaProgramacionesAMI agenda) {

        String seriesmed = "";

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
        if (programacionAMI.getGrupoMedidores() != null) {
            if ("Concentrador".equals(programacionAMI.getGrupoMedidores().getVcfiltro())) {

                List<Medidores> medidores = medidoresService
                        .findByConcentradorVcnoSerie(programacionAMI.getGrupoMedidores().getVcidentificador());
                seriesmed = MedidorUtils.obtenerStringDeVcSerie(medidores);

            } else if ("ConcentradorYmedidores".equals(programacionAMI.getGrupoMedidores().getVcfiltro())) {// aqui
                                                                                                            // falta
                                                                                                            // indicar
                                                                                                            // que
                                                                                                            // tienen
                                                                                                            // una
                                                                                                            // relacon
                                                                                                            // con el
                                                                                                            // medidor

                seriesmed = programacionAMI.getGrupoMedidores().getJsseriesMed();

            } else if ("Medidores".equals(programacionAMI.getGrupoMedidores().getVcfiltro())) {
                seriesmed = programacionAMI.getGrupoMedidores().getJsseriesMed();

            } else if ("Frontera SIC".equals(programacionAMI.getGrupoMedidores().getVcfiltro())) {
                List<Medidores> medidores = medidoresService
                        .findByVcsic(programacionAMI.getGrupoMedidores().getVcidentificador());
                seriesmed = MedidorUtils.obtenerStringDeVcSerie(medidores);

            } else {
                throw new IllegalArgumentException("Filtro no encontrado.");
            }

        }

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Convertir el String JSON a un array de Strings
            String[] serialNumbers = objectMapper.readValue(seriesmed, String[].class);

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
        } catch (JsonProcessingException e) {
            // Manejar la excepción
            e.printStackTrace();
        }
    }

    public void verificarYRemover(AgendaProgramacionesAMI agenda) {

        String seriesmed = agenda.getProgramacionAMI().getGrupoMedidores().getJsseriesMed();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Convertir el String JSON a un array de Strings
            String[] serialNumbers = objectMapper.readValue(seriesmed, String[].class);

            // Recorrer el arreglo resultante y verificar cada número de serie
            for (String serial : serialNumbers) {
                System.out.println("Número de serie: " + serial);
                try {
                    Medidores medidor = medidoresService.findById(serial);
                    if (medidor != null) {
                        System.out.println("Medidor encontrado: " + medidor.getVcSerie());

                        medidor.setEnAgendaProgramacionesAMI(null);
                        medidor.setEstadoEnAgenda(null);

                    } else {
                        System.out.println("Medidor no encontrado para el número de serie: " + serial);
                    }
                } catch (RuntimeException e) {
                    System.out.println("Medidor no encontrado para el número de serie: " + serial);
                }
            }
        } catch (JsonProcessingException e) {
            // Manejar la excepción
            e.printStackTrace();
        }

    }
}

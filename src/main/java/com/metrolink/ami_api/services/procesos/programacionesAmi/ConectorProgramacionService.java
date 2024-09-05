package com.metrolink.ami_api.services.procesos.programacionesAmi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.metrolink.ami_api.models.medidor.Medidores;
import com.metrolink.ami_api.models.procesos.programacionesAmi.ProgramacionesAMI;
import com.metrolink.ami_api.services.medidor.MedidoresService;

@Service
public class ConectorProgramacionService {

    @Autowired
    private MedidoresService medidoresService;

    public String UsarConectorProgramacionCaso1(String mensaje, ProgramacionesAMI programacionAMI) {

        programacionAMI.getGrupoMedidores().getVcfiltro();

        System.out.println(programacionAMI.getGrupoMedidores().getVcidentificador());

        List<Medidores> medidores = medidoresService
                .findByConcentradorVcnoSerie(programacionAMI.getGrupoMedidores().getVcidentificador());

        medidores.forEach(medidor -> {
            String vcSerie = medidor.getVcSerie();
            System.out.println("vcserie: " + vcSerie);
        });

        // leer los medidores a leer en este punto se acceden a traves del concetrador
        // con la direccion ip y puerto de concentrador

        // Entregar el resultado de los medidores leidos

        String medidor1 = "19014";

        String medidoresFaltantesPorLeer = String.format("[\"%s\", \"15913\", \"61452\"]", medidor1);

        return medidoresFaltantesPorLeer;

    }

    public String UsarConectorProgramacionFaltantesCaso1(String mensaje, ProgramacionesAMI programacionAMI,
            String medidoresFaltantesPorLeer_) {

        programacionAMI.getGrupoMedidores().getVcfiltro();

        System.out.println(programacionAMI.getGrupoMedidores().getVcidentificador());

        // Convertir la cadena JSON a una lista de strings
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> medidoresFaltantesList;

        try {
            medidoresFaltantesList = objectMapper.readValue(medidoresFaltantesPorLeer_,
                    new TypeReference<List<String>>() {
                    });

            // Extraer elementos en variables individuales
            String vcserie1 = medidoresFaltantesList.get(0);
            String vcserie2 = medidoresFaltantesList.get(1);
            String vcserie3 = medidoresFaltantesList.get(2);

            System.out.println("vcserie1: " + vcserie1);
            System.out.println("vcserie2: " + vcserie2);
            System.out.println("vcserie3: " + vcserie3);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al procesar medidores faltantes";
        }

        //medidoresFaltantesPorLeer_ = "[]";

        String medidor1 = "19014";

        String medidoresFaltantesPorLeer = String.format("[\"%s\", \"15913\", \"61452\"]", medidor1);



        //return medidoresFaltantesPorLeer_;
        return medidoresFaltantesPorLeer;

    }
}

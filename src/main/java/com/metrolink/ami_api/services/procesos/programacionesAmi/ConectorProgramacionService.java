package com.metrolink.ami_api.services.procesos.programacionesAmi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.metrolink.ami_api.models.medidor.Medidores;
import com.metrolink.ami_api.models.procesos.programacionesAmi.ProgramacionesAMI;
import com.metrolink.ami_api.services.medidor.MedidoresService;

@Service
public class ConectorProgramacionService {

    @Autowired
    private MedidoresService medidoresService;

    public String UsarConectorProgramacionTEST(String mensaje, ProgramacionesAMI programacionAMI) {

        programacionAMI.getGrupoMedidores().getVcfiltro();

        System.out.println(programacionAMI.getGrupoMedidores().getVcidentificador());

        List<Medidores> medidores = medidoresService
                .findByConcentradorVcnoSerie(programacionAMI.getGrupoMedidores().getVcidentificador());

        medidores.forEach(medidor -> {
            String vcSerie = medidor.getVcSerie();
            System.out.println("vcserie: " + vcSerie);
        });

        // leer los medidores a leer en este punto se acceden a traves del concetrador con la direccion ip y puerto de concentrador
        

        // Entregar el resultado de los medidores leidos

        int leidos = 2;
        int noLeidos = 1;

        String ResultadoLLeidosNoLeidos = String.format("{\n" +
                "  \"medidoresLeidos\": %d,\n" +
                "  \"medidoresNoLeidos\": %d\n" +
                "}", leidos,noLeidos );

    
        return ResultadoLLeidosNoLeidos;

    }
}

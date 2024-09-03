package com.metrolink.ami_api.services.procesos.conectorGeneral;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import com.metrolink.ami_api.models.primeraLectura.AutoconfMedidor;

import com.metrolink.ami_api.services.procesos.autoconfiguracion.ConectorAutoConfService2;

import com.metrolink.ami_api.services.procesos.deteccionMed.ConectorDetecMedService2;

import com.metrolink.ami_api.services.procesos.programacionesAmi.ConectorProgramacionService;

@Service
public class ConectorGeneralService {

    @Autowired
    private ConectorDetecMedService2 conectorDetecMedService2;

    @Autowired
    private ConectorAutoConfService2 conectorAutoConfService2;

    @Autowired
    private ConectorProgramacionService conectorProgramacionService;



    public String usarConectorDeteccion(String json) {
        System.out.println("entre a usarconector detectcion");
        String newJson = conectorDetecMedService2.usarConectorDeteccion(json);
        return newJson;
    }

    public List<AutoconfMedidor> UsarConectorAutoConfMed(String vcnoSerie) {
        List<AutoconfMedidor> autoconfMedidores = conectorAutoConfService2.UsarConectorAutoConfMed(vcnoSerie);
        return autoconfMedidores;
    }

    public AutoconfMedidor UsarConectorAutoConfMed2(String vcserie, String vcnoSerie, String vcSIC, JsonNode vcserialesNode) {
        AutoconfMedidor autoconfMedidor = conectorAutoConfService2.UsarConectorAutoConfMed_solo(vcserie, vcnoSerie, vcSIC, vcserialesNode);
        return autoconfMedidor;
    }



    public String usarConectorProgramacion(String mensaje) {
        String Impreso = conectorProgramacionService.UsarConectorProgramacion(mensaje);

        
        return  Impreso; // Retorna el mensaje para confirmar la ejecución
    }
}

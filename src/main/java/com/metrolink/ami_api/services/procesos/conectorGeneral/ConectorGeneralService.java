package com.metrolink.ami_api.services.procesos.conectorGeneral;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import com.metrolink.ami_api.models.primeraLectura.AutoconfMedidor;

import com.metrolink.ami_api.services.procesos.autoconfiguracion.ConectorAutoConfService2;

import com.metrolink.ami_api.services.procesos.deteccionMed.ConectorDetecMedService2;

@Service
public class ConectorGeneralService {

    @Autowired
    private ConectorDetecMedService2 conectorDetecMedService2;

    @Autowired
    private ConectorAutoConfService2 conectorAutoConfService2;

    public String usarConectorDeteccion(String json) {
        System.out.println("entre a usarconector detectcion");
        String newJson = conectorDetecMedService2.usarConectorDeteccion(json);
        return newJson;
    }

    public List<AutoconfMedidor> UsarConectorAutoConfMed(JsonNode rootNode) {
        List<AutoconfMedidor> autoconfMedidores = conectorAutoConfService2.UsarConectorAutoConfMed(rootNode);
        return autoconfMedidores;
    }
}

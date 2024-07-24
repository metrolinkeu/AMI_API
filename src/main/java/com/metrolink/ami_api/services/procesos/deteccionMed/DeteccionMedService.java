package com.metrolink.ami_api.services.procesos.deteccionMed;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metrolink.ami_api.models.tablasFront.Empresas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
public class DeteccionMedService {

    @Autowired
    private ConectorDetecMedService conectorDetecMedService;

    public Empresas procesarDeteccionByCon(String json) throws IOException {
        // Llamar al conector para procesar el JSON
        String jsonMed = conectorDetecMedService.usarConectorDeteccion(json);

        // Parsear el JSON devuelto para extraer los medidores
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode medidoresNode = objectMapper.readTree(jsonMed).get("Medidores");

        // Extraer los medidores en un mapa
        Map<String, String> medidores = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> fields = medidoresNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            medidores.put(field.getKey(), field.getValue().asText());
        }

        // Imprimir los medidores extraídos
        medidores.forEach((key, value) -> System.out.println(key + ": " + value));


        //objeto de prueba, lo que retornara este sera la lista de medidores creados
        Empresas empresa = new Empresas();
        empresa.setVcempresa("Empresa de prueba");
        empresa.setVcconcat("1 - Empresa de prueba");


        // Devolver objeto con medidores guardados
        return empresa;
    }
}

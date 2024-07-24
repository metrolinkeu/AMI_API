package com.metrolink.ami_api.services.procesos.deteccionMed;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DeteccionMedService {

    public String procesarConcentrador(String json) throws IOException {
        // Parsear el JSON para obtener el valor de "vcnoSerie"
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(json);
        String vcnoSerie = jsonNode.get("vcnoSerie").asText();

        // Imprimir el JSON recibido y el valor de "vcnoSerie" en la consola
        System.out.println("Recibido: " + json);
        System.out.println("vcnoSerie: " + vcnoSerie);

        // Retornar el valor de "vcnoSerie"
        return vcnoSerie;
    }
}

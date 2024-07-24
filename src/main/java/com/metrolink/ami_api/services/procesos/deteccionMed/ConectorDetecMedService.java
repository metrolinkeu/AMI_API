package com.metrolink.ami_api.services.procesos.deteccionMed;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ConectorDetecMedService {

    public String usarConectorDeteccion(String json) throws IOException {
        // Parsear el JSON para obtener los valores
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(json);
        String vcnoSerie = jsonNode.get("vcnoSerie").asText();
      

        // Imprimir los valores
        System.out.println("vcnoSerie: " + vcnoSerie);

        // Construir el nuevo JSON en el formato deseado
        String newJson = "{ \"Medidores\": { " +
                "\"Medidor1\": \"1\", " +
                "\"Medidor2\": \"2\", " +
                "\"Medidor3\": \"3\", " +
                "\"Medidor4\": \"4\", " +
                "\"Medidor5\": \"5\" " +
                "} }";



        // Aquí puedes realizar cualquier lógica adicional si es necesario
        System.out.println("Recibido: " + json);

        // Devolver el JSON procesado
        return newJson;
    }
}

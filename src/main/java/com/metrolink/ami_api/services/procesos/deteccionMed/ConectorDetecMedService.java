package com.metrolink.ami_api.services.procesos.deteccionMed;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Random;
import java.util.StringJoiner;

@Service
public class ConectorDetecMedService {

    public String usarConectorDeteccion(String json) throws IOException {
        // Parsear el JSON para obtener los valores
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(json);
        String vcnoSerie = jsonNode.get("vcnoSerie").asText();

        // Imprimir los valores
        System.out.println("vcnoSerie: " + vcnoSerie);

        // Generar medidores aleatorios
        Random random = new Random();
        int cantidadMedidores = random.nextInt(3) + 1; // Generar entre 1 y 10 medidores
        StringJoiner medidoresJoiner = new StringJoiner(", ");

        for (int i = 1; i <= cantidadMedidores; i++) {
            String numeroSerie = String.format("%05d", random.nextInt(99999)); // Número de serie de 5 dígitos
            medidoresJoiner.add("\"Medidor" + i + "\": \"" + numeroSerie + "\"");
        }

        String newJson = "{ \"Medidores\": { " + medidoresJoiner.toString() + " } }";

        

        System.out.println("Recibido: " + json);

        // Devolver el JSON procesado
        return newJson;
    }
}

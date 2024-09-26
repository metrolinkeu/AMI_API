package com.metrolink.ami_api.services.procesos.deteccionMed;

import java.io.IOException;
import java.util.Random;
import java.util.StringJoiner;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ConectorDetecMedService {

    public String usarConectorDeteccion(String json) {

        // Procesar el JSON como antes
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String vcnoSerie = jsonNode.get("vcnoSerie").asText();
        System.out.println("vcnoSerie: " + vcnoSerie);

        /*
         * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
         * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
         * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
         * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
         * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
         */

        Random random = new Random();
        int cantidadMedidores = 3;
        StringJoiner medidoresJoiner = new StringJoiner(", ");
        for (int i = 1; i <= cantidadMedidores; i++) {
            String numeroSerie = String.format("%05d", random.nextInt(99999)); // Número de serie de 5 dígitos
            medidoresJoiner.add("\"Medidor" + i + "\": \"" + numeroSerie + "\"");
        }
        String newJson = "{ \"Medidores\": { " + medidoresJoiner.toString() + " } }";
        System.out.println("Procesado: " + newJson);
        return newJson;

    }

}

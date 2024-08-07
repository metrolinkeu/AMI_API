package com.metrolink.ami_api.services.procesos.programacionesAmi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metrolink.ami_api.models.procesos.programacionesAmi.MedidoresAgenda;
import org.springframework.stereotype.Service;

@Service
public class MedidoresAgendaService {

    public List<MedidoresAgenda> procesarMedidoresAgenda(String json) throws IOException {

        // Parsear el JSON para obtener los valores
        ObjectMapper objectMapperCon = new ObjectMapper();
        JsonNode jsonNode = objectMapperCon.readTree(json);

        List<MedidoresAgenda> medidoresAgenda = new ArrayList<>();

        // Verificar si el JSON contiene "vcnoSerie" o "vcseriales"
        if (jsonNode.has("vcnoSerie")) {
            String vcnoSerie = jsonNode.get("vcnoSerie").asText();
            // Acción para "vcnoSerie"
            System.out.println("Número de serie (vcnoSerie): " + vcnoSerie);
            // Lógica para manejar "vcnoSerie"
            // Por ejemplo, agregar un MedidoresAgenda con este número de serie
            MedidoresAgenda medidor = new MedidoresAgenda();
            medidor.setVcSerie(vcnoSerie);
            medidoresAgenda.add(medidor);
        } else if (jsonNode.has("vcseriales")) {
            JsonNode vcserialesNode = jsonNode.get("vcseriales");
            // Acción para "vcseriales"
            System.out.println("Lista de series (vcseriales): ");
            // Recorrer los números de serie y realizar acciones
            vcserialesNode.fields().forEachRemaining(entry -> {
                String serial = entry.getValue().asText();
                System.out.println("Serie: " + serial);
                // Lógica para manejar cada número de serie
                // Por ejemplo, agregar un MedidoresAgenda con cada número de serie
                MedidoresAgenda medidor = new MedidoresAgenda();
                medidor.setVcSerie(serial);
                medidoresAgenda.add(medidor);
            });
        } else {
            System.out.println("Formato de JSON no reconocido.");
            // Opcionalmente, podrías lanzar una excepción o manejar este caso de otra forma
        }

        return medidoresAgenda;
    }

}

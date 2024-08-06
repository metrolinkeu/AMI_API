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

        String vcnoSerie = jsonNode.get("vcnoSerie").asText();
        // Imprimir los valores
        System.out.println("vcnoSerieeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee: " + vcnoSerie);

        List<MedidoresAgenda> medidoresAgenda = new ArrayList<>();

        return medidoresAgenda;
    }

}

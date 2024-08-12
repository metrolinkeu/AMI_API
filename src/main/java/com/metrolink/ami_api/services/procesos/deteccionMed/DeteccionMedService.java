package com.metrolink.ami_api.services.procesos.deteccionMed;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metrolink.ami_api.config.LoggerConfigProcesos;
import com.metrolink.ami_api.models.concentrador.Concentradores;
import com.metrolink.ami_api.models.medidor.Medidores;
import com.metrolink.ami_api.services.concentrador.ConcentradoresService;
import com.metrolink.ami_api.services.medidor.MedidoresService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import java.util.logging.Logger;

@Service
public class DeteccionMedService {

    @Autowired
    private ConectorDetecMedService conectorDetecMedService;

    @Autowired
    private MedidoresService medidoresService;

    @Autowired
    private ConcentradoresService concentradoresService;

    private static final Logger logger = LoggerConfigProcesos.getLogger();

    public List<Medidores> procesarDeteccionByCon(String json) throws IOException {

        // Parsear el JSON para obtener los valores
        ObjectMapper objectMapperCon = new ObjectMapper();
        JsonNode jsonNode = objectMapperCon.readTree(json);
        String vcnoSerie = jsonNode.get("vcnoSerie").asText();
        // Imprimir los valores
        System.out.println("vcnoSerie: " + vcnoSerie);


        // Llamar al conector para procesar el JSON
        String jsonMed = conectorDetecMedService.usarConectorDeteccion(json);

        logger.info("JSON Devuelto: " + jsonMed);

        // Parsear el JSON devuelto para extraer los medidores
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode medidoresNode = objectMapper.readTree(jsonMed).get("Medidores");

        // Extraer los medidores en un mapa
        Map<String, String> medidoresDetectados = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> fields = medidoresNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            medidoresDetectados.put(field.getKey(), field.getValue().asText());
        }

        System.out.println("Mediores detectados ");

        // Imprimir los medidores extraídos
        medidoresDetectados.forEach((key, value) -> System.out.println(key + ": " + value));

        // Crear una instancia de Medidores para cada serial detectado y guardarlo en la
        // base de datos
        List<Medidores> medidoresGuardados = new ArrayList<>();

        Concentradores concentrador;
        try {
            concentrador = concentradoresService.findById(vcnoSerie);

            for (String value : medidoresDetectados.values()) {
                Medidores medidor = new Medidores();
                medidor.setVcSerie(value);
                medidor.setLisMacro(true);
                medidor.setConcentrador(concentrador);

                try {
                    // Guardar el medidor en la base de datos
                    Medidores medidorGuardado = medidoresService.save(medidor, false);
                    medidorGuardado.setEsExistente(false);
                    medidoresGuardados.add(medidorGuardado);
                } catch (IllegalArgumentException e) {
                    Medidores medidorExistente = new Medidores();
                    medidorExistente.setVcSerie(value);
                    medidorExistente.setEsExistente(true);
                    medidoresGuardados.add(medidorExistente);
                }

            }

        } catch (RuntimeException e) {
            System.out.println("Concentrador no existente");
            logger.info("Concentrador no existente: " + vcnoSerie);

        }

        return medidoresGuardados;
    }
}

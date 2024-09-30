package com.metrolink.ami_api.services.procesos.deteccionMed;

import java.io.IOException;
import java.util.Random;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.metrolink.ami_api.models.procesos.ejecucionesLecturas.EjecucionesLecturaDetect;
import com.metrolink.ami_api.models.procesos.ejecucionesLecturas.EjecucionesLecturas;

import com.metrolink.ami_api.services.procesos.ejecucionesLecturas.EjecucionesLecturasService;

@Service
public class ConectorDetecMedService {

    @Autowired
    private EjecucionesLecturasService ejecucionesLecturasService;

    public String usarConectorDeteccion(String json) {

        Random random = new Random();

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

        EjecucionesLecturas ejecucionLectura = new EjecucionesLecturas();
        ejecucionLectura.setNidEjecucionLectura(0L);
        ejecucionLectura.setNidAnteriorIntentoEjecucionLectura((long) 0);
        ejecucionLectura.setDinicioEjecucionLectura(new Timestamp(System.currentTimeMillis()));
        ejecucionLectura.setNintentoLecturaNumero(1);

        // Crear instancia de EjecucionesLecturaDetect con valores aleatorios
        EjecucionesLecturaDetect ejecucionLecturaDetect = new EjecucionesLecturaDetect();
        ejecucionLecturaDetect.setVcdescripcionDetect("Deteccion de tabla de medidores del concentrador" + vcnoSerie);
        ejecucionLecturaDetect.setVcnoserie(vcnoSerie); // Usando el valor del JSON



        ////<----------------------------

        int cantidadMedidores = 3;
        StringJoiner medidoresJoiner = new StringJoiner(", ");
        for (int i = 1; i <= cantidadMedidores; i++) {
            String numeroSerie = String.format("%05d", random.nextInt(99999)); // Número de serie de 5 dígitos
            medidoresJoiner.add("\"Medidor" + i + "\": \"" + numeroSerie + "\"");
        }
        String newJson = "{ \"Medidores\": { " + medidoresJoiner.toString() + " } }";
        System.out.println("Procesado: " + newJson);


        ////<----------------------------
        


        if (!newJson.equals("{}")) {
            ejecucionLecturaDetect.setJsTablaMedidoresDetec(newJson); // JSON vacío para ejemplo
            ejecucionLecturaDetect.setLdeteccionOK(true);

        }else{
            ejecucionLecturaDetect.setLdeteccionOK(false);
        }


        ejecucionLectura.setEjecucionLecturaDetect(ejecucionLecturaDetect);

        ejecucionLectura.setDfinEjecucionLectura(new Timestamp(System.currentTimeMillis()));

        // Guardar
        EjecucionesLecturas createdEjecucion = ejecucionesLecturasService.save(ejecucionLectura, false);
        // esta se setea cuando se termine

        System.out.println(createdEjecucion);

        return newJson;

    }

}

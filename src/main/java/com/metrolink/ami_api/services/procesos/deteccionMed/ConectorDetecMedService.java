package com.metrolink.ami_api.services.procesos.deteccionMed;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Random;
import java.util.StringJoiner;
import org.springframework.beans.factory.annotation.Autowired;

import com.metrolink.ami_api.comunications.TcpClientDetecMedService;
import com.metrolink.ami_api.models.concentrador.Concentradores;
import com.metrolink.ami_api.services.concentrador.ConcentradoresService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.metrolink.ami_api.services.shared.SharedTaskQueue;

@Service
public class ConectorDetecMedService implements SharedTaskQueue.TaskProcessor {

    @Autowired
    private TcpClientDetecMedService tcpClientDetecMedService;

    @Autowired
    private ConcentradoresService concentradoresService;

    @Autowired
    private SharedTaskQueue sharedTaskQueue; // Inyectar la cola compartida

    public String usarConectorDeteccion(String json) throws IOException, ExecutionException, InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        sharedTaskQueue.submitTask(new SharedTaskQueue.CompletableFutureTask(future, json, this)); // Encolar la tarea
                                                                                                   // con el JSON
        return future.get(); // Esto bloquea hasta que la tarea se complete
    }

    @Override
    public String processRequest(String json) {
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
        Concentradores concentrador = concentradoresService.findById(vcnoSerie);
        System.out.println(concentrador.getParamTiposDeComunicacion().getVctiposDeComunicacion());

        if ("Servidor".equalsIgnoreCase(concentrador.getParamTiposDeComunicacion().getVctiposDeComunicacion())) {
            String direccion = concentrador.getParamTiposDeComunicacion().getVcip();
            int puerto = Integer.parseInt(concentrador.getParamTiposDeComunicacion().getVcpuerto());

            byte[] bytesToSend = new byte[] { 0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00,
                    0x02, 0x62, 0x00 };

            String response = tcpClientDetecMedService.sendBytesToAddressAndPort(bytesToSend, direccion, puerto);
            System.out.println("Response from TCP server at " + direccion + ":" + puerto + ": " + response);
        } else {
            System.out.println("en construccion");
            return "en construcción";
        }

        Random random = new Random();
        int cantidadMedidores = 1;
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

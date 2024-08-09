package com.metrolink.ami_api.services.procesos.deteccionMed;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Random;
import java.util.StringJoiner;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ConectorDetecMedService {

    @Autowired
    private TcpClientService tcpClientService; // Asegúrate de tener este servicio disponible

    private final ExecutorService executorService = Executors.newFixedThreadPool(3); // Crear un pool de 3 threads

    public String usarConectorDeteccion(String json) throws IOException {
        // Parsear el JSON para obtener los valores
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(json);
        String vcnoSerie = jsonNode.get("vcnoSerie").asText();

        // Imprimir los valores
        System.out.println("vcnoSerie: " + vcnoSerie);

        // Direcciones y puertos de los servidores TCP
        String[] direcciones = { "190.145.202.42", "192.168.1.11", "192.168.1.11" };
        int[] puertos = { 60180, 57800, 57900 }; // Reemplaza con los puertos correctos

        // Bytes a enviar
        byte[] bytesToSend = new byte[] { 0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00, 0x02, 0x62, 0x00 };

        // Enviar los bytes a tres servidores TCP simultáneamente
        for (int i = 0; i < direcciones.length; i++) {
            final int index = i;
            executorService.submit(() -> {
                String response = tcpClientService.sendBytesToAddressAndPort(bytesToSend, direcciones[index],
                        puertos[index]);
                System.out.println(
                        "\033[47;30m" + // Secuencia ANSI para fondo blanco (47) y texto negro (30)
                                "Response from TCP server at " +
                                "\033[0m" + // Reset de color a los valores predeterminados
                                direcciones[index] + ":" + puertos[index] + ": " + response);

            });
        }

        // Generar medidores aleatorios
        Random random = new Random();
        // int cantidadMedidores = random.nextInt(3) + 1; // Generar entre 1 y 10
        // medidores
        int cantidadMedidores = 1;
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

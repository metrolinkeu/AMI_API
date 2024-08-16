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
import java.util.concurrent.LinkedBlockingQueue;

import com.metrolink.ami_api.comunications.TcpClientDetecMedService;
import com.metrolink.ami_api.models.concentrador.Concentradores;
import com.metrolink.ami_api.services.concentrador.ConcentradoresService;

@Service
public class ConectorDetecMedService {

    @Autowired
    private TcpClientDetecMedService tcpClientDetecMedService; // Asegúrate de tener este servicio disponible

    @Autowired
    private ConcentradoresService concentradoresService;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor(); // Crear un pool de 1 thread
    private final LinkedBlockingQueue<Runnable> requestQueue = new LinkedBlockingQueue<>(); // Cola para manejar las
                                                                                            // solicitudes

    public ConectorDetecMedService() {
        // Iniciar un hilo que procesará la cola
        executorService.submit(() -> {
            while (true) {
                try {
                    // Tomar la próxima solicitud de la cola y procesarla
                    Runnable requestTask = requestQueue.take();
                    requestTask.run();
                    // Pausa de 10ms después de procesar cada tarea
                    Thread.sleep(10); // Puedes ajustar el tiempo según sea necesario
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    public String usarConectorDeteccion(String json) throws IOException {
        // Parsear el JSON para obtener los valores
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(json);
        String vcnoSerie = jsonNode.get("vcnoSerie").asText();

        System.out.println("vcnoSerie: " + vcnoSerie);

        // Concentradores concentrador = concentradoresService.findById(vcnoSerie);

        // System.out.println(concentrador.getParamTiposDeComunicacion().getVctiposDeComunicacion());

        // if ("Servidor".equalsIgnoreCase(concentrador.getParamTiposDeComunicacion().getVctiposDeComunicacion())) {

        //     // Direcciones y puertos de los servidores TCP
        //     String direccion = concentrador.getParamTiposDeComunicacion().getVcip();
        //     int puerto = Integer.parseInt(concentrador.getParamTiposDeComunicacion().getVcpuerto());

        //     // Obtener direccion cliente y fisica
        //     // Contraseña

        //     // Bytes a enviar
        //     byte[] bytesToSend = new byte[] { 0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00, 0x02, 0x62, 0x00 };

        //     // Enviar los bytes a tres servidores TCP simultáneamente

        //     // Encolar la tarea para enviar los bytes
        //     requestQueue.offer(() -> {
        //         String response = tcpClientDetecMedService.sendBytesToAddressAndPort(bytesToSend, direccion, puerto);
        //         System.out.println("\033[47;30m" +
        //                 "Response from TCP server at " +
        //                 "\033[0m" +
        //                 direccion + ":" + puerto + ": " + response);
        //     });

        // } else {
        //     System.out.println("en construccion");
        // }

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

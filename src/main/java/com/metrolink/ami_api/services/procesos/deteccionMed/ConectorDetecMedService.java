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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class ConectorDetecMedService {

    @Autowired
    private TcpClientDetecMedService tcpClientDetecMedService;

    @Autowired
    private ConcentradoresService concentradoresService;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor(); // Crear un pool de 1 thread
    private final LinkedBlockingQueue<CompletableFutureTask> requestQueue = new LinkedBlockingQueue<>(); // Cola para
                                                                                                         // manejar las
                                                                                                         // solicitudes

    public ConectorDetecMedService() {
        // Iniciar un hilo que procesará la cola
        executorService.submit(() -> {
            while (true) {
                try {
                    // Tomar la próxima solicitud de la cola y procesarla
                    CompletableFutureTask task = requestQueue.take();
                    String result = processRequest(task.getJson()); // Procesa la tarea con el JSON
                    task.getFuture().complete(result); // Completa el CompletableFuture con el resultado
                    Thread.sleep(10); // Pausa de 10ms después de procesar cada tarea
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    public String usarConectorDeteccion(String json) throws IOException, ExecutionException, InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        requestQueue.offer(new CompletableFutureTask(future, json)); // Encolar la tarea con el JSON
        return future.get(); // Esto bloquea hasta que la tarea se complete
    }

    private String processRequest(String json) {
        // Procesar el JSON como lo hacías antes
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
            // Direcciones y puertos de los servidores TCP
            String direccion = concentrador.getParamTiposDeComunicacion().getVcip();
            int puerto = Integer.parseInt(concentrador.getParamTiposDeComunicacion().getVcpuerto());

            // Bytes a enviar
            byte[] bytesToSend = new byte[] { 0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00,
                    0x02, 0x62, 0x00 };

            // Enviar los bytes
            String response = tcpClientDetecMedService.sendBytesToAddressAndPort(bytesToSend, direccion, puerto);
            System.out.println("Response from TCP server at " + direccion + ":" + puerto + ": " + response);

        } else {
            System.out.println("en construccion");
            return "en construcción";
        }

        // Procesar la respuesta y crear el JSON final
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

    // Clase auxiliar para encapsular la tarea y el JSON
    private static class CompletableFutureTask {
        private final CompletableFuture<String> future;
        private final String json;

        public CompletableFutureTask(CompletableFuture<String> future, String json) {
            this.future = future;
            this.json = json;
        }

        public CompletableFuture<String> getFuture() {
            return future;
        }

        public String getJson() {
            return json;
        }
    }
}

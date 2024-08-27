package com.metrolink.ami_api.services.procesos.autoconfiguracion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metrolink.ami_api.comunications.TcpClientDetecMedService;
import com.metrolink.ami_api.models.concentrador.Concentradores;
import com.metrolink.ami_api.models.medidor.Medidores;
import com.metrolink.ami_api.models.primeraLectura.AutoConfCanalesPerfilCarga;
import com.metrolink.ami_api.models.primeraLectura.AutoConfCodigosObisCanal;
import com.metrolink.ami_api.models.primeraLectura.AutoconfMedidor;
import com.metrolink.ami_api.services.concentrador.ConcentradoresService;
import com.metrolink.ami_api.services.concentrador.hilo_colaCompartidaConcentrador.SharedTaskQueueConc;
import com.metrolink.ami_api.services.medidor.MedidoresService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class ConectorAutoConfService implements SharedTaskQueueConc.TaskProcessor<List<AutoconfMedidor>> {

    @Autowired
    private MedidoresService medidoresService;

    @Autowired
    private ConcentradoresService concentradoresService;

    @Autowired
    private TcpClientDetecMedService tcpClientDetecMedService;

    @Autowired
    private SharedTaskQueueConc sharedTaskQueue; // Inyectar la cola compartida

    public List<AutoconfMedidor> procesarConfiguracion(JsonNode rootNode)
            throws ExecutionException, InterruptedException {

        // Convertir el JsonNode a JSON String para encolarlo
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            json = mapper.writeValueAsString(rootNode);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Crear CompletableFuture para la tarea
        CompletableFuture<List<AutoconfMedidor>> future = new CompletableFuture<>();

        // Encolar la tarea
        sharedTaskQueue.submitTask(new SharedTaskQueueConc.CompletableFutureTask<>(future, json, this));

        // Esperar a que la tarea se complete y devolver el resultado
        return future.get();

    }

    @Override
    public List<AutoconfMedidor> processRequest(String json) {
        // Convertir el JSON a JsonNode y procesar la configuración
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = objectMapper.readTree(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Llama al método interno para procesar la configuración
        return procesarConfiguracionInterna(rootNode);
    }

    private List<AutoconfMedidor> procesarConfiguracionInterna(JsonNode rootNode) {

        List<AutoconfMedidor> autoconfMedidores = new ArrayList<>();
        Random random = new Random();

        try {
            String vcnoSerie = rootNode.path("vcnoSerie").asText();
            System.out.println("vcnoSerie: " + vcnoSerie);

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // parte para pruebas de conexion con socket ///////////////////////////////////////////////////////////////
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

            }
            // parte para pruebas de conexion con socket ///////////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////

            JsonNode vcserialesNode = rootNode.path("vcseriales");

            if (vcserialesNode.isMissingNode()) {
                List<Medidores> medidores = medidoresService.findByConcentradorVcnoSerie(vcnoSerie);
                for (Medidores medidor : medidores) {
                    AutoconfMedidor autoconfMedidor = crearAutoconfMedidor(medidor.getVcSerie(), random);
                    autoconfMedidores.add(autoconfMedidor);
                }
            } else {
                vcserialesNode.forEach(serialNode -> {
                    String vcserie = serialNode.asText();
                    AutoconfMedidor autoconfMedidor = crearAutoconfMedidor(vcserie, random);
                    autoconfMedidores.add(autoconfMedidor);
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return autoconfMedidores;
    }

    private AutoconfMedidor crearAutoconfMedidor(String vcSerie, Random random) {
        AutoconfMedidor autoconfMedidor = new AutoconfMedidor();
        autoconfMedidor.setVcSerie(vcSerie);

        AutoConfCanalesPerfilCarga canalesPerfilCarga = new AutoConfCanalesPerfilCarga();
        canalesPerfilCarga.setAutoConfCodigosObisCanal_1(crearCodigosObisCanal(random));
        canalesPerfilCarga.setAutoConfCodigosObisCanal_2(crearCodigosObisCanal(random));
        canalesPerfilCarga.setAutoConfCodigosObisCanal_3(crearCodigosObisCanal(random));

        autoconfMedidor.setAutoConfcanalesPerfilCarga(canalesPerfilCarga);

        LocalDateTime dateTime = LocalDateTime.now().plusDays(random.nextInt(10)); // Fecha aleatoria cercana
        // Convertir LocalDateTime a Timestamp
        Timestamp timestamp = Timestamp.valueOf(dateTime);
        autoconfMedidor.setDfechaHoraUltimaLectura(timestamp);

        autoconfMedidor.setVcdíasdeRegDíariosMensuales(String.valueOf(random.nextInt(30) + 1));
        autoconfMedidor.setVcdiasdeEventos(String.valueOf(random.nextInt(20) + 1));
        int[] opcionesIntegracion = { 15, 30, 60 };
        int periodoIntegracion = opcionesIntegracion[random.nextInt(opcionesIntegracion.length)];
        autoconfMedidor.setVcperiodoIntegracion(String.valueOf(periodoIntegracion));
        autoconfMedidor.setVcultimoEstadoRele(random.nextBoolean() ? "activo" : "inactivo");
        autoconfMedidor.setVcfirmware("v" + (random.nextInt(2) + 1) + "." + (random.nextInt(9) + 1) + "."
                + (random.nextInt(9) + 1));

        return autoconfMedidor;
    }

    private AutoConfCodigosObisCanal crearCodigosObisCanal(Random random) {
        AutoConfCodigosObisCanal codigosObisCanal = new AutoConfCodigosObisCanal();
        codigosObisCanal.setVcobis_1("1-0:1.8." + (random.nextInt(4) + 1));
        codigosObisCanal.setVcobis_2("1-0:1.8." + (random.nextInt(4) + 1));
        codigosObisCanal.setVcobis_3("1-0:2.8." + (random.nextInt(4) + 1));
        codigosObisCanal.setVcobis_4("1-0:2.8." + (random.nextInt(4) + 1));
        codigosObisCanal.setVcobis_5("1-0:3.7." + random.nextInt(10));
        codigosObisCanal.setVcobis_6("1-0:4.7." + random.nextInt(10));
        codigosObisCanal.setVcobis_7("1-0:5.7." + random.nextInt(10));
        codigosObisCanal.setVcobis_8("1-0:6.7." + random.nextInt(10));
        codigosObisCanal.setVcobis_9("1-0:7.0." + random.nextInt(10));
        codigosObisCanal.setVcobis_10("1-0:8.0." + random.nextInt(10));
        return codigosObisCanal;
    }

}

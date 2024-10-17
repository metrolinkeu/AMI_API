package com.metrolink.ami_api.services.procesos.ejecucionesLecturas;

import com.fasterxml.jackson.databind.JsonNode;
import com.metrolink.ami_api.comunications.conectividad.ConectividadService;
import com.metrolink.ami_api.comunications.conectividadTCP.ConexionStreams;

import com.metrolink.ami_api.models.medidor.Medidores;
import com.metrolink.ami_api.models.primeraLectura.AutoConfCanalesPerfilCarga;
import com.metrolink.ami_api.models.primeraLectura.AutoConfCodigosObisCanal;
import com.metrolink.ami_api.models.primeraLectura.AutoconfMedidor;
import com.metrolink.ami_api.models.procesos.ejecucionesLecturas.EjecucionesLecturas;

import com.metrolink.ami_api.services.medidor.MedidoresService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.net.Socket;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EjecucionesLectHandlerService {

        @Autowired
        private MedidoresService medidoresService;

        @Autowired
        private ConectividadService conectividadService;

        public Object EnviarAEjecucionesLectHandler(EjecucionesLecturas ejecucionLecturas) {

                Random random = new Random();

                System.out.println("");
                System.out.println("-------------------------------------------------");
                System.out.println("ID de Ejecución de Lectura: " + ejecucionLecturas.getNidEjecucionLectura());
                System.out.println("Intento de Lectura Número: " + ejecucionLecturas.getNintentoLecturaNumero());
                System.out.println(
                                "Anterior intento de Lectura Número: "
                                                + ejecucionLecturas.getNidAnteriorIntentoEjecucionLectura());
                System.out.println("Inicio de Ejecución: " + ejecucionLecturas.getDinicioEjecucionLectura());

                // Manejo del caso de Ejecución de Lectura Detect
                if (ejecucionLecturas.getEjecucionLecturaDetect() != null) {

                        System.out.println("Ejecución de Lectura Detect:");
                        System.out.println("  Descripción Detección: "
                                        + ejecucionLecturas.getEjecucionLecturaDetect().getVcdescripcionDetect());
                        System.out.println(
                                        "  noSerie: " + ejecucionLecturas.getEjecucionLecturaDetect().getVcnoserie());
                        String vcnoserie = ejecucionLecturas.getEjecucionLecturaDetect().getVcnoserie();

                        // ------------------------------
                        // Equipo: Concentrador
                        // peticion: tabla

                        ConexionStreams conexionStreams = conectividadService.conectividad("C_" + vcnoserie);

                        enviarMensajePrueba(conexionStreams, vcnoserie);

                        //ya teiendo los IOstreams , necesito pasar al control de flijo de secion
                        //que debo de mandar??


                        
                        // Control de Flujo de secion.

                        // ------------------------------Simulado

                        int cantidadMedidores = 3;
                        StringJoiner medidoresJoiner = new StringJoiner(", ");
                        for (int i = 1; i <= cantidadMedidores; i++) {
                                String numeroSerie = String.format("%05d", random.nextInt(99999)); // Número de serie de
                                                                                                   // 5 dígitos
                                medidoresJoiner.add("\"Medidor" + i + "\": \"" + numeroSerie + "\"");
                        }
                        String newJson = "{ \"Medidores\": { " + medidoresJoiner.toString() + " } }";
                        System.out.println("Procesado: " + newJson);

                        System.out.println("-------------------------------------------------");
                        System.out.println("");
                        // ------------------------------SImulado

                        // resultado

                        // -----------------------------

                        return newJson;
                }

                // Manejo del caso de Ejecución de Lectura AutoConf
                if (ejecucionLecturas.getEjecucionLecturaAutoConf() != null) {

                        JsonNode rootNode = ejecucionLecturas.getEjecucionLecturaAutoConf()
                                        .getJsequiposAutoconfigurar();

                        // Obtener nodos del JSON
                        JsonNode vcnoSerieNode = rootNode.path("vcnoSerie");
                        String vcnoSerie = vcnoSerieNode.asText(null);

                        JsonNode vcserialesNode = rootNode.path("vcseriales");

                        JsonNode vcSICNode = rootNode.path("SIC");
                        String vcSIC = vcSICNode.asText(null);

                        String vcSerie = ejecucionLecturas.getEjecucionLecturaAutoConf().getVcserie();

                        System.out.println("Ejecución de Lectura AutoConf:");
                        System.out.println(" Descripción AutoConf: "
                                        + ejecucionLecturas.getEjecucionLecturaAutoConf().getVcdescripcionAutoconf());
                        System.out.println(" Serie: " + vcSerie);
                        System.out.println(
                                        " noSerie: " + ejecucionLecturas.getEjecucionLecturaAutoConf().getVcnoserie());
                        System.out.println(" Equipos AutoConfigurar: " + rootNode);

                        // Determinar qué nodos están presentes
                        boolean hasVcnoSerie = !vcnoSerieNode.isMissingNode() && !vcnoSerieNode.isNull();
                        boolean hasVcseriales = !vcserialesNode.isMissingNode() && !vcserialesNode.isNull();
                        boolean hasVcSIC = !vcSICNode.isMissingNode() && !vcSICNode.isNull();

                        return handleAutoConfCase(hasVcnoSerie, hasVcseriales, hasVcSIC, vcnoSerie, vcSerie, vcSIC,
                                        vcserialesNode);
                }

                // Manejo del caso de Ejecución de Lectura Prog
                if (ejecucionLecturas.getEjecucionLecturaProg() != null) {

                        String vcSerieAReintentar = "";

                        System.out.println("Ejecución de Lectura Prog:");

                        System.out.println(" Descripción Prog: " +
                                        ejecucionLecturas.getEjecucionLecturaProg().getVcdescripcionProg());
                        System.out.println(" Serie: " +
                                        ejecucionLecturas.getEjecucionLecturaProg().getVcserie());
                        System.out.println(" noSerie: " +
                                        ejecucionLecturas.getEjecucionLecturaProg().getVcnoserie());

                        System.out.println(" Series Medidores: " +
                                        ejecucionLecturas.getEjecucionLecturaProg().getJsseriesMed());

                        String vcnoserie = ejecucionLecturas.getEjecucionLecturaProg().getVcnoserie();
                        String vcserie = ejecucionLecturas.getEjecucionLecturaProg().getVcserie();

                        // ------------------------------

                        // Equipo: Concentrador
                        // peticion: las que vengan en la programacion

                        if (ejecucionLecturas.getEjecucionLecturaProg().getVcnoserie() != null) { // caso por
                                                                                                  // concentrador

                                // ------------------------------
                                // Equipo: Concentrador
                                // Series: jsseriesMed, estos se leeran
                                // peticion: Las programadas

                                ejecucionLecturas.getEjecucionLecturaProg().getJsseriesMed(); // Series que se quieren
                                                                                              // leer

                                ConexionStreams conexionStreams = conectividadService.conectividad("C_" + vcnoserie);

                                enviarMensajePrueba(conexionStreams, vcnoserie);

                                // Control de Flujo de secion.
                                // ------------------------------Simulado

                                String medidor1 = "19014";
                                String medidoresFaltantesPorLeer = String.format("[\"%s\", \"15913\", \"61452\"]",
                                                medidor1);

                                // ------------------------------SImulado
                                // resultado
                                // -----------------------------
                                return medidoresFaltantesPorLeer;
                        }

                        else {

                                // ------------------------------
                                // Equipo: Medidor
                                // serie: vcserie
                                // peticion: Las programadas

                                ejecucionLecturas.getEjecucionLecturaProg().getVcserie();

                                ConexionStreams conexionStreams = conectividadService.conectividad("M_" + vcserie);

                                enviarMensajePrueba(conexionStreams, vcserie);

                                // Control de Flujo de secion.

                                // ------------------------------Simulado
                                vcSerieAReintentar = ejecucionLecturas.getEjecucionLecturaProg().getVcserie();
                                System.out.println("extraje el vcserie del metodo de ejecuciones y es "
                                                + vcSerieAReintentar);
                                System.out.println("");
                                // ------------------------------SImulado
                                // resultado
                                // -----------------------------
                                return vcSerieAReintentar;
                        }
                }

                // Caso en que no hay ningún tipo de Ejecución asociado
                if (ejecucionLecturas.getEjecucionLecturaDetect() == null &&
                                ejecucionLecturas.getEjecucionLecturaAutoConf() == null &&
                                ejecucionLecturas.getEjecucionLecturaProg() == null) {
                        System.out.println("No hay ningún tipo de ejecución asociado a esta lectura.");
                        return null;
                }

                return null;

        }

        private AutoconfMedidor crearAutoconfMedidor(String vcSerie, Random random) {
                System.out.println("Estoy dentro del ejecuciones handler y soy el metodo crear autoconf");
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

        private void enviarMensajePrueba(ConexionStreams conexionStreams, String Serie) {

                if (conexionStreams != null) {

                        InputStream in = conexionStreams.getInputStream();
                        OutputStream out = conexionStreams.getOutputStream();
                        Socket socket = conexionStreams.getSocket(); // Necesario para cerrar la conexión

                        try {
                                byte[] comando = ("Mensaje de prueba Cliente tcp de : " + Serie + "\n").getBytes();
                                // Enviar el comando con la Serie incluida
                                out.write(comando);
                                out.flush();

                                // Leer la respuesta
                                byte[] buffer = new byte[1024];
                                int bytesRead = in.read(buffer);
                                if (bytesRead != -1) {
                                        String respuesta = new String(buffer, 0, bytesRead);
                                        System.out.println("Respuesta del medidor: " + respuesta);
                                }

                        } catch (IOException e) {
                                e.printStackTrace();
                        } finally {
                                // Cerrar el 'Socket' al finalizar la comunicación
                                try {
                                        if (socket != null && !socket.isClosed()) {
                                                socket.close();
                                        }
                                } catch (IOException e) {
                                        e.printStackTrace();
                                }
                        }

                } else {
                        System.out.println("No se pudo establecer la conexión con el concentrador.");
                }
        }

        // Método para manejar los diferentes casos de AutoConf
        private Object handleAutoConfCase(boolean hasVcnoSerie, boolean hasVcseriales, boolean hasVcSIC,
                        String vcnoSerie, String vcSerie, String vcSIC, JsonNode vcserialesNode) {
                Random random = new Random();

                if (hasVcnoSerie && !hasVcseriales && !hasVcSIC) {
                        // Caso 1: Solo vcnoSerie está presente
                        connectAndSendMessage("C", vcnoSerie);
                        List<AutoconfMedidor> autoconfMedidores = simulateAutoconfMedidoresByConcentrador(vcnoSerie);
                        System.out.println("Lista de autoconfiguraciones generadas.");
                        return autoconfMedidores;
                } else if (hasVcnoSerie && hasVcseriales && !hasVcSIC) {
                        // Caso 2: vcnoSerie y vcseriales están presentes
                        connectAndSendMessage("C", vcnoSerie);
                        List<AutoconfMedidor> autoconfMedidores = simulateAutoconfMedidoresBySeriales(vcserialesNode);
                        System.out.println("Lista de autoconfiguraciones generadas.");
                        return autoconfMedidores;
                } else if (!hasVcnoSerie && hasVcseriales && !hasVcSIC) {
                        // Caso 3: Solo vcseriales está presente
                        connectAndSendMessage("M", vcSerie);
                        AutoconfMedidor autoconfMedidor = crearAutoconfMedidor(vcSerie, random);
                        return autoconfMedidor;
                } else if (hasVcnoSerie && !hasVcseriales && hasVcSIC) {
                        // Caso 4: vcnoSerie y vcSIC están presentes
                        connectAndSendMessage("C", vcnoSerie);
                        List<AutoconfMedidor> autoconfMedidores = simulateAutoconfMedidoresBySIC(vcSIC);
                        System.out.println("Lista de autoconfiguraciones generadas.");
                        return autoconfMedidores;
                } else if (!hasVcnoSerie && !hasVcseriales && hasVcSIC) {
                        // Caso 5: Solo vcSIC está presente
                        connectAndSendMessage("M", vcSerie);
                        AutoconfMedidor autoconfMedidor = crearAutoconfMedidor(vcSerie, random);
                        return autoconfMedidor;
                } else {
                        System.out.println("No existe - rootNode - no válido.");
                        // Caso por defecto
                        AutoconfMedidor autoconfMedidor = crearAutoconfMedidor(vcSerie, random);
                        return autoconfMedidor;
                }
        }

        // Método para conectar y enviar mensaje
        private void connectAndSendMessage(String equipoPrefix, String serie) {
                if (serie != null && !serie.isEmpty()) {

                        ConexionStreams conexionStreams = conectividadService.conectividad(equipoPrefix + "_" + serie);

                        enviarMensajePrueba(conexionStreams, serie);

                } else {
                        System.out.println("Serie no válida para conexión.");
                }
        }

        // Simulación de AutoconfMedidores por Concentrador
        private List<AutoconfMedidor> simulateAutoconfMedidoresByConcentrador(String vcnoSerie) {
                Random random = new Random();
                List<AutoconfMedidor> autoconfMedidores = new ArrayList<>();
                List<Medidores> medidores = medidoresService.findByConcentradorVcnoSerie(vcnoSerie);
                for (Medidores medidor : medidores) {
                        AutoconfMedidor autoconfMedidor = crearAutoconfMedidor(medidor.getVcSerie(), random);
                        autoconfMedidores.add(autoconfMedidor);
                }
                return autoconfMedidores;
        }

        // Simulación de AutoconfMedidores por lista de seriales
        private List<AutoconfMedidor> simulateAutoconfMedidoresBySeriales(JsonNode vcserialesNode) {
                Random random = new Random();
                List<AutoconfMedidor> autoconfMedidores = new ArrayList<>();
                vcserialesNode.forEach(serialNode -> {
                        String vcserie = serialNode.asText();
                        AutoconfMedidor autoconfMedidor = crearAutoconfMedidor(vcserie, random);
                        autoconfMedidores.add(autoconfMedidor);
                });
                return autoconfMedidores;
        }

        // Simulación de AutoconfMedidores por SIC
        private List<AutoconfMedidor> simulateAutoconfMedidoresBySIC(String vcSIC) {
                Random random = new Random();
                List<AutoconfMedidor> autoconfMedidores = new ArrayList<>();
                List<Medidores> medidores = medidoresService.findByVcsic(vcSIC);
                for (Medidores medidor : medidores) {
                        AutoconfMedidor autoconfMedidor = crearAutoconfMedidor(medidor.getVcSerie(), random);
                        autoconfMedidores.add(autoconfMedidor);
                }
                return autoconfMedidores;
        }

}

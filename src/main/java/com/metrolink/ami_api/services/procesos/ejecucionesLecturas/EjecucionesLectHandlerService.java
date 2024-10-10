package com.metrolink.ami_api.services.procesos.ejecucionesLecturas;

import com.fasterxml.jackson.databind.JsonNode;
import com.metrolink.ami_api.comunications.conectividadTCP.ConectividadTCP;
import com.metrolink.ami_api.comunications.conectividadTCP.ConexionStreams;
import com.metrolink.ami_api.comunications.conectividadTCP.TcpServerService;
import com.metrolink.ami_api.models.concentrador.Concentradores;
import com.metrolink.ami_api.models.medidor.Medidores;
import com.metrolink.ami_api.models.primeraLectura.AutoConfCanalesPerfilCarga;
import com.metrolink.ami_api.models.primeraLectura.AutoConfCodigosObisCanal;
import com.metrolink.ami_api.models.primeraLectura.AutoconfMedidor;
import com.metrolink.ami_api.models.procesos.ejecucionesLecturas.EjecucionesLecturas;
import com.metrolink.ami_api.services.concentrador.ConcentradoresService;
import com.metrolink.ami_api.services.medidor.MedidoresService;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;

import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EjecucionesLectHandlerService {

        @Autowired
        private ConectividadTCP conectividadTCP;

        @Autowired
        private ConcentradoresService concentradoresService;

        @Autowired
        private TcpServerService tcpServerService;

        @Autowired
        private MedidoresService medidoresService;

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

                        // ------------------------------

                        // Equipo: Concentrador
                        // peticion: tabla

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

                        JsonNode vcnoSerieNode = rootNode.path("vcnoSerie");
                        String vcnoSerie = rootNode.path("vcnoSerie").asText();

                        JsonNode vcserialesNode = rootNode.path("vcseriales");

                        JsonNode vcSICNode = rootNode.path("SIC");
                        String vcSIC = rootNode.path("SIC").asText();

                        List<Medidores> medidores = new ArrayList<>();

                        AutoconfMedidor autoconfMedidor = new AutoconfMedidor();

                        System.out.println("Ejecución de Lectura AutoConf:");
                        System.out.println(" Descripción AutoConf: " +
                                        ejecucionLecturas.getEjecucionLecturaAutoConf().getVcdescripcionAutoconf());
                        System.out.println(" Serie: " +
                                        ejecucionLecturas.getEjecucionLecturaAutoConf().getVcserie());
                        System.out.println(" noSerie: " +
                                        ejecucionLecturas.getEjecucionLecturaAutoConf().getVcnoserie());
                        System.out.println(" Equipos AutoConfigurar: " +
                                        ejecucionLecturas.getEjecucionLecturaAutoConf().getJsequiposAutoconfigurar());

                        // ------------------------------

                        // Solo Concentrador-<<<<<<<<<<<<<<<< UsarConectorAutoConfMed

                        if (!vcnoSerieNode.isMissingNode() && vcserialesNode.isMissingNode()
                                        && vcSICNode.isMissingNode()) {

                                // ------------------------------Simulado
                                List<AutoconfMedidor> autoconfMedidores = new ArrayList<>();

                                medidores = medidoresService.findByConcentradorVcnoSerie(
                                                ejecucionLecturas.getEjecucionLecturaAutoConf().getVcnoserie());
                                for (Medidores medidor : medidores) {
                                        autoconfMedidor = crearAutoconfMedidor(medidor.getVcSerie(),
                                                        random);
                                        autoconfMedidores.add(autoconfMedidor);
                                }
                                // ------------------------------Simulado

                                // resultado

                                // -----------------------------
                                System.out.println(
                                                "lista de autoconfiguraciones que viene de ejeuciiones lecturas handler");

                                return autoconfMedidores;

                        }
                        // Concentrador y lista de medidores  UsarConectorAutoConfMed
                        else if (!vcnoSerieNode.isMissingNode() && !vcserialesNode.isMissingNode()
                                        && vcSICNode.isMissingNode()) {

                                // ------------------------------Simulado
                                List<AutoconfMedidor> autoconfMedidores = new ArrayList<>();
                                final AutoconfMedidor[] autoconfMedidorHolder = new AutoconfMedidor[1];

                                vcserialesNode.forEach(serialNode -> {
                                        String vcserie = serialNode.asText();
                                        autoconfMedidorHolder[0] = crearAutoconfMedidor(vcserie, random); // Modificar
                                                                                                          // dentro de
                                                                                                          // la lambda
                                        autoconfMedidores.add(autoconfMedidorHolder[0]);

                                });

                                // ------------------------------Simulado

                                // resultado

                                // -----------------------------
                                System.out.println(
                                                "lista de autoconfiguraciones que viene de ejeuciiones lecturas handler");

                                return autoconfMedidores;

                        }

                        // Lista de medidores
                        else if (vcnoSerieNode.isMissingNode() && !vcserialesNode.isMissingNode()
                                        && vcSICNode.isMissingNode()) {

                                // ------------------------------Simulado
                                autoconfMedidor = crearAutoconfMedidor(
                                                ejecucionLecturas.getEjecucionLecturaAutoConf().getVcserie(),
                                                random);
                                // ------------------------------SImulado

                                // resultado

                                // -----------------------------
                                return autoconfMedidor;

                        }

                        // concentrador con SIC  UsarConectorAutoConfMed

                        else if (!vcnoSerieNode.isMissingNode() && vcserialesNode.isMissingNode()
                                        && !vcSICNode.isMissingNode()) {

                                // ------------------------------Simulado
                                List<AutoconfMedidor> autoconfMedidores = new ArrayList<>();

                                medidores = medidoresService.findByVcsic(vcSIC);
                                for (Medidores medidor : medidores) {
                                        autoconfMedidor = crearAutoconfMedidor(medidor.getVcSerie(),
                                                        random);
                                        autoconfMedidores.add(autoconfMedidor);
                                }
                                // ------------------------------Simulado

                                // resultado

                                // -----------------------------
                                System.out.println(
                                                "lista de autoconfiguraciones que viene de ejeuciiones lecturas handler");

                                return autoconfMedidores;

                        }

                        // solo SIC

                        else if (vcnoSerieNode.isMissingNode() && vcserialesNode.isMissingNode()
                                        && !vcSICNode.isMissingNode()) {

                                // ------------------------------Simulado
                                autoconfMedidor = crearAutoconfMedidor(
                                                ejecucionLecturas.getEjecucionLecturaAutoConf().getVcserie(),
                                                random);
                                // ------------------------------SImulado

                                // resultado

                                // -----------------------------
                                return autoconfMedidor;

                        } else {
                                System.out.println("no existe - rootnode - no valido");
                        }
                        //

                        // if (ejecucionLecturas.getEjecucionLecturaAutoConf().getVcnoserie() != null &&
                        // ejecucionLecturas.getEjecucionLecturaAutoConf().getVcserie() != null) {
                        // // si es por concentrador
                        // // primero consultar el concentrador que tipo de comunicacion tiene
                        // Concentradores concentrador = concentradoresService.findById(
                        // ejecucionLecturas.getEjecucionLecturaAutoConf().getVcnoserie());

                        // String tipoComunicacion = concentrador.getParamTiposDeComunicacion()
                        // .getVctiposDeComunicacion();

                        // if ("Servidor".equals(tipoComunicacion)) {
                        // // Obtener el valor del puerto como String
                        // String puertoStr = concentrador.getParamTiposDeComunicacion().getVcpuerto();
                        // System.out.println("Este el puerto al cual voy a crear como servidor "
                        // + puertoStr);
                        // int puerto;
                        // if (puertoStr != null) {
                        // try {
                        // puerto = Integer.parseInt(puertoStr);
                        // // Validar el rango del puerto
                        // if (puerto < 1024 || puerto > 65535) {
                        // System.err.println(
                        // "El puerto debe estar entre 1024 y 65535. Se asignará el puerto por defecto
                        // 12345.");
                        // puerto = 12345; // Puerto por defecto
                        // }
                        // } catch (NumberFormatException e) {
                        // System.err.println(
                        // "El valor del puerto no es un número válido: "
                        // + puertoStr
                        // + ". Se asignará el puerto por defecto 12345.");
                        // puerto = 12345; // Puerto por defecto
                        // }
                        // } else {
                        // System.err.println(
                        // "El valor del puerto es nulo. Se asignará el puerto por defecto 12345.");
                        // puerto = 12345; // Puerto por defecto
                        // }
                        // long duracionEnMinutos = 5; // Duración deseada
                        // tcpServerService.startServer(puerto, duracionEnMinutos);
                        // } else if ("Cliente".equals(tipoComunicacion)) {
                        // ConexionStreams conexionStreams = conectividadTCP.obtenerIOStreams(
                        // "concentrador",
                        // ejecucionLecturas.getEjecucionLecturaAutoConf().getVcnoserie());

                        // if (conexionStreams != null) {
                        // InputStream in = conexionStreams.getInputStream();
                        // OutputStream out = conexionStreams.getOutputStream();
                        // Socket socket = conexionStreams.getSocket(); // Necesario para cerrar la
                        // // conexión

                        // try {
                        // // Comunicación con el medidor usando 'in' y 'out'
                        // // Por ejemplo, enviar un comando y leer la respuesta

                        // // Enviar comando
                        // byte[] comando = new byte[] { 0x68, 0x6F, 0x6C, 0x61 };

                        // out.write(comando);
                        // out.flush();

                        // // Leer respuesta
                        // byte[] buffer = new byte[1024];
                        // int bytesRead = in.read(buffer);
                        // if (bytesRead != -1) {
                        // String respuesta = new String(buffer, 0, bytesRead);
                        // System.out.println(
                        // "Respuesta del medidor: " + respuesta);
                        // }

                        // } catch (IOException e) {
                        // e.printStackTrace();
                        // } finally {
                        // // Cerrar el 'Socket' al finalizar la comunicación
                        // try {
                        // if (socket != null && !socket.isClosed()) {
                        // socket.close();
                        // }
                        // } catch (IOException e) {
                        // e.printStackTrace();
                        // }
                        // }
                        // } else {
                        // System.out.println("No se pudo establecer la conexión con el medidor.");
                        // // Maneja este caso según tu lógica de negocio
                        // }
                        // }

                        // } else if (ejecucionLecturas.getEjecucionLecturaAutoConf().getVcserie() !=
                        // null
                        // && ejecucionLecturas.getEjecucionLecturaAutoConf().getVcnoserie() == null) {

                        // // si es por medidor
                        // ConexionStreams conexionStreams = conectividadTCP.obtenerIOStreams("medidor",
                        // ejecucionLecturas.getEjecucionLecturaAutoConf().getVcserie());

                        // }

                        // ------------------------------Simulado
                        autoconfMedidor = crearAutoconfMedidor(
                                        ejecucionLecturas.getEjecucionLecturaAutoConf().getVcserie(),
                                        random);
                        // ------------------------------SImulado

                        // resultado

                        // -----------------------------

                        return autoconfMedidor;
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

                        // ------------------------------

                        // Equipo: Medidor
                        // peticion: las que vengan en la programacion

                        // resultado

                        // ------------------------------Simulado

                        vcSerieAReintentar = ejecucionLecturas.getEjecucionLecturaProg().getVcserie();
                        System.out.println("extraje el vcserie del metodo de ejecuciones y es " + vcSerieAReintentar);
                        System.out.println("");

                        // ------------------------------SImulado

                        // -----------------------------

                        return vcSerieAReintentar;

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
}

package com.metrolink.ami_api.services.procesos.autoconfiguracion;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.JsonNode;

import com.metrolink.ami_api.models.medidor.Medidores;
import com.metrolink.ami_api.models.primeraLectura.AutoConfCanalesPerfilCarga;
import com.metrolink.ami_api.models.primeraLectura.AutoConfCodigosObisCanal;
import com.metrolink.ami_api.models.primeraLectura.AutoconfMedidor;

import com.metrolink.ami_api.services.medidor.MedidoresService;
import org.springframework.stereotype.Service;

@Service
public class ConectorAutoConfService2 {

    @Autowired
    private MedidoresService medidoresService;

    public List<AutoconfMedidor> UsarConectorAutoConfMed(String vcnoSerie) {

        System.out.println("Caso 1: vcnoSerie.");

        List<AutoconfMedidor> autoconfMedidores = new ArrayList<>();
        Random random = new Random();

        try {

            // ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // // parte para pruebas de conexion con socket
            // ///////////////////////////////////////////////////////////////
            // Concentradores concentrador = concentradoresService.findById(vcnoSerie);
            // System.out.println(concentrador.getParamTiposDeComunicacion().getVctiposDeComunicacion());

            // if
            // ("Servidor".equalsIgnoreCase(concentrador.getParamTiposDeComunicacion().getVctiposDeComunicacion()))
            // {
            // String direccion = concentrador.getParamTiposDeComunicacion().getVcip();
            // int puerto =
            // Integer.parseInt(concentrador.getParamTiposDeComunicacion().getVcpuerto());

            // byte[] bytesToSend = new byte[] { 0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00,
            // 0x02, 0x62, 0x00 };

            // String response =
            // tcpClientDetecMedService.sendBytesToAddressAndPort(bytesToSend, direccion,
            // puerto);
            // System.out.println("Response from TCP server at " + direccion + ":" + puerto
            // + ": " + response);
            // } else {
            // System.out.println("en construccion");

            // }
            // // parte para pruebas de conexion con socket
            // ///////////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////

            List<Medidores> medidores = medidoresService.findByConcentradorVcnoSerie(vcnoSerie);
            for (Medidores medidor : medidores) {
                AutoconfMedidor autoconfMedidor = crearAutoconfMedidor(medidor.getVcSerie(), random);
                autoconfMedidores.add(autoconfMedidor);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return autoconfMedidores;
    }

    public AutoconfMedidor UsarConectorAutoConfMed_solo(String vcserie, String vcnoSerie, String vcSIC,
            JsonNode vcserialesNode) {

        // Verificar y manejar el caso 2: vcnoSerie y vcseriales están presentes
        if (vcserialesNode != null && !vcserialesNode.isEmpty() && vcnoSerie != null && !vcnoSerie.equals("")
                && (vcSIC == null || vcSIC.equals(""))) {
            System.out.println("Caso 2: vcnoSerie y vcseriales están presentes.");
        }
        // Verificar y manejar el caso 3: Solo vcseriales está presente
        else if (vcserialesNode != null && !vcserialesNode.isEmpty() && (vcnoSerie == null || vcnoSerie.equals(""))
                && (vcSIC == null || vcSIC.equals(""))) {
            System.out.println("Caso 3: Solo vcseriales está presente.");
        }
        // Verificar y manejar el caso 4: vcnoSerie y SIC están presentes
        else if ((vcserialesNode == null || vcserialesNode.isEmpty()) && vcnoSerie != null && !vcnoSerie.equals("")
                && vcSIC != null
                && !vcSIC.equals("")) {
            System.out.println("Caso 4: vcnoSerie y SIC están presentes.");
        }
        // Verificar y manejar el caso 5: Solo SIC está presente
        else if ((vcserialesNode == null || vcserialesNode.isEmpty()) && (vcnoSerie == null || vcnoSerie.equals(""))
                && vcSIC != null
                && !vcSIC.equals("")) {
            System.out.println("Caso 5: Solo SIC está presente.");
        }
        Random random = new Random();
        AutoconfMedidor autoconfMedidor = crearAutoconfMedidor(vcserie, random);

        return autoconfMedidor;
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

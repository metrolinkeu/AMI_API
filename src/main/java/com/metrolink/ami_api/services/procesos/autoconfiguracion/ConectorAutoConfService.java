package com.metrolink.ami_api.services.procesos.autoconfiguracion;

import com.fasterxml.jackson.databind.JsonNode;
import com.metrolink.ami_api.models.medidor.Medidores;
import com.metrolink.ami_api.models.primeraLectura.AutoConfCanalesPerfilCarga;
import com.metrolink.ami_api.models.primeraLectura.AutoConfCodigosObisCanal;
import com.metrolink.ami_api.models.primeraLectura.AutoconfMedidor;
import com.metrolink.ami_api.services.medidor.MedidoresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ConectorAutoConfService {

    @Autowired
    private MedidoresService medidoresService;

    public List<AutoconfMedidor> procesarConfiguracion(JsonNode rootNode) {
        List<AutoconfMedidor> autoconfMedidores = new ArrayList<>();
        Random random = new Random();

        try {
            // Obtener el valor de vcnoSerie
            String vcnoSerie = rootNode.path("vcnoSerie").asText();
            System.out.println("vcnoSerie: " + vcnoSerie);

            // Verificar si existe el nodo "vcseriales"
            JsonNode vcserialesNode = rootNode.path("vcseriales");

            if (vcserialesNode.isMissingNode()) {
                // Caso donde solo hay "vcnoSerie"
                System.out.println("No se encontraron 'vcseriales'. Procesar solo con vcnoSerie.");
                List<Medidores> medidores = medidoresService.findByConcentradorVcnoSerie(vcnoSerie);

                for (Medidores medidor : medidores) {
                    AutoconfMedidor autoconfMedidor = crearAutoconfMedidor(medidor.getVcSerie(), random);
                    autoconfMedidores.add(autoconfMedidor);
                }
            } else {
                // Caso donde hay "vcnoSerie" y "vcseriales"
                System.out.println("Se encontraron 'vcseriales'. Procesar con vcnoSerie y vcseriales.");

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
        int[] opcionesIntegracion = { 15, 30, 60 }; int periodoIntegracion = opcionesIntegracion[random.nextInt(opcionesIntegracion.length)]; autoconfMedidor.setVcperiodoIntegracion(String.valueOf(periodoIntegracion));
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

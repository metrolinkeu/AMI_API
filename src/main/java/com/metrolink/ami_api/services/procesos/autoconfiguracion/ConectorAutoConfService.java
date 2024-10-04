package com.metrolink.ami_api.services.procesos.autoconfiguracion;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.JsonNode;

import com.metrolink.ami_api.models.medidor.Medidores;
import com.metrolink.ami_api.models.primeraLectura.AutoConfCanalesPerfilCarga;
import com.metrolink.ami_api.models.primeraLectura.AutoConfCodigosObisCanal;
import com.metrolink.ami_api.models.primeraLectura.AutoconfMedidor;
import com.metrolink.ami_api.models.procesos.ejecucionesLecturas.EjecucionesLecturaAutoConf;

import com.metrolink.ami_api.models.procesos.ejecucionesLecturas.EjecucionesLecturas;

import com.metrolink.ami_api.services.medidor.MedidoresService;
import com.metrolink.ami_api.services.procesos.ejecucionesLecturas.EjecucionesLectHandlerService;
import com.metrolink.ami_api.services.procesos.ejecucionesLecturas.EjecucionesLecturasService;

import org.springframework.stereotype.Service;

@Service
public class ConectorAutoConfService {

    @Autowired
    private MedidoresService medidoresService;

    @Autowired
    private EjecucionesLecturasService ejecucionesLecturasService;

    @Autowired
    private EjecucionesLectHandlerService ejecucionesLectHandlerService;

    @Transactional
    public List<AutoconfMedidor> UsarConectorAutoConfMed(String vcnoSerie, JsonNode rootNode) {

        System.out.println("Caso 1: vcnoSerie.");
        Random random = new Random();

        List<Medidores> medidores = new ArrayList<>();
        List<AutoconfMedidor> autoconfMedidores = new ArrayList<>();

        EjecucionesLecturas ejecucionLecturaToSave = new EjecucionesLecturas();
        ejecucionLecturaToSave.setNidEjecucionLectura(0L);
        EjecucionesLecturas ejecucionLecturaSaved = ejecucionesLecturasService.save(ejecucionLecturaToSave, false);
        EjecucionesLecturas ejecucionLectura = ejecucionesLecturasService
                .findById(ejecucionLecturaSaved.getNidEjecucionLectura());

        ejecucionLectura.setDinicioEjecucionLectura(new Timestamp(System.currentTimeMillis()));
        ejecucionLectura.setNintentoLecturaNumero(1);
        ejecucionLectura.setNidAnteriorIntentoEjecucionLectura((long) 0);

        EjecucionesLecturaAutoConf ejecucionLecturaAutoConf = new EjecucionesLecturaAutoConf();
        ejecucionLecturaAutoConf
                .setVcdescripcionAutoconf("Auto Configuracion de medidores por concentrador: " + vcnoSerie);
        ejecucionLecturaAutoConf.setVcnoserie(vcnoSerie);
        // ejecucionLecturaAutoConf.setVcserie("vcserie");
        ejecucionLecturaAutoConf.setJsequiposAutoconfigurar(rootNode);

        ejecucionLectura.setEjecucionLecturaAutoConf(ejecucionLecturaAutoConf);

        //////// <----------------
        try {

            ejecucionesLectHandlerService.EnviarAEjecucionesLectHandler(ejecucionLectura);

            medidores = medidoresService.findByConcentradorVcnoSerie(vcnoSerie);
            for (Medidores medidor : medidores) {
                AutoconfMedidor autoconfMedidor = crearAutoconfMedidor(medidor.getVcSerie(), random);
                autoconfMedidores.add(autoconfMedidor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //////// <----------------

        if (!medidores.isEmpty()) {
            ejecucionLecturaAutoConf.setLobtencionAutoConfOK(true);

        } else {
            ejecucionLecturaAutoConf.setLobtencionAutoConfOK(false);

        }

        ejecucionLectura.setDfinEjecucionLectura(new Timestamp(System.currentTimeMillis()));

        ejecucionLectura.setEjecucionLecturaAutoConf(ejecucionLecturaAutoConf);

        return autoconfMedidores;
    }

    @Transactional
    public AutoconfMedidor UsarConectorAutoConfMed_solo(String vcserie, String vcnoSerie, String vcSIC,
            JsonNode vcserialesNode, JsonNode rootNode) {

        AutoconfMedidor autoconfMedidor = new AutoconfMedidor();

        Random random = new Random();

        // Verificar y manejar el caso 2: vcnoSerie y vcseriales están presentes
        if (vcserialesNode != null && !vcserialesNode.isEmpty() && vcnoSerie != null && !vcnoSerie.equals("")
                && (vcSIC == null || vcSIC.equals(""))) {
            System.out.println("Caso 2: vcnoSerie y vcseriales están presentes.");

            EjecucionesLecturas ejecucionLecturaToSave = new EjecucionesLecturas();
            ejecucionLecturaToSave.setNidEjecucionLectura(0L);
            EjecucionesLecturas ejecucionLecturaSaved = ejecucionesLecturasService.save(ejecucionLecturaToSave, false);
            EjecucionesLecturas ejecucionLectura = ejecucionesLecturasService
                    .findById(ejecucionLecturaSaved.getNidEjecucionLectura());

            ejecucionLectura.setNidAnteriorIntentoEjecucionLectura((long) 0);
            ejecucionLectura.setDinicioEjecucionLectura(new Timestamp(System.currentTimeMillis()));
            ejecucionLectura.setNintentoLecturaNumero(1);

            EjecucionesLecturaAutoConf ejecucionLecturaAutoConf = new EjecucionesLecturaAutoConf();
            ejecucionLecturaAutoConf.setVcdescripcionAutoconf(
                    "Auto Configuracion de medidor: " + vcserie + " por concentrador: " + vcnoSerie);
            ejecucionLecturaAutoConf.setVcnoserie(vcnoSerie);
            ejecucionLecturaAutoConf.setVcserie(vcserie);
            ejecucionLecturaAutoConf.setJsequiposAutoconfigurar(rootNode);

            ejecucionLectura.setEjecucionLecturaAutoConf(ejecucionLecturaAutoConf);

            //////// <----------------

            ejecucionesLectHandlerService.EnviarAEjecucionesLectHandler(ejecucionLectura);

            autoconfMedidor = crearAutoconfMedidor(vcserie, random);

            //////// <----------------

            if (autoconfMedidor.getVcSerie() != null) {
                ejecucionLecturaAutoConf.setLobtencionAutoConfOK(true);

            } else {
                ejecucionLecturaAutoConf.setLobtencionAutoConfOK(false);

            }

            ejecucionLectura.setEjecucionLecturaAutoConf(ejecucionLecturaAutoConf);

            ejecucionLectura.setDfinEjecucionLectura(new Timestamp(System.currentTimeMillis()));
        }
        // Verificar y manejar el caso 3: Solo vcseriales está presente
        else if (vcserialesNode != null && !vcserialesNode.isEmpty() && (vcnoSerie == null || vcnoSerie.equals(""))
                && (vcSIC == null || vcSIC.equals(""))) {
            System.out.println("Caso 3: Solo vcseriales está presente.");

            EjecucionesLecturas ejecucionLecturaToSave = new EjecucionesLecturas();
            ejecucionLecturaToSave.setNidEjecucionLectura(0L);
            EjecucionesLecturas ejecucionLecturaSaved = ejecucionesLecturasService.save(ejecucionLecturaToSave, false);
            EjecucionesLecturas ejecucionLectura = ejecucionesLecturasService
                    .findById(ejecucionLecturaSaved.getNidEjecucionLectura());

            ejecucionLectura.setNidAnteriorIntentoEjecucionLectura((long) 0);
            ejecucionLectura.setDinicioEjecucionLectura(new Timestamp(System.currentTimeMillis()));
            ejecucionLectura.setNintentoLecturaNumero(1);

            EjecucionesLecturaAutoConf ejecucionLecturaAutoConf = new EjecucionesLecturaAutoConf();
            ejecucionLecturaAutoConf.setVcdescripcionAutoconf("Auto Configuracion de medidor: " + vcserie);
            // ejecucionLecturaAutoConf.setVcnoserie("vcnoSerie");
            ejecucionLecturaAutoConf.setVcserie(vcserie);
            ejecucionLecturaAutoConf.setJsequiposAutoconfigurar(rootNode);

            ejecucionLectura.setEjecucionLecturaAutoConf(ejecucionLecturaAutoConf);

            //////// <----------------

            ejecucionesLectHandlerService.EnviarAEjecucionesLectHandler(ejecucionLectura);

            autoconfMedidor = crearAutoconfMedidor(vcserie, random);

            //////// <----------------

            if (autoconfMedidor.getVcSerie() != null) {
                ejecucionLecturaAutoConf.setLobtencionAutoConfOK(true);

            } else {
                ejecucionLecturaAutoConf.setLobtencionAutoConfOK(false);

            }

            ejecucionLectura.setEjecucionLecturaAutoConf(ejecucionLecturaAutoConf);

            ejecucionLectura.setDfinEjecucionLectura(new Timestamp(System.currentTimeMillis()));

        }
        // Verificar y manejar el caso 4: vcnoSerie y SIC están presentes
        else if ((vcserialesNode == null || vcserialesNode.isEmpty()) && vcnoSerie != null && !vcnoSerie.equals("")
                && vcSIC != null
                && !vcSIC.equals("")) {
            System.out.println("Caso 4: vcnoSerie y SIC están presentes.");

            EjecucionesLecturas ejecucionLecturaToSave = new EjecucionesLecturas();
            ejecucionLecturaToSave.setNidEjecucionLectura(0L);
            EjecucionesLecturas ejecucionLecturaSaved = ejecucionesLecturasService.save(ejecucionLecturaToSave, false);
            EjecucionesLecturas ejecucionLectura = ejecucionesLecturasService
                    .findById(ejecucionLecturaSaved.getNidEjecucionLectura());

            ejecucionLectura.setNidAnteriorIntentoEjecucionLectura((long) 0);
            ejecucionLectura.setDinicioEjecucionLectura(new Timestamp(System.currentTimeMillis()));
            ejecucionLectura.setNintentoLecturaNumero(1);

            EjecucionesLecturaAutoConf ejecucionLecturaAutoConf = new EjecucionesLecturaAutoConf();
            ejecucionLecturaAutoConf.setVcdescripcionAutoconf("Auto Configuracion de medidor: " + vcserie
                    + " por concentrador " + vcnoSerie + " con SIC: " + vcSIC);
            ejecucionLecturaAutoConf.setVcnoserie(vcnoSerie);
            ejecucionLecturaAutoConf.setVcserie(vcserie);
            ejecucionLecturaAutoConf.setJsequiposAutoconfigurar(rootNode);

            ejecucionLectura.setEjecucionLecturaAutoConf(ejecucionLecturaAutoConf);

            //////// <----------------

            ejecucionesLectHandlerService.EnviarAEjecucionesLectHandler(ejecucionLectura);

            autoconfMedidor = crearAutoconfMedidor(vcserie, random);

            //////// <----------------

            if (autoconfMedidor.getVcSerie() != null) {
                ejecucionLecturaAutoConf.setLobtencionAutoConfOK(true);

            } else {
                ejecucionLecturaAutoConf.setLobtencionAutoConfOK(false);

            }

            ejecucionLectura.setEjecucionLecturaAutoConf(ejecucionLecturaAutoConf);

            ejecucionLectura.setDfinEjecucionLectura(new Timestamp(System.currentTimeMillis()));
        }
        // Verificar y manejar el caso 5: Solo SIC está presente
        else if ((vcserialesNode == null || vcserialesNode.isEmpty()) && (vcnoSerie == null || vcnoSerie.equals(""))
                && vcSIC != null
                && !vcSIC.equals("")) {
            System.out.println("Caso 5: Solo SIC está presente.");

            EjecucionesLecturas ejecucionLecturaToSave = new EjecucionesLecturas();
            ejecucionLecturaToSave.setNidEjecucionLectura(0L);
            EjecucionesLecturas ejecucionLecturaSaved = ejecucionesLecturasService.save(ejecucionLecturaToSave, false);
            EjecucionesLecturas ejecucionLectura = ejecucionesLecturasService
                    .findById(ejecucionLecturaSaved.getNidEjecucionLectura());

            ejecucionLectura.setNidAnteriorIntentoEjecucionLectura((long) 0);
            ejecucionLectura.setDinicioEjecucionLectura(new Timestamp(System.currentTimeMillis()));
            ejecucionLectura.setNintentoLecturaNumero(1);

            EjecucionesLecturaAutoConf ejecucionLecturaAutoConf = new EjecucionesLecturaAutoConf();
            ejecucionLecturaAutoConf.setVcdescripcionAutoconf("Auto Configuracion de medidor: " + vcserie
                    + " con SIC: " + vcSIC);
            // ejecucionLecturaAutoConf.setVcnoserie(vcnoSerie);
            ejecucionLecturaAutoConf.setVcserie(vcserie);
            ejecucionLecturaAutoConf.setJsequiposAutoconfigurar(rootNode);

            ejecucionLectura.setEjecucionLecturaAutoConf(ejecucionLecturaAutoConf);

            //////// <----------------

            ejecucionesLectHandlerService.EnviarAEjecucionesLectHandler(ejecucionLectura);

            autoconfMedidor = crearAutoconfMedidor(vcserie, random);

            //////// <----------------

            if (autoconfMedidor.getVcSerie() != null) {
                ejecucionLecturaAutoConf.setLobtencionAutoConfOK(true);

            } else {
                ejecucionLecturaAutoConf.setLobtencionAutoConfOK(false);
            }

            ejecucionLectura.setEjecucionLecturaAutoConf(ejecucionLecturaAutoConf);

            ejecucionLectura.setDfinEjecucionLectura(new Timestamp(System.currentTimeMillis()));

        }

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

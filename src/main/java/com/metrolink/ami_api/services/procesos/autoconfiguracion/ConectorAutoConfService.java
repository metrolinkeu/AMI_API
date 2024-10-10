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

import com.metrolink.ami_api.services.procesos.ejecucionesLecturas.EjecucionesLectHandlerService;
import com.metrolink.ami_api.services.procesos.ejecucionesLecturas.EjecucionesLecturasService;

import org.springframework.stereotype.Service;

@Service
public class ConectorAutoConfService {

    @Autowired
    private EjecucionesLecturasService ejecucionesLecturasService;

    @Autowired
    private EjecucionesLectHandlerService ejecucionesLectHandlerService;

    @Transactional
    public List<AutoconfMedidor> UsarConectorAutoConfMed(String vcnoSerie, JsonNode rootNode) {

        System.out.println("Caso 1,2 y 4: vcnoSerie.");

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

            Object resultado = ejecucionesLectHandlerService.EnviarAEjecucionesLectHandler(ejecucionLectura);

            if (resultado instanceof List<?>) {
                List<?> lista = (List<?>) resultado;

                // Verificar que todos los elementos en la lista son de tipo AutoconfMedidor
                if (!lista.isEmpty() && lista.get(0) instanceof AutoconfMedidor) {
                    boolean esValida = true;
                    for (Object obj : lista) {
                        if (!(obj instanceof AutoconfMedidor)) {
                            esValida = false;
                            break;
                        }
                    }
                    if (esValida) {
                        autoconfMedidores = (List<AutoconfMedidor>) lista;
                    } else {
                        System.out.println("La lista contiene elementos que no son de tipo AutoconfMedidor.");
                    }
                }
            } else {
                System.out.println("El resultado es de un tipo inesperado.");
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

    
        // Verificar y manejar el caso 3: Solo vcseriales está presente
        if (vcserialesNode != null && !vcserialesNode.isEmpty() && (vcnoSerie == null || vcnoSerie.equals(""))
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

            Object resultado = ejecucionesLectHandlerService.EnviarAEjecucionesLectHandler(ejecucionLectura);

            // Si esperas un AutoconfMedidor
            if (resultado instanceof AutoconfMedidor) {
                autoconfMedidor = (AutoconfMedidor) resultado;
                System.out.println(autoconfMedidor.getVcSerie());
            }

           
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

            Object resultado = ejecucionesLectHandlerService.EnviarAEjecucionesLectHandler(ejecucionLectura);

            // Si esperas un AutoconfMedidor
            if (resultado instanceof AutoconfMedidor) {
                autoconfMedidor = (AutoconfMedidor) resultado;
                System.out.println(autoconfMedidor.getVcSerie());
            }

            // autoconfMedidor = crearAutoconfMedidor(vcserie, random);

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

 

}

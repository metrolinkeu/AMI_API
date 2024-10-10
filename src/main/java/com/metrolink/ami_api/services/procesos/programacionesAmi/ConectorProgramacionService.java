package com.metrolink.ami_api.services.procesos.programacionesAmi;

import java.sql.Timestamp;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.metrolink.ami_api.models.medidor.Medidores;
import com.metrolink.ami_api.models.procesos.ejecucionesLecturas.EjecucionesLecturaProg;
import com.metrolink.ami_api.models.procesos.ejecucionesLecturas.EjecucionesLecturas;
import com.metrolink.ami_api.models.procesos.programacionesAmi.ProgramacionesAMI;
import com.metrolink.ami_api.services.medidor.MedidoresService;
import com.metrolink.ami_api.services.procesos.ejecucionesLecturas.EjecucionesLectHandlerService;
import com.metrolink.ami_api.services.procesos.ejecucionesLecturas.EjecucionesLecturasService;

@Service
public class ConectorProgramacionService {

    @Autowired
    private MedidoresService medidoresService;

    @Autowired
    private EjecucionesLecturasService ejecucionesLecturasService;

    @Autowired
    private EjecucionesLectHandlerService ejecucionesLectHandlerService;

    @Transactional
    public String UsarConectorProgramacionFiltroConcentrador(String mensaje, ProgramacionesAMI programacionAMI,
            String vcSeriesAReintentarFiltrado, int reintentosRestantes) {

        System.out.println(mensaje);
        String vcnoSerie = programacionAMI.getGrupoMedidores().getVcidentificador();

        EjecucionesLecturas ejecucionLecturaToSave = new EjecucionesLecturas();
        ejecucionLecturaToSave.setNidEjecucionLectura(0L);
        EjecucionesLecturas ejecucionLecturaSaved = ejecucionesLecturasService.save(ejecucionLecturaToSave, false);
        EjecucionesLecturas ejecucionLectura = ejecucionesLecturasService
                .findById(ejecucionLecturaSaved.getNidEjecucionLectura());

        ejecucionLectura.setDinicioEjecucionLectura(new Timestamp(System.currentTimeMillis()));
        ejecucionLectura.setNintentoLecturaNumero(
                programacionAMI.getParametrizacionProg().getNreintentos() - reintentosRestantes);

        if (programacionAMI.getParametrizacionProg().getNreintentos() - reintentosRestantes == 1) {
            ejecucionLectura.setNidAnteriorIntentoEjecucionLectura(0L);
        } else {

            EjecucionesLecturas ejecucionLast = ejecucionesLecturasService
                    .findLastByDescripcionProg("Lectura de medidores programados en la programacion: "
                            + programacionAMI.getNcodigo() + " por concentrador: " + vcnoSerie);

            ejecucionLectura.setNidAnteriorIntentoEjecucionLectura(ejecucionLast.getNidEjecucionLectura());//
        }

        EjecucionesLecturaProg ejecucionLecturaProg = new EjecucionesLecturaProg();
        ejecucionLecturaProg.setVcdescripcionProg("Lectura de medidores programados en la programacion: "
                + programacionAMI.getNcodigo() + " por concentrador: " + vcnoSerie);

        // ejecucionLecturaProg.setVcserie("vcserie");
        ejecucionLecturaProg.setVcnoserie(vcnoSerie);
        ejecucionLecturaProg.setProgramacionAMI(programacionAMI);

        String jsseriesMed = "";
        if (!"EstadoInicio".equalsIgnoreCase(vcSeriesAReintentarFiltrado)) {
            jsseriesMed = vcSeriesAReintentarFiltrado;
            System.out.println("jsseriesMed: " + jsseriesMed);
        } else {
            List<Medidores> medidores = medidoresService
                    .findByConcentradorVcnoSerie(programacionAMI.getGrupoMedidores().getVcidentificador());
            jsseriesMed = "["; // Iniciamos con el formato de array
            StringBuilder tempBuilder = new StringBuilder();
            medidores.forEach(medidor -> {
                String vcSerie = medidor.getVcSerie();
                if (tempBuilder.length() > 0) {
                    tempBuilder.append(", ");
                }
                tempBuilder.append("\"").append(vcSerie).append("\"");
            });
            // Asignamos el valor final a la variable jsseriesMed con el formato adecuado
            jsseriesMed += tempBuilder.toString() + "]";
            System.out.println("jsseriesMed: " + jsseriesMed);
        }

        ejecucionLecturaProg.setJsseriesMed(jsseriesMed);
        ejecucionLectura.setEjecucionLecturaProg(ejecucionLecturaProg);

        //// <----------------------------

        ejecucionesLectHandlerService.EnviarAEjecucionesLectHandler(ejecucionLectura);

        String medidor1 = "19014";
        String medidoresFaltantesPorLeer = String.format("[\"%s\", \"15913\", \"61452\"]", medidor1);
        // String medidoresFaltantesPorLeer = "[]";

        //// <----------------------------

        if (!medidoresFaltantesPorLeer.equals("[]")) {
            ejecucionLecturaProg.setLlecturaOK(false);
        } else {
            ejecucionLecturaProg.setLlecturaOK(true);
        }

        ejecucionLecturaProg.setJsmedidoresFaltantesPorLeer(medidoresFaltantesPorLeer);
        ejecucionLectura.setEjecucionLecturaProg(ejecucionLecturaProg);

        ejecucionLectura.setDfinEjecucionLectura(new Timestamp(System.currentTimeMillis()));

        return medidoresFaltantesPorLeer;
        // return "[]";
    }

    @Transactional
    public String UsarConectorProgramacionFiltroConyMed(String mensaje, ProgramacionesAMI programacionAMI,
            String vcSeriesAReintentarFiltrado, int reintentosRestantes) {

        System.out.println(mensaje);
        String vcnoSerie = programacionAMI.getGrupoMedidores().getVcidentificador();

        EjecucionesLecturas ejecucionLecturaToSave = new EjecucionesLecturas();
        ejecucionLecturaToSave.setNidEjecucionLectura(0L);
        EjecucionesLecturas ejecucionLecturaSaved = ejecucionesLecturasService.save(ejecucionLecturaToSave, false);
        EjecucionesLecturas ejecucionLectura = ejecucionesLecturasService
                .findById(ejecucionLecturaSaved.getNidEjecucionLectura());

        ejecucionLectura.setDinicioEjecucionLectura(new Timestamp(System.currentTimeMillis()));
        ejecucionLectura.setNintentoLecturaNumero(
                programacionAMI.getParametrizacionProg().getNreintentos() - reintentosRestantes);

        if (programacionAMI.getParametrizacionProg().getNreintentos() - reintentosRestantes == 1) {
            ejecucionLectura.setNidAnteriorIntentoEjecucionLectura(0L);
        } else {

            EjecucionesLecturas ejecucionLast = ejecucionesLecturasService
                    .findLastByDescripcionProg("Lectura de medidores programados en la programacion: "
                            + programacionAMI.getNcodigo() + " por concentrador: " + vcnoSerie);

            ejecucionLectura.setNidAnteriorIntentoEjecucionLectura(ejecucionLast.getNidEjecucionLectura());//
        }

        EjecucionesLecturaProg ejecucionLecturaProg = new EjecucionesLecturaProg();
        ejecucionLecturaProg.setVcdescripcionProg("Lectura de medidores programados en la programacion: "
                + programacionAMI.getNcodigo() + " por concentrador: " + vcnoSerie);

        ejecucionLecturaProg.setVcnoserie(vcnoSerie);
        ejecucionLecturaProg.setProgramacionAMI(programacionAMI);

        String jsseriesMed = "";
        if (!"EstadoInicio".equalsIgnoreCase(vcSeriesAReintentarFiltrado)) {
            jsseriesMed = vcSeriesAReintentarFiltrado;
        } else {
            jsseriesMed = programacionAMI.getGrupoMedidores().getJsseriesMed();
        }

        ejecucionLecturaProg.setJsseriesMed(jsseriesMed);
        ejecucionLectura.setEjecucionLecturaProg(ejecucionLecturaProg);

        //// <----------------------------

        ejecucionesLectHandlerService.EnviarAEjecucionesLectHandler(ejecucionLectura);

        String medidor1 = "19014";
        String medidoresFaltantesPorLeer = String.format("[\"%s\", \"15913\", \"61452\"]", medidor1);

        //// <----------------------------

        if (!medidoresFaltantesPorLeer.equals("[]")) {
            ejecucionLecturaProg.setLlecturaOK(false);
        } else {
            ejecucionLecturaProg.setLlecturaOK(true);
        }

        ejecucionLecturaProg.setJsmedidoresFaltantesPorLeer(medidoresFaltantesPorLeer);
        ejecucionLectura.setEjecucionLecturaProg(ejecucionLecturaProg);

        ejecucionLectura.setDfinEjecucionLectura(new Timestamp(System.currentTimeMillis()));

        return medidoresFaltantesPorLeer;

    }

    @Transactional
    public String UsarConectorProgramacionFiltroMedidores(String mensaje, ProgramacionesAMI programacionAMI,
            String vcserie, int reintentosRestantes) {

        String vcSerieAReintentar = "";

        System.out.println(mensaje);

        EjecucionesLecturas ejecucionLecturaToSave = new EjecucionesLecturas();
        ejecucionLecturaToSave.setNidEjecucionLectura(0L);
        EjecucionesLecturas ejecucionLecturaSaved = ejecucionesLecturasService.save(ejecucionLecturaToSave, false);
        EjecucionesLecturas ejecucionLectura = ejecucionesLecturasService
                .findById(ejecucionLecturaSaved.getNidEjecucionLectura());

        ejecucionLectura.setDinicioEjecucionLectura(new Timestamp(System.currentTimeMillis()));
        ejecucionLectura.setNintentoLecturaNumero(
                programacionAMI.getParametrizacionProg().getNreintentos() - reintentosRestantes);

        if (programacionAMI.getParametrizacionProg().getNreintentos() - reintentosRestantes == 1) {
            ejecucionLectura.setNidAnteriorIntentoEjecucionLectura(0L);
        } else {

            EjecucionesLecturas ejecucionLast = ejecucionesLecturasService
                    .findLastByDescripcionProg("Lectura de medidor programado en la programacion: "
                            + programacionAMI.getNcodigo() + " con numero de serie: " + vcserie);

            ejecucionLectura.setNidAnteriorIntentoEjecucionLectura(ejecucionLast.getNidEjecucionLectura());//
        }

        EjecucionesLecturaProg ejecucionLecturaProg = new EjecucionesLecturaProg();
        ejecucionLecturaProg.setVcdescripcionProg("Lectura de medidor programado en la programacion: "
                + programacionAMI.getNcodigo() + " con numero de serie: " + vcserie);

        ejecucionLecturaProg.setVcserie(vcserie);
        // ejecucionLecturaProg.setVcnoserie(vcnoSerie);
        ejecucionLecturaProg.setProgramacionAMI(programacionAMI);
        // ejecucionLecturaProg.setJsseriesMed(jsseriesMed);
        ejecucionLectura.setEjecucionLecturaProg(ejecucionLecturaProg);

        //// <----------------------------

        Object resultado = ejecucionesLectHandlerService.EnviarAEjecucionesLectHandler(ejecucionLectura);

        if (resultado instanceof String) {
            vcSerieAReintentar = (String) resultado;
            // System.out.println(newJson);
        }

        //// <----------------------------

        if (!vcSerieAReintentar.equals("")) {
            ejecucionLecturaProg.setLlecturaOK(false);
            ejecucionLecturaProg.setJsmedidoresFaltantesPorLeer(String.format("[\"%s\"]", vcSerieAReintentar));
        } else {
            ejecucionLecturaProg.setLlecturaOK(true);
            ejecucionLecturaProg.setJsmedidoresFaltantesPorLeer("[]");
        }

        ejecucionLectura.setEjecucionLecturaProg(ejecucionLecturaProg);

        ejecucionLectura.setDfinEjecucionLectura(new Timestamp(System.currentTimeMillis()));

        return vcSerieAReintentar;
    }

    @Transactional
    public String UsarConectorProgramacionFiltroSIC(String mensaje, ProgramacionesAMI programacionAMI, String vcserie,
            int reintentosRestantes) {

        String vcSerieAReintentar = "";

        System.out.println(mensaje);

        EjecucionesLecturas ejecucionLecturaToSave = new EjecucionesLecturas();
        ejecucionLecturaToSave.setNidEjecucionLectura(0L);
        EjecucionesLecturas ejecucionLecturaSaved = ejecucionesLecturasService.save(ejecucionLecturaToSave, false);
        EjecucionesLecturas ejecucionLectura = ejecucionesLecturasService
                .findById(ejecucionLecturaSaved.getNidEjecucionLectura());

        ejecucionLectura.setDinicioEjecucionLectura(new Timestamp(System.currentTimeMillis()));
        ejecucionLectura.setNintentoLecturaNumero(
                programacionAMI.getParametrizacionProg().getNreintentos() - reintentosRestantes);

        if (programacionAMI.getParametrizacionProg().getNreintentos() - reintentosRestantes == 1) {
            ejecucionLectura.setNidAnteriorIntentoEjecucionLectura(0L);
        } else {

            EjecucionesLecturas ejecucionLast = ejecucionesLecturasService
                    .findLastByDescripcionProg("Lectura de medidor programado en la programacion: "
                            + programacionAMI.getNcodigo() + " con numero de serie: " + vcserie);

            ejecucionLectura.setNidAnteriorIntentoEjecucionLectura(ejecucionLast.getNidEjecucionLectura());//
        }

        EjecucionesLecturaProg ejecucionLecturaProg = new EjecucionesLecturaProg();
        ejecucionLecturaProg.setVcdescripcionProg("Lectura de medidor programado en la programacion: "
                + programacionAMI.getNcodigo() + " con numero de serie: " + vcserie);

        ejecucionLecturaProg.setVcserie(vcserie);
        // ejecucionLecturaProg.setVcnoserie(vcnoSerie);
        ejecucionLecturaProg.setProgramacionAMI(programacionAMI);
        // ejecucionLecturaProg.setJsseriesMed(jsseriesMed);
        ejecucionLectura.setEjecucionLecturaProg(ejecucionLecturaProg);

        //// <----------------------------

        Object resultado = ejecucionesLectHandlerService.EnviarAEjecucionesLectHandler(ejecucionLectura);

        if (resultado instanceof String) {
            vcSerieAReintentar = (String) resultado;
            // System.out.println(newJson);
        }

        //// <----------------------------

        if (!vcSerieAReintentar.equals("")) {
            ejecucionLecturaProg.setLlecturaOK(false);
            ejecucionLecturaProg.setJsmedidoresFaltantesPorLeer(String.format("[\"%s\"]", vcSerieAReintentar));
        } else {
            ejecucionLecturaProg.setLlecturaOK(true);
            ejecucionLecturaProg.setJsmedidoresFaltantesPorLeer("[]");
        }

        ejecucionLectura.setEjecucionLecturaProg(ejecucionLecturaProg);

        ejecucionLectura.setDfinEjecucionLectura(new Timestamp(System.currentTimeMillis()));

        return vcSerieAReintentar;

    }

}

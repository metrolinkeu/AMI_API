package com.metrolink.ami_api.services.procesos.programacionesAmi;


import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.metrolink.ami_api.models.concentrador.Concentradores;
import com.metrolink.ami_api.models.medidor.Medidores;
import com.metrolink.ami_api.models.procesos.programacionesAmi.ProgramacionesAMI;
import com.metrolink.ami_api.services.concentrador.ConcentradoresService;
import com.metrolink.ami_api.services.medidor.MedidoresService;

@Service
public class ConectorProgramacionService {

    @Autowired
    private ConcentradoresService concentradoresService;

    @Autowired
    private MedidoresService medidoresService;

    public String UsarConectorProgramacionFiltroConcentrador(String mensaje, ProgramacionesAMI programacionAMI,
            String vcSeriesAReintentarFiltrado) {

        System.out.println(mensaje);

        String jsseriesMed = "";

        // Este caso se usa para leer los medidores a traves del concentrador asociado
        // en:
        // List<Medidores> medidores =
        // medidoresService.findByConcentradorVcnoSerie(programacionAMI.getGrupoMedidores().getVcidentificador());

        // por otra parte, a traves de un condicional se revisa si lee todos los
        // medidores
        // o si solo lee los medidores que vienen en vcSeriesAReintentarFiltrado los
        // cuales
        // son producto de haber intentado leer, pero no logrando leerlos todos
        // Estos no leidos vienen en: vcSeriesAReintentarFiltrado

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

                // Agregamos cada vcSerie al StringBuilder temporal
                if (tempBuilder.length() > 0) {
                    tempBuilder.append(", ");
                }
                tempBuilder.append("\"").append(vcSerie).append("\"");
            });

            // Asignamos el valor final a la variable jsseriesMed con el formato adecuado
            jsseriesMed += tempBuilder.toString() + "]";
            System.out.println("jsseriesMed: " + jsseriesMed);
        }

        // Convertir la cadena JSON a una lista de strings
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> jsseriesMedList;

        try {
            jsseriesMedList = objectMapper.readValue(jsseriesMed, new TypeReference<List<String>>() {
            });

            // Verificar si la lista contiene elementos
            if (jsseriesMedList.isEmpty()) {
                System.out.println("No se encontraron series en la lista.");
            } else {
                // Iterar sobre la lista y procesar cada serie
                for (int i = 0; i < jsseriesMedList.size(); i++) {
                    System.out.println("vcserie" + (i + 1) + ": " + jsseriesMedList.get(i));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error al procesar medidores faltantes";
        }

        //
        // Lugar para pedir esto en Realidad
        //

        // Esto ultimo no tiene es inventado, no tiene relacion con la logica de arriba,
        // solo
        // devuelve un resultado similar al que deberia de entregar

        String medidor1 = "19014";

        String medidoresFaltantesPorLeer = String.format("[\"%s\", \"15913\", \"61452\"]", medidor1);

        return medidoresFaltantesPorLeer;
        //return "[]";

    }

    public String UsarConectorProgramacionFiltroConyMed(String mensaje, ProgramacionesAMI programacionAMI,
            String vcSeriesAReintentarFiltrado) {

        System.out.println(mensaje);

        String jsseriesMed = "";

        // Este caso se usa para leer los medidores a traves de un concentrador el cual
        // viene con ip y puerto:

        String vcnoSerie = programacionAMI.getGrupoMedidores().getVcidentificador();
        Concentradores concentrador = concentradoresService.findById(vcnoSerie);

        String ip = concentrador.getParamTiposDeComunicacion().getVcip();
        String puerto = concentrador.getParamTiposDeComunicacion().getVcpuerto();
        System.out.println("IP del concentrador: " + ip);
        System.out.println("Puerto del concentrador: " + puerto);

        // en este momento se revisa si se deben de leer todos los medidores de la
        // programacion
        // o si es un reintento y solo se leeran los medidores que estan faltantdo

        // Si son medidores que estan faltando se leen los medidores de
        // vcSeriesAReintentarFiltrado

        if (!"EstadoInicio".equalsIgnoreCase(vcSeriesAReintentarFiltrado)) {
            // en caso de que ya se haya hecho un intento la lectura de los
            // medidores se hace igualmente a traves del concentrador pero
            // esta vez solo los medidores que no ha sido leidos que vienen en:
            jsseriesMed = vcSeriesAReintentarFiltrado;

            // Si en cambio es la primera vez que se usa esta funcion los medidores a leeer
            // seran
            // todos los que estan en la programacon a a traves de
            // programacionAMI.getGrupoMedidores().getJsseriesMed()
        } else {
            // Lectura de los medidores a traves del concentrador que viene en:
            jsseriesMed = programacionAMI.getGrupoMedidores().getJsseriesMed();
        }

        // Convertir la cadena JSON a una lista de strings
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> jsseriesMedList;

        try {
            jsseriesMedList = objectMapper.readValue(jsseriesMed, new TypeReference<List<String>>() {
            });

            // Verificar si la lista contiene elementos
            if (jsseriesMedList.isEmpty()) {
                System.out.println("No se encontraron series en la lista.");
            } else {
                // Iterar sobre la lista y procesar cada serie
                for (int i = 0; i < jsseriesMedList.size(); i++) {
                    System.out.println("vcserie" + (i + 1) + ": " + jsseriesMedList.get(i));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error al procesar medidores faltantes";
        }

        //
        // Lugar para pedir esto en Realidad
        //

        // Esto ultimo no tiene es inventado, no tiene relacion con la logica de arriba,
        // solo
        // devuelve un resultado similar al que deberia de entregar
        String medidor1 = jsseriesMedList.get(0); // Variables de pruebas
        String medidoresFaltantesPorLeer = String.format("[\"%s\", \"15913\", \"61452\"]", medidor1);
        return medidoresFaltantesPorLeer;

    }

    public String UsarConectorProgramacionFiltroMedidores(String mensaje, ProgramacionesAMI programacionAMI, String vcserie) {

        String vcSerieAReintentar = "";

        System.out.println(mensaje);

        Medidores medidor = medidoresService.findById(vcserie);

        // lectura de un medidor en especifico,
        // EL medidor a leer viene en vcserie
        // lo que se le va a pedir al medidor viene en

        boolean llectura_perfil_1 = programacionAMI.getListaPeticiones().isLlectura_perfil_1();
        boolean leventos = programacionAMI.getListaPeticiones().isLeventos();
        boolean lregistros = programacionAMI.getListaPeticiones().isLregistros();
        boolean lfactorPotencia = programacionAMI.getListaPeticiones().isLfactorPotencia();
        boolean linstantaneos = programacionAMI.getListaPeticiones().isLinstantaneos();
        String vcaccionRele = programacionAMI.getListaPeticiones().getVcaccionRele();
        java.sql.Timestamp dfechaHoraSincronizacion = programacionAMI.getListaPeticiones()
                .getDfechaHoraSincronizacion();

        // Imprimir las variables
        System.out.println("llectura_perfil_1: " + llectura_perfil_1);
        System.out.println("leventos: " + leventos);
        System.out.println("lregistros: " + lregistros);
        System.out.println("lfactorPotencia: " + lfactorPotencia);
        System.out.println("linstantaneos: " + linstantaneos);
        System.out.println("vcaccionRele: " + vcaccionRele);
        System.out.println("dfechaHoraSincronizacion: " + dfechaHoraSincronizacion);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dfechaHoraSincronizacion.toLocalDateTime().format(formatter);
        System.out.println("dfechaHoraSincronizacion: " + formattedDate);

        // en esta caso es cuando el medidor es Servidor.
        // En este punto ya se esta en una cola e hilo unicos para este medidor la cual
        // fue construida de acuerdo a dos casos:
        // Este medidor esta asociado a un concentrador:
        // aqui se debe de hacer las eticiones a traves del concentrador con su ip y
        // puerto:

        if (medidor.getConcentrador() != null) {

            String vcnoSerie = medidor.getConcentrador().getVcnoSerie();
            Concentradores concentrador = concentradoresService.findById(vcnoSerie);

            String ip = concentrador.getParamTiposDeComunicacion().getVcip();
            String puerto = concentrador.getParamTiposDeComunicacion().getVcpuerto();
            System.out.println("IP del concentrador: " + ip);
            System.out.println("Puerto del concentrador: " + puerto);

        } else {

            String ip = medidor.getVcip();
            String puerto = medidor.getVcpuerto();

            // Imprimir los valores
            System.out.println("IP: " + ip);
            System.out.println("Puerto: " + puerto);

        }

        //
        // construida con la ip y el puerto.
        // por lo tanto se debe de usar la misma ip y puerto que estan en:

        // pido al medidor con todo necesario

        // devuelve un Leido o no leido

        boolean Leido = true;

        if (Leido) {
            vcSerieAReintentar = vcserie;
        } else {
            vcSerieAReintentar = "";

        }

        return vcSerieAReintentar;

    }

    public String UsarConectorProgramacionFiltroSIC(String mensaje, ProgramacionesAMI programacionAMI, String vcserie) {

        String vcSerieAReintentar = "";

        System.out.println(mensaje);

        Medidores medidor = medidoresService.findById(vcserie);

        // lectura de un medidor en especifico del traido por su codigo sic,
        // EL medidor a leer viene en vcserie
        // lo que se le va a pedir al medidor viene en

        boolean llectura_perfil_1 = programacionAMI.getListaPeticiones().isLlectura_perfil_1();
        boolean leventos = programacionAMI.getListaPeticiones().isLeventos();
        boolean lregistros = programacionAMI.getListaPeticiones().isLregistros();
        boolean lfactorPotencia = programacionAMI.getListaPeticiones().isLfactorPotencia();
        boolean linstantaneos = programacionAMI.getListaPeticiones().isLinstantaneos();
        String vcaccionRele = programacionAMI.getListaPeticiones().getVcaccionRele();
        java.sql.Timestamp dfechaHoraSincronizacion = programacionAMI.getListaPeticiones()
                .getDfechaHoraSincronizacion();

        // Imprimir las variables
        System.out.println("llectura_perfil_1: " + llectura_perfil_1);
        System.out.println("leventos: " + leventos);
        System.out.println("lregistros: " + lregistros);
        System.out.println("lfactorPotencia: " + lfactorPotencia);
        System.out.println("linstantaneos: " + linstantaneos);
        System.out.println("vcaccionRele: " + vcaccionRele);
        System.out.println("dfechaHoraSincronizacion: " + dfechaHoraSincronizacion);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dfechaHoraSincronizacion.toLocalDateTime().format(formatter);
        System.out.println("dfechaHoraSincronizacion: " + formattedDate);

       // en esta caso es cuando el medidor es Servidor.
        // En este punto ya se esta en una cola e hilo unicos para este medidor la cual
        // fue construida de acuerdo a dos casos:
        // Este medidor esta asociado a un concentrador:
        // aqui se debe de hacer las eticiones a traves del concentrador con su ip y
        // puerto:

        if (medidor.getConcentrador() != null) {

            String vcnoSerie = medidor.getConcentrador().getVcnoSerie();
            Concentradores concentrador = concentradoresService.findById(vcnoSerie);

            String ip = concentrador.getParamTiposDeComunicacion().getVcip();
            String puerto = concentrador.getParamTiposDeComunicacion().getVcpuerto();
            System.out.println("IP del concentrador: " + ip);
            System.out.println("Puerto del concentrador: " + puerto);

        } else {

            String ip = medidor.getVcip();
            String puerto = medidor.getVcpuerto();

            // Imprimir los valores
            System.out.println("IP: " + ip);
            System.out.println("Puerto: " + puerto);

        }

        // pido al medidor con todo necesario

        // devuelve un Leido o no leido

        boolean Leido = true;

        if (Leido) {
            vcSerieAReintentar = vcserie;
        } else {
            vcSerieAReintentar = "";

        }

        return vcSerieAReintentar;

    }

}

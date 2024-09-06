package com.metrolink.ami_api.services.procesos.programacionesAmi;

import java.security.Timestamp;
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

    public String UsarConectorProgramacionCaso1(String mensaje, ProgramacionesAMI programacionAMI) {

        System.out.println(mensaje);

        programacionAMI.getGrupoMedidores().getVcfiltro();

        System.out.println(programacionAMI.getGrupoMedidores().getVcidentificador());

        List<Medidores> medidores = medidoresService
                .findByConcentradorVcnoSerie(programacionAMI.getGrupoMedidores().getVcidentificador());

        medidores.forEach(medidor -> {
            String vcSerie = medidor.getVcSerie();
            System.out.println("vcserie: " + vcSerie);
        });

        String medidor1 = "19014";

        String medidoresFaltantesPorLeer = String.format("[\"%s\", \"15913\", \"61452\"]", medidor1);
        return medidoresFaltantesPorLeer;

    }

    public String UsarConectorProgramacionFaltantesCaso1(String mensaje, ProgramacionesAMI programacionAMI,
            String medidoresFaltantesPorLeer_) {

        System.out.println(mensaje);

        programacionAMI.getGrupoMedidores().getVcfiltro();

        System.out.println(programacionAMI.getGrupoMedidores().getVcidentificador());

        // Convertir la cadena JSON a una lista de strings
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> medidoresFaltantesList;

        try {
            medidoresFaltantesList = objectMapper.readValue(medidoresFaltantesPorLeer_,
                    new TypeReference<List<String>>() {
                    });

            // Extraer elementos en variables individuales
            String vcserie1 = medidoresFaltantesList.get(0);
            String vcserie2 = medidoresFaltantesList.get(1);
            String vcserie3 = medidoresFaltantesList.get(2);

            System.out.println("vcserie1: " + vcserie1);
            System.out.println("vcserie2: " + vcserie2);
            System.out.println("vcserie3: " + vcserie3);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al procesar medidores faltantes";
        }
        // medidoresFaltantesPorLeer_ = "[]";

        String medidor1 = "19014";
        String medidoresFaltantesPorLeer = String.format("[\"%s\", \"15913\", \"61452\"]", medidor1);

        // return medidoresFaltantesPorLeer_;
        return medidoresFaltantesPorLeer;

    }

    public String UsarConectorProgramacionCaso2(String mensaje, ProgramacionesAMI programacionAMI,
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

        
        // en este momento se revisa si se deben de leer todos los medidores de la programacion
        // o si es un reintento y solo se leeran los medidores que estan faltantdo
        
        //Si son medidores que estan faltando se leen los medidores de vcSeriesAReintentarFiltrado

        if (!"EstadoInicio".equalsIgnoreCase(vcSeriesAReintentarFiltrado)) {
            // en caso de que ya se haya hecho un intento la lectura de los
            // medidores se hace igualmente a traves del concentrador pero
            // esta vez solo los medidores que no ha sido leidos que vienen en:
            jsseriesMed = vcSeriesAReintentarFiltrado;

        //Si en cambio es la primera vez que se usa esta funcion los medidores a leeer seran
        //todos los que estan en la programacon a a traves de programacionAMI.getGrupoMedidores().getJsseriesMed()
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

        String medidor1 = jsseriesMedList.get(0); // Variables de pruebas
        String medidoresFaltantesPorLeer = String.format("[\"%s\", \"15913\", \"61452\"]", medidor1);
        return medidoresFaltantesPorLeer;

    }

    public String UsarConectorProgramacionCaso3(String mensaje, ProgramacionesAMI programacionAMI, String vcserie) {

        String vcSerieAReintentar = "";

        System.out.println(mensaje);

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
        // fue
        // construida la ip y el puerto.
        // por lo tanto se debe de usar la misma ip y puerto que estan en:

        Medidores medidor = medidoresService.findById(vcserie);

        String ip = medidor.getVcip();
        String puerto = medidor.getVcpuerto();

        // Imprimir los valores
        System.out.println("IP: " + ip);
        System.out.println("Puerto: " + puerto);

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

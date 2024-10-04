package com.metrolink.ami_api.services.procesos.ejecucionesLecturas;

import com.metrolink.ami_api.models.procesos.ejecucionesLecturas.EjecucionesLecturas;
import org.springframework.stereotype.Service;

@Service
public class EjecucionesLectHandlerService {

    public void EnviarAEjecucionesLectHandler (EjecucionesLecturas ejecucionLecturas) {
        
        System.out.println("");
        System.out.println("-------------------------------------------------");
        System.out.println("ID de Ejecución de Lectura: " + ejecucionLecturas.getNidEjecucionLectura());
        System.out.println("Intento de Lectura Número: " + ejecucionLecturas.getNintentoLecturaNumero());
        System.out.println("Anterior intento de Lectura Número: " + ejecucionLecturas.getNidAnteriorIntentoEjecucionLectura());
        System.out.println("Inicio de Ejecución: " + ejecucionLecturas.getDinicioEjecucionLectura());
        // System.out.println("Fin de Ejecución: " + ejecucionLecturas.getDfinEjecucionLectura());


        // Manejo del caso de Ejecución de Lectura Detect
        if (ejecucionLecturas.getEjecucionLecturaDetect() != null) {
            System.out.println("Ejecución de Lectura Detect:");
            // System.out.println("  ID Detección: " + ejecucionLecturas.getEjecucionLecturaDetect().getNidEjecucionLecturaDetect());
            System.out.println("  Descripción Detección: " + ejecucionLecturas.getEjecucionLecturaDetect().getVcdescripcionDetect());
            System.out.println("  noSerie: " + ejecucionLecturas.getEjecucionLecturaDetect().getVcnoserie());
            // System.out.println("  Detección OK: " + ejecucionLecturas.getEjecucionLecturaDetect().isLdeteccionOK());
            // System.out.println("  Tabla Medidores Detect: " + ejecucionLecturas.getEjecucionLecturaDetect().getJsTablaMedidoresDetec());
        }

        // Manejo del caso de Ejecución de Lectura AutoConf
        if (ejecucionLecturas.getEjecucionLecturaAutoConf() != null) {
            System.out.println("Ejecución de Lectura AutoConf:");
            // System.out.println("  ID AutoConf: " + ejecucionLecturas.getEjecucionLecturaAutoConf().getNidEjecucionLecturaAutoConf());
            System.out.println("  Descripción AutoConf: " + ejecucionLecturas.getEjecucionLecturaAutoConf().getVcdescripcionAutoconf());
            System.out.println("  Serie: " + ejecucionLecturas.getEjecucionLecturaAutoConf().getVcserie());
            System.out.println("  noSerie: " + ejecucionLecturas.getEjecucionLecturaAutoConf().getVcnoserie());
            // System.out.println("  AutoConf OK: " + ejecucionLecturas.getEjecucionLecturaAutoConf().isLobtencionAutoConfOK());
            System.out.println("  Equipos AutoConfigurar: " + ejecucionLecturas.getEjecucionLecturaAutoConf().getJsequiposAutoconfigurar());
        }

        // Manejo del caso de Ejecución de Lectura Prog
        if (ejecucionLecturas.getEjecucionLecturaProg() != null) {
            System.out.println("Ejecución de Lectura Prog:");
            // System.out.println("  ID Programación: " + ejecucionLecturas.getEjecucionLecturaProg().getNidEjecucionesLecturaProg());
            System.out.println("  Descripción Prog: " + ejecucionLecturas.getEjecucionLecturaProg().getVcdescripcionProg());
            System.out.println("  Serie: " + ejecucionLecturas.getEjecucionLecturaProg().getVcserie());
            System.out.println("  noSerie: " + ejecucionLecturas.getEjecucionLecturaProg().getVcnoserie());
            // System.out.println("  Lectura OK: " + ejecucionLecturas.getEjecucionLecturaProg().isLlecturaOK());
            System.out.println("  Series Medidores: " + ejecucionLecturas.getEjecucionLecturaProg().getJsseriesMed());
            // System.out.println("  Medidores Faltantes: " + ejecucionLecturas.getEjecucionLecturaProg().getJsmedidoresFaltantesPorLeer());

            // Imprimir detalles de la programación AMI si existe
            if (ejecucionLecturas.getEjecucionLecturaProg().getProgramacionAMI() != null) {
                System.out.println("  Programación AMI:");
                System.out.println("    Código: " + ejecucionLecturas.getEjecucionLecturaProg().getProgramacionAMI().getNcodigo());
                System.out.println("    Estado: " + ejecucionLecturas.getEjecucionLecturaProg().getProgramacionAMI().getVcestado());
                System.out.println("    Filtro: " + ejecucionLecturas.getEjecucionLecturaProg().getProgramacionAMI().getGrupoMedidores().getVcfiltro());
                System.out.println("    Identificador: " + ejecucionLecturas.getEjecucionLecturaProg().getProgramacionAMI().getGrupoMedidores().getVcidentificador());
            }
        }

        // Caso en que no hay ningún tipo de Ejecución asociado
        if (ejecucionLecturas.getEjecucionLecturaDetect() == null && ejecucionLecturas.getEjecucionLecturaAutoConf() == null && ejecucionLecturas.getEjecucionLecturaProg() == null) {
            System.out.println("No hay ningún tipo de ejecución asociado a esta lectura.");
        }

        System.out.println("-------------------------------------------------");
        System.out.println("");
    }
}

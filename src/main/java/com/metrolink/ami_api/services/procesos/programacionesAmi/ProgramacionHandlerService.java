package com.metrolink.ami_api.services.procesos.programacionesAmi;

import org.springframework.beans.factory.annotation.Autowired;

import com.metrolink.ami_api.models.procesos.programacionesAmi.ProgramacionesAMI;
import com.metrolink.ami_api.services.procesos.conectorGeneral.ConectorGeneralService;
import com.metrolink.ami_api.services.procesos.generadorDeColas.GeneradorDeColas;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class ProgramacionHandlerService {

    @Autowired
    private ConectorGeneralService conectorGeneralService;

    @Autowired
    private GeneradorDeColas generadorDeColas;

    public String manejarProgramacion(ProgramacionesAMI programacionAMI) {

        String tipoLectura = programacionAMI.getParametrizacionProg().getVctipoDeLectura();
        String filtro = programacionAMI.getGrupoMedidores().getVcfiltro();

        // Determinar el tipo de lectura y aplicar la lógica correspondiente
        if ("única".equalsIgnoreCase(tipoLectura)) {
            System.out.println("LECTURA ÚNICA");
            return manejarFiltro_unica(filtro, programacionAMI);
        } else if ("frecuente no recurrente".equalsIgnoreCase(tipoLectura)) {
            System.out.println("LECTURA FRECUENTE NO RECURRENTE");
            return manejarFiltro_frecuenteNoR(filtro, programacionAMI);
        } else if ("recurrente".equalsIgnoreCase(tipoLectura)) {
            System.out.println("LECTURA RECURRENTE");
            return manejarFiltro_recurrente(filtro, programacionAMI);
        } else {
            System.out.println("CASO NO DEFINIDO");
        }

        return "CASO NO DEFINIDO";
    }

    // Método para manejar la lógica de filtrado
    private String manejarFiltro_unica(String filtro, ProgramacionesAMI programacionAMI) {

        String resultado = String.format("Puesta en marcha de Programacion: %d De periodicidad única con Filtro %s",
                programacionAMI.getNcodigo(),
                filtro);

        switch (filtro.toLowerCase()) {
            case "concentrador":
                System.out.println("FILTRO POR CONCENTRADOR");

                Timestamp tiempoInicio = programacionAMI.getParametrizacionProg().getDfechaHoraInicio();
                System.out.println("Tiempo de inicio programado: " + tiempoInicio);

                Instant ahora = Instant.now();
                long delay = Duration.between(ahora, tiempoInicio.toInstant()).toMillis();

                if (delay > 0) {
                    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                    programarTarea(scheduler, programacionAMI, delay);
                } else {
                    System.out.println("La fecha y hora ya han pasado, no se puede programar la tarea.");
                }

                break;
            case "concentradorymedidores":
                System.out.println("FILTRO POR CONCENTRADOR Y MEDIDORES");
                break;
            case "medidores":
                System.out.println("FILTRO POR MEDIDORES");
                break;
            case "frontera sic":
                System.out.println("FILTRO FRONTERA SIC");
                break;
            default:
                System.out.println("FILTRO NO DEFINIDO");
                break;
        }

        return resultado;
    }

    // Método para manejar la lógica de filtrado
    private String manejarFiltro_frecuenteNoR(String filtro, ProgramacionesAMI programacionAMI) {

        String resultado = String.format("Puesta en marcha de Programacion: %d De periodicidad única con Filtro %s",
                programacionAMI.getNcodigo(),
                filtro); // estoo es un error

        switch (filtro.toLowerCase()) {
            case "concentrador":
                System.out.println("FILTRO POR CONCENTRADOR");

                break;
            case "concentradorymedidores":
                System.out.println("FILTRO POR CONCENTRADOR Y MEDIDORES");
                break;
            case "medidores":
                System.out.println("FILTRO POR MEDIDORES");
                break;
            case "frontera sic":
                System.out.println("FILTRO FRONTERA SIC");
                break;
            default:
                System.out.println("FILTRO NO DEFINIDO");
                break;
        }

        return resultado;
    }

    // Método para manejar la lógica de filtrado
    private String manejarFiltro_recurrente(String filtro, ProgramacionesAMI programacionAMI) {

        String resultado = String.format("Puesta en marcha de Programacion: %d De periodicidad única con Filtro %s",
                programacionAMI.getNcodigo(),
                filtro); // estoo es un error

        switch (filtro.toLowerCase()) {
            case "concentrador":
                System.out.println("FILTRO POR CONCENTRADOR");

                break;
            case "concentradorymedidores":
                System.out.println("FILTRO POR CONCENTRADOR Y MEDIDORES");
                break;
            case "medidores":
                System.out.println("FILTRO POR MEDIDORES");
                break;
            case "frontera sic":
                System.out.println("FILTRO FRONTERA SIC");
                break;
            default:
                System.out.println("FILTRO NO DEFINIDO");
                break;
        }

        return resultado;
    }

    // Método para programar y reintentar la tarea
    private void programarTarea(ScheduledExecutorService scheduler, ProgramacionesAMI programacionAMI, long delay) {
        scheduler.schedule(() -> {
            try {
                // Crear la tarea que se encolará
                Callable<String> PedirPeticionesPorConcentrador = () -> conectorGeneralService
                        .usarConectorProgramacion("provvicional Hola Mundo", programacionAMI);

                // Usar GeneradorDeColas para encolar la tarea
                CompletableFuture<String> future = generadorDeColas.encolarSolicitud("C_" +
                        programacionAMI.getGrupoMedidores().getVcidentificador(),
                        PedirPeticionesPorConcentrador);

                // Esperar a que se complete la tarea
                String resultadoTarea = future.get(); // Este método bloquea hasta que la tarea esté completa

                String medidoresFaltantesPorLeer = resultadoTarea;

                int reintentosRestantes = programacionAMI.getParametrizacionProg().getNreintentos();
                // Reintentar la tarea si hay medidores no leídos
                if (!"[]".equalsIgnoreCase(medidoresFaltantesPorLeer)) {
                    System.out.println("Reintentando la tarea en 1 minuto debido a medidores no leídos.");
                   

                    programarTareaRestantes(scheduler, programacionAMI, 60000, medidoresFaltantesPorLeer,
                            reintentosRestantes); // Reprograma
                    // +1

                } else {
                    System.out.println(
                            "Se leyeron todos los medidores de " + programacionAMI.getGrupoMedidores().getVcfiltro()
                                    + " " + programacionAMI.getGrupoMedidores().getVcidentificador());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    private void programarTareaRestantes(ScheduledExecutorService scheduler, ProgramacionesAMI programacionAMI,
            long delay, String medidoresFaltantesPorLeer_, int reintentosRestantes) {


                System.out.println("Quedan " + reintentosRestantes + " intentos");
        scheduler.schedule(() -> {
            try {
                // Crear la tarea que se encolará
                Callable<String> PedirPeticionesPorConcentrador = () -> conectorGeneralService
                        .usarConectorProgramacionFaltantes("Hola Mundo", programacionAMI, medidoresFaltantesPorLeer_);

                // Usar GeneradorDeColas para encolar la tarea
                CompletableFuture<String> future = generadorDeColas.encolarSolicitud("C_" +
                        programacionAMI.getGrupoMedidores().getVcidentificador(),
                        PedirPeticionesPorConcentrador);

                // Esperar a que se complete la tarea
                String resultadoTarea = future.get(); // Este método bloquea hasta que la tarea esté completa

                String medidoresFaltantesPorLeer = resultadoTarea;

                // Reintentar la tarea si hay medidores no leídos y aún hay reintentos
                // disponibles
                if (!"[]".equalsIgnoreCase(medidoresFaltantesPorLeer) && reintentosRestantes > 0) {
                    System.out.println("Reintentando la tarea en 1 minuto debido a medidores no leídos.");
                    programarTareaRestantes(scheduler, programacionAMI, 60000, medidoresFaltantesPorLeer,
                            reintentosRestantes - 1);
                } else if (reintentosRestantes == 0) {
                    System.out.println("Se alcanzó el número máximo de reintentos.");
                } else {
                    System.out.println(
                            "Se leyeron todos los medidores de " + programacionAMI.getGrupoMedidores().getVcfiltro()
                                    + " " + programacionAMI.getGrupoMedidores().getVcidentificador());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, delay, TimeUnit.MILLISECONDS);
    }
}

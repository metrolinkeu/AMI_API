package com.metrolink.ami_api.services.procesos.programacionesAmi;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.metrolink.ami_api.models.procesos.programacionesAmi.ProgramacionesAMI;
import com.metrolink.ami_api.services.procesos.conectorGeneral.ConectorGeneralService;
import com.metrolink.ami_api.services.procesos.generadorDeColas.GeneradorDeColas;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

        Timestamp tiempoInicio = null;
        Instant ahora = Instant.now();
        long delay = 0;

        String vcSeriesAReintentarFiltrado_ = "";
        int reintentosRestantes = 0;

        switch (filtro.toLowerCase()) {
            case "concentrador":
                System.out.println("FILTRO POR CONCENTRADOR");

                tiempoInicio = programacionAMI.getParametrizacionProg().getDfechaHoraInicio();
                System.out.println("Tiempo de inicio programado: " + tiempoInicio);

                ahora = Instant.now();
                delay = Duration.between(ahora, tiempoInicio.toInstant()).toMillis();

                if (delay > 0) {
                    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                    programarTareaCaso1(scheduler, programacionAMI, delay);
                } else {
                    System.out.println("La fecha y hora ya han pasado, no se puede programar la tarea.");
                }

                break;
            case "concentradorymedidores":
                System.out.println("FILTRO POR CONCENTRADOR Y MEDIDORES");

                tiempoInicio = programacionAMI.getParametrizacionProg().getDfechaHoraInicio();
                System.out.println("Tiempo de inicio programado: " + tiempoInicio);

                ahora = Instant.now();
                delay = Duration.between(ahora, tiempoInicio.toInstant()).toMillis();

                vcSeriesAReintentarFiltrado_ = "EstadoInicio";
                reintentosRestantes = programacionAMI.getParametrizacionProg().getNreintentos();

                if (delay > 0) {
                    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                    programarTareaCaso2(scheduler, programacionAMI, delay, vcSeriesAReintentarFiltrado_,
                            reintentosRestantes);
                } else {
                    System.out.println("La fecha y hora ya han pasado, no se puede programar la tarea.");
                }

                break;
            case "medidores":
                System.out.println("FILTRO POR MEDIDORES");

                tiempoInicio = programacionAMI.getParametrizacionProg().getDfechaHoraInicio();
                System.out.println("Tiempo de inicio programado: " + tiempoInicio);

                ahora = Instant.now();
                delay = Duration.between(ahora, tiempoInicio.toInstant()).toMillis();

                vcSeriesAReintentarFiltrado_ = "EstadoInicio";
                reintentosRestantes = programacionAMI.getParametrizacionProg().getNreintentos();

                if (delay > 0) {
                    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                    programarTareaCaso3(scheduler, programacionAMI, delay, vcSeriesAReintentarFiltrado_,
                            reintentosRestantes);
                } else {
                    System.out.println("La fecha y hora ya han pasado, no se puede programar la tarea.");
                }

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
    private void programarTareaCaso1(ScheduledExecutorService scheduler, ProgramacionesAMI programacionAMI,
            long delay) {
        scheduler.schedule(() -> {
            try {
                // Crear la tarea que se encolará
                Callable<String> PedirPeticionesPorConcentrador = () -> conectorGeneralService
                        .usarConectorProgramacionCaso1("Lectura caso 1", programacionAMI);

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

                    programarTareaRestantesCaso1(scheduler, programacionAMI, 60000, medidoresFaltantesPorLeer,
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

    private void programarTareaRestantesCaso1(ScheduledExecutorService scheduler, ProgramacionesAMI programacionAMI,
            long delay, String medidoresFaltantesPorLeer_, int reintentosRestantes) {

        System.out.println("Quedan " + reintentosRestantes + " intentos");
        scheduler.schedule(() -> {
            try {
                // Crear la tarea que se encolará
                Callable<String> PedirPeticionesPorConcentrador = () -> conectorGeneralService
                        .usarConectorProgramacionFaltantesCaso1("Lectura caso 1", programacionAMI,
                                medidoresFaltantesPorLeer_);

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
                    programarTareaRestantesCaso1(scheduler, programacionAMI, 60000, medidoresFaltantesPorLeer,
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

    // Método para programar y reintentar la tarea
    private void programarTareaCaso2(ScheduledExecutorService scheduler, ProgramacionesAMI programacionAMI,
            long delay, String vcSeriesAReintentarFiltrado_, int reintentosRestantes) {
        scheduler.schedule(() -> {
            try {

                // Crear la tarea que se encolará
                Callable<String> PedirPeticionesPorConcentrador = () -> conectorGeneralService
                        .usarConectorProgramacionCaso2("Lectura caso 2", programacionAMI, vcSeriesAReintentarFiltrado_);

                // Usar GeneradorDeColas para encolar la tarea
                CompletableFuture<String> future = generadorDeColas.encolarSolicitud("C_" +
                        programacionAMI.getGrupoMedidores().getVcidentificador(),
                        PedirPeticionesPorConcentrador);

                // Esperar a que se complete la tarea
                String resultadoTarea = future.get(); // Este método bloquea hasta que la tarea esté completa

                String vcSeriesAReintentarFiltrado = resultadoTarea;

                // Reintentar la tarea si hay medidores no leídos
                if (!"[]".equalsIgnoreCase(vcSeriesAReintentarFiltrado) && reintentosRestantes > 0) {
                    System.out.println("Reintentando la tarea en 1 minuto debido a medidores no leídos.");

                    programarTareaCaso2(scheduler, programacionAMI, 60000, vcSeriesAReintentarFiltrado,
                            reintentosRestantes - 1); // Reprograma
                    // +1
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

    // Método para programar y reintentar la tarea
    private void programarTareaCaso3(ScheduledExecutorService scheduler, ProgramacionesAMI programacionAMI,
            long delay, String vcSeriesAReintentarFiltrado_, int reintentosRestantes) {
        scheduler.schedule(() -> {
            try {

                String jsseriesMed = "";
                List<String> vcSeriesAReintentar = new ArrayList<>();
                // Crear una lista para almacenar los CompletableFutures
                List<CompletableFuture<String>> futuresList = new ArrayList<>();

                if (!"EstadoInicio".equalsIgnoreCase(vcSeriesAReintentarFiltrado_)) {
                    jsseriesMed = vcSeriesAReintentarFiltrado_;
                } else {
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
                            String vcserie = jsseriesMedList.get(i);
                            // Crar la tarea que se encolará
                            Callable<String> PedirPeticionesPorConcentrador = () -> conectorGeneralService
                                    .usarConectorProgramacionCaso3("Lectura caso 3", programacionAMI, vcserie);

                            // Usar GeneradorDeColas para encolar la tarea
                            CompletableFuture<String> future = generadorDeColas.encolarSolicitud("M_" + vcserie,
                                    PedirPeticionesPorConcentrador);

                            // Agregar el CompletableFuture a la lista de futuros
                            futuresList.add(future);
                        }

                        // Esperar a que todos los futuros se completen y recoger los resultados
                        for (CompletableFuture<String> future : futuresList) {
                            try {
                                // Obtener el resultado de cada future y agregarlo a la lista
                                // autoConfiguraciones
                                String LeidoNoLeido = future.get(); // Este método bloquea hasta que el
                                                                    // resultado
                                                                    // esté disponible
                                vcSeriesAReintentar.add(LeidoNoLeido);
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                                // Manejar excepciones según sea necesario
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Filtrar los valores vacíos y construir el string con el formato requerido
                String vcSeriesAReintentarFiltrado = vcSeriesAReintentar.stream()
                        .filter(serie -> !serie.isEmpty()) // Filtra las cadenas vacías
                        .map(serie -> "\"" + serie + "\"") // Añade las comillas a cada serie
                        .collect(Collectors.joining(", ", "[", "]")); // Une con comas y encierra en corchetes

                // Imprimir el resultado
                System.out.println(vcSeriesAReintentarFiltrado);
                // // Reintentar la tarea si hay medidores no leídos
                if (!"[]".equalsIgnoreCase(vcSeriesAReintentarFiltrado) && reintentosRestantes > 0) {
                    System.out.println("Reintentando la tarea en 1 minuto debido a medidores no leídos.");

                    programarTareaCaso3(scheduler, programacionAMI, 60000,
                            vcSeriesAReintentarFiltrado,
                            reintentosRestantes - 1); // Reprograma
                    // +1
                } else if (reintentosRestantes == 0) {
                    System.out.println("Se alcanzó el número máximo de reintentos.");
                } else {
                    System.out.println(
                            "Se leyeron todos los medidores de " +
                                    programacionAMI.getGrupoMedidores().getVcfiltro());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, delay, TimeUnit.MILLISECONDS);
    }
}

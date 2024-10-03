package com.metrolink.ami_api.services.procesos.programacionesAmi;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metrolink.ami_api.models.medidor.Medidores;
import com.metrolink.ami_api.models.procesos.programacionesAmi.ProgramacionesAMI;
import com.metrolink.ami_api.services.medidor.MedidoresService;
import com.metrolink.ami_api.services.procesos.conectorGeneral.ConectorGeneralService;
import com.metrolink.ami_api.services.procesos.generadorDeColas.GeneradorDeColas;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import java.util.Map;

@Component
public class ProgramacionHandlerService {

    @Autowired
    private ConectorGeneralService conectorGeneralService;

    @Autowired
    private GeneradorDeColas generadorDeColas;

    @Autowired
    private MedidoresService medidoresService;

    public String manejarProgramacion(ProgramacionesAMI programacionAMI) {

        String tipoLectura = programacionAMI.getParametrizacionProg().getVctipoDeLectura();
        String filtro = programacionAMI.getGrupoMedidores().getVcfiltro();

        System.out.println("Estos son los minutos de delay que programare para cada reintento "
                + programacionAMI.getParametrizacionProg().getNdelayMin());

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

    private String manejarFiltro_unica(String filtro, ProgramacionesAMI programacionAMI) {

        String resultado = String.format("Puesta en marcha de Programacion: %d De periodicidad única con Filtro %s",
                programacionAMI.getNcodigo(),
                filtro);

        Timestamp tiempoInicio = programacionAMI.getParametrizacionProg().getDfechaHoraInicio();
        System.out.println("Tiempo de inicio programado: " + tiempoInicio);
        Instant ahora = Instant.now();
        long delay = Duration.between(ahora, tiempoInicio.toInstant()).toMillis();

        String vcSeriesAReintentarFiltrado_ = "EstadoInicio";
        int reintentosRestantes = programacionAMI.getParametrizacionProg().getNreintentos();

        switch (filtro.toLowerCase()) {
            case "concentrador":
                System.out.println("FILTRO POR CONCENTRADOR");
                if (delay > 0) {
                    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                    programarTareaCaso1(scheduler, programacionAMI, delay, vcSeriesAReintentarFiltrado_,
                            reintentosRestantes - 1);
                } else {
                    System.out.println("La fecha y hora ya han pasado, no se puede programar la tarea.");
                }

                break;
            case "concentradorymedidores":
                System.out.println("FILTRO POR CONCENTRADOR Y MEDIDORES");
                if (delay > 0) {
                    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                    programarTareaCaso2(scheduler, programacionAMI, delay, vcSeriesAReintentarFiltrado_,
                            reintentosRestantes - 1);
                } else {
                    System.out.println("La fecha y hora ya han pasado, no se puede programar la tarea.");
                }

                break;
            case "medidores":
                System.out.println("FILTRO POR MEDIDORES");
                if (delay > 0) {
                    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                    programarTareaCaso3(scheduler, programacionAMI, delay, vcSeriesAReintentarFiltrado_,
                            reintentosRestantes - 1);
                } else {
                    System.out.println("La fecha y hora ya han pasado, no se puede programar la tarea.");
                }

                break;
            case "frontera sic":
                System.out.println("FILTRO FRONTERA SIC");
                if (delay > 0) {
                    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                    programarTareaCaso4(scheduler, programacionAMI, delay, vcSeriesAReintentarFiltrado_,
                            reintentosRestantes - 1);
                } else {
                    System.out.println("La fecha y hora ya han pasado, no se puede programar la tarea.");
                }

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

        // Obtener los días de la semana desde jsdiasSemana
        String jsdiasSemana = programacionAMI.getParametrizacionProg().getJsdiasSemana();
        System.out.println(jsdiasSemana);

        String jsfrecuenciaLecturaLote = programacionAMI.getParametrizacionProg().getJsfrecuenciaLecturaLote();
        System.out.println(jsfrecuenciaLecturaLote);

        List<DayOfWeek> diasSemana = convertirDiasSemana(jsdiasSemana);
        System.out.println(diasSemana);

        // Obtenemos el tiempo de inicio base
        LocalDateTime tiempoInicio = programacionAMI.getParametrizacionProg().getDfechaHoraInicio().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDateTime();

        System.out.println(tiempoInicio);

        String vcSeriesAReintentarFiltrado_ = "EstadoInicio";
        int reintentosRestantes = programacionAMI.getParametrizacionProg().getNreintentos();

        switch (filtro.toLowerCase()) {
            case "concentrador":
                System.out.println("FILTRO POR CONCENTRADOR");
                programarTareasFrecuentesCaso5(diasSemana, tiempoInicio, programacionAMI, filtro,
                        vcSeriesAReintentarFiltrado_,
                        reintentosRestantes - 1);

                break;
            case "concentradorymedidores":
                System.out.println("FILTRO POR CONCENTRADOR Y MEDIDORES");

                programarTareasFrecuentesCaso6(diasSemana, tiempoInicio, programacionAMI, filtro,
                        vcSeriesAReintentarFiltrado_,
                        reintentosRestantes - 1);

                break;
            case "medidores":
                System.out.println("FILTRO POR MEDIDORES");
                programarTareasFrecuentesCaso7(diasSemana, tiempoInicio, programacionAMI, filtro,
                        vcSeriesAReintentarFiltrado_,
                        reintentosRestantes - 1);
                break;
            case "frontera sic":
                System.out.println("FILTRO FRONTERA SIC");
                programarTareasFrecuentesCaso8(diasSemana, tiempoInicio, programacionAMI, filtro,
                        vcSeriesAReintentarFiltrado_,
                        reintentosRestantes - 1);
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

        // Obtener los días de la semana desde jsdiasSemana
        String jsdiasSemana = programacionAMI.getParametrizacionProg().getJsdiasSemana();
        System.out.println(jsdiasSemana);

        String jsfrecuenciaLecturaLote = programacionAMI.getParametrizacionProg().getJsfrecuenciaLecturaLote();
        System.out.println(jsfrecuenciaLecturaLote);

        List<DayOfWeek> diasSemana = convertirDiasSemana(jsdiasSemana);
        System.out.println(diasSemana);

        // Obtenemos el tiempo de inicio base
        LocalDateTime tiempoInicio = programacionAMI.getParametrizacionProg().getDfechaHoraInicio().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDateTime();

        System.out.println(tiempoInicio);

        String vcSeriesAReintentarFiltrado_ = "EstadoInicio";
        int reintentosRestantes = programacionAMI.getParametrizacionProg().getNreintentos();

        switch (filtro.toLowerCase()) {
            case "concentrador":
                System.out.println("FILTRO POR CONCENTRADOR");
                programarTareasRecurrenteCaso9(diasSemana, tiempoInicio, programacionAMI, filtro,
                        vcSeriesAReintentarFiltrado_,
                        reintentosRestantes - 1);
                break;
            case "concentradorymedidores":
                System.out.println("FILTRO POR CONCENTRADOR Y MEDIDORES");
                programarTareasRecurrenteCaso10(diasSemana, tiempoInicio, programacionAMI, filtro,
                        vcSeriesAReintentarFiltrado_,
                        reintentosRestantes - 1);
                break;
            case "medidores":
                System.out.println("FILTRO POR MEDIDORES");
                programarTareasRecurrenteCaso11(diasSemana, tiempoInicio, programacionAMI, filtro,
                        vcSeriesAReintentarFiltrado_,
                        reintentosRestantes - 1);
                break;
            case "frontera sic":
                System.out.println("FILTRO FRONTERA SIC");
                programarTareasRecurrenteCaso12(diasSemana, tiempoInicio, programacionAMI, filtro,
                        vcSeriesAReintentarFiltrado_,
                        reintentosRestantes - 1);
                break;
            default:
                System.out.println("FILTRO NO DEFINIDO");
                break;
        }

        return resultado;
    }

    // Método para programar y reintentar la tarea
    private void programarTareaCaso1(ScheduledExecutorService scheduler, ProgramacionesAMI programacionAMI,
            long delay, String vcSeriesAReintentarFiltrado_, int reintentosRestantes) {
                
        scheduler.schedule(() -> {
            try {
                
                // Crear la tarea que se encolará
                Callable<String> tareaParaProgramar = () -> conectorGeneralService
                        .usarConectorProgramacionFiltroConcentrador("Lectura caso 1", programacionAMI,
                                vcSeriesAReintentarFiltrado_, reintentosRestantes);

                // Usar GeneradorDeColas para encolar la tarea
                CompletableFuture<String> future = generadorDeColas.encolarSolicitud("C_" +
                        programacionAMI.getGrupoMedidores().getVcidentificador(),
                        tareaParaProgramar);

                // Esperar a que se complete la tarea
                String resultadoTarea = future.get(); // Este método bloquea hasta que la tarea esté completa

                String vcSeriesAReintentarFiltrado = resultadoTarea;

                // Reintentar la tarea si hay medidores no leídos
                if (!"[]".equalsIgnoreCase(vcSeriesAReintentarFiltrado) && reintentosRestantes > 0) {
                    System.out.println("Reintentando la tarea en 1 minuto debido a medidores no leídos.");
                    int LapsoMinReintentos = programacionAMI.getParametrizacionProg().getNdelayMin();
                    Long delayReintento = LapsoMinReintentos * 60 * 1000L;

                    programarTareaCaso1(scheduler, programacionAMI, delayReintento, vcSeriesAReintentarFiltrado,
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
    private void programarTareaCaso2(ScheduledExecutorService scheduler, ProgramacionesAMI programacionAMI,
            long delay, String vcSeriesAReintentarFiltrado_, int reintentosRestantes) {
        scheduler.schedule(() -> {
            try {

                // Crear la tarea que se encolará
                Callable<String> tareaParaProgramar = () -> conectorGeneralService
                        .usarConectorProgramacionFiltroConyMed("Lectura caso 2", programacionAMI,
                                vcSeriesAReintentarFiltrado_, reintentosRestantes);

                // Usar GeneradorDeColas para encolar la tarea
                CompletableFuture<String> future = generadorDeColas.encolarSolicitud("C_" +
                        programacionAMI.getGrupoMedidores().getVcidentificador(),
                        tareaParaProgramar);

                // Esperar a que se complete la tarea
                String resultadoTarea = future.get(); // Este método bloquea hasta que la tarea esté completa

                String vcSeriesAReintentarFiltrado = resultadoTarea;

                // Reintentar la tarea si hay medidores no leídos
                if (!"[]".equalsIgnoreCase(vcSeriesAReintentarFiltrado) && reintentosRestantes > 0) {
                    System.out.println("Reintentando la tarea en 1 minuto debido a medidores no leídos.");
                    int LapsoMinReintentos = programacionAMI.getParametrizacionProg().getNdelayMin();
                    Long delayReintento = LapsoMinReintentos * 60 * 1000L;

                    programarTareaCaso2(scheduler, programacionAMI, delayReintento, vcSeriesAReintentarFiltrado,
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
                            Callable<String> tareaParaProgramar = () -> conectorGeneralService
                                    .usarConectorProgramacionFiltroMedidores("Lectura caso 3", programacionAMI,
                                            vcserie, reintentosRestantes);

                            // Usar GeneradorDeColas para encolar la tarea
                            CompletableFuture<String> future = generadorDeColas.encolarSolicitud("M_" + vcserie,
                                    tareaParaProgramar);

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
                    int LapsoMinReintentos = programacionAMI.getParametrizacionProg().getNdelayMin();
                    Long delayReintento = LapsoMinReintentos * 60 * 1000L;

                    programarTareaCaso3(scheduler, programacionAMI, delayReintento,
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

    // Método para programar y reintentar la tarea
    private void programarTareaCaso4(ScheduledExecutorService scheduler, ProgramacionesAMI programacionAMI,
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
                    String identidicadorSic = programacionAMI.getGrupoMedidores().getVcidentificador();
                    List<Medidores> medidores = medidoresService.findByVcsic(identidicadorSic);
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
                            Callable<String> tareaParaProgramar = () -> conectorGeneralService
                                    .usarConectorProgramacionFiltroSIC("Lectura caso 4", programacionAMI, vcserie, reintentosRestantes);

                            // Usar GeneradorDeColas para encolar la tarea
                            CompletableFuture<String> future = generadorDeColas.encolarSolicitud("M_" + vcserie,
                                    tareaParaProgramar);

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
                    int LapsoMinReintentos = programacionAMI.getParametrizacionProg().getNdelayMin();
                    Long delayReintento = LapsoMinReintentos * 60 * 1000L;

                    programarTareaCaso4(scheduler, programacionAMI, delayReintento,
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

    private void programarTareasFrecuentesCaso5(List<DayOfWeek> diasSemana, LocalDateTime tiempoInicio,
            ProgramacionesAMI programacionAMI, String filtro, String vcSeriesAReintentarFiltrado_,
            int reintentosRestantes) {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        LocalDateTime ahora = LocalDateTime.now();

        // Iterar sobre cada día de la semana y programar la tarea
        for (DayOfWeek diaSemana : diasSemana) {
            LocalDateTime proximoDia = ahora.with(TemporalAdjusters.nextOrSame(diaSemana))
                    .withHour(tiempoInicio.getHour())
                    .withMinute(tiempoInicio.getMinute()).withSecond(0);

            System.out.println("Se programa para el siguiente dia: " + diaSemana);

            // Calcular el delay en milisegundos hasta el siguiente día de la semana
            long delay = Duration.between(ahora, proximoDia).toMillis();

            // Programar la tarea recurrente indefinidamente para ese día de la semana
            scheduler.scheduleAtFixedRate(() -> {
                System.out.println("Ejecutando tarea para " + diaSemana + " con filtro: " + filtro);
                // Aquí iría la lógica de ejecución de la tarea para este día

                // Crear la tarea que se encolará
                Callable<String> tareaParaProgramar = () -> conectorGeneralService
                        .usarConectorProgramacionFiltroConcentrador("Lectura caso 5", programacionAMI,
                                vcSeriesAReintentarFiltrado_, reintentosRestantes);

                // Usar GeneradorDeColas para encolar la tarea
                CompletableFuture<String> future = generadorDeColas.encolarSolicitud("C_" +
                        programacionAMI.getGrupoMedidores().getVcidentificador(),
                        tareaParaProgramar);

                try {
                    // Esperar a que se complete la tarea
                    String resultadoTarea = future.get(); // Este método bloquea hasta que la tarea esté completa
                    String vcSeriesAReintentarFiltrado = resultadoTarea;

                    // Reintentar la tarea si hay medidores no leídos
                    if (!"[]".equalsIgnoreCase(vcSeriesAReintentarFiltrado) && reintentosRestantes > 0) {
                        System.out.println("Reintentando la tarea en 1 minuto debido a medidores no leídos.");
                        int LapsoMinReintentos = programacionAMI.getParametrizacionProg().getNdelayMin();
                        Long delayReintento = LapsoMinReintentos * 60 * 1000L;

                        // Programar la tarea para 1 minuto después solo con los medidores no leídos
                        programarReintentoTareaCaso5(scheduler, programacionAMI, delayReintento,
                                vcSeriesAReintentarFiltrado,
                                reintentosRestantes - 1);

                    } else if (reintentosRestantes == 0) {
                        System.out.println("Se alcanzó el número máximo de reintentos.");
                    } else {
                        System.out.println(
                                "Se leyeron todos los medidores de " + programacionAMI.getGrupoMedidores().getVcfiltro()
                                        + " " + programacionAMI.getGrupoMedidores().getVcidentificador());
                    }

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

            }, delay, TimeUnit.DAYS.toMillis(7), TimeUnit.MILLISECONDS); // Repetir cada 7 días
        }
    }

    private void programarReintentoTareaCaso5(ScheduledExecutorService scheduler, ProgramacionesAMI programacionAMI,
            long delay, String vcSeriesAReintentarFiltrado, int reintentosRestantes) {

        scheduler.schedule(() -> {
            System.out.println("Reintentando tarea para medidores no leídos...");

            // Crear la tarea para reintentar
            Callable<String> tareaParaReintentar = () -> conectorGeneralService
                    .usarConectorProgramacionFiltroConcentrador("Lectura reintento caso 5", programacionAMI,
                            vcSeriesAReintentarFiltrado, reintentosRestantes);

            // Usar GeneradorDeColas para encolar la tarea
            CompletableFuture<String> future = generadorDeColas.encolarSolicitud("C_" +
                    programacionAMI.getGrupoMedidores().getVcidentificador(),
                    tareaParaReintentar);

            try {
                // Esperar a que se complete la tarea
                String resultadoReintento = future.get(); // Bloquea hasta que la tarea esté completa
                String vcSeriesAReintentarFiltradoNuevo = resultadoReintento;

                // Si quedan medidores por leer y hay reintentos disponibles, reprograma
                // nuevamente
                if (!"[]".equalsIgnoreCase(vcSeriesAReintentarFiltradoNuevo) && reintentosRestantes > 0) {
                    System.out.println("Reintentando la tarea nuevamente en 1 minuto...");
                    int LapsoMinReintentos = programacionAMI.getParametrizacionProg().getNdelayMin();
                    Long delayReintento = LapsoMinReintentos * 60 * 1000L;

                    programarReintentoTareaCaso5(scheduler, programacionAMI, delayReintento,
                            vcSeriesAReintentarFiltradoNuevo,
                            reintentosRestantes - 1);
                } else if (reintentosRestantes == 0) {
                    System.out.println("Se alcanzó el número máximo de reintentos.");
                } else {
                    System.out.println("Se completó la lectura de todos los medidores.");
                }

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        }, delay, TimeUnit.MILLISECONDS); // Programar para ejecutarse después de 1 minuto
    }

    private void programarTareasFrecuentesCaso6(List<DayOfWeek> diasSemana, LocalDateTime tiempoInicio,
            ProgramacionesAMI programacionAMI, String filtro, String vcSeriesAReintentarFiltrado_,
            int reintentosRestantes) {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        LocalDateTime ahora = LocalDateTime.now();

        // Iterar sobre cada día de la semana y programar la tarea
        for (DayOfWeek diaSemana : diasSemana) {
            LocalDateTime proximoDia = ahora.with(TemporalAdjusters.nextOrSame(diaSemana))
                    .withHour(tiempoInicio.getHour())
                    .withMinute(tiempoInicio.getMinute()).withSecond(0);

            System.out.println("Se programa para el siguiente dia: " + diaSemana);

            // Calcular el delay en milisegundos hasta el siguiente día de la semana
            long delay = Duration.between(ahora, proximoDia).toMillis();

            // Programar la tarea recurrente indefinidamente para ese día de la semana
            scheduler.scheduleAtFixedRate(() -> {
                System.out.println("Ejecutando tarea para " + diaSemana + " con filtro: " + filtro);
                // Aquí iría la lógica de ejecución de la tarea para este día

                // Crear la tarea que se encolará
                Callable<String> tareaParaProgramar = () -> conectorGeneralService
                        .usarConectorProgramacionFiltroConyMed("Lectura caso 6", programacionAMI,
                                vcSeriesAReintentarFiltrado_, reintentosRestantes);

                // Usar GeneradorDeColas para encolar la tarea
                CompletableFuture<String> future = generadorDeColas.encolarSolicitud("C_" +
                        programacionAMI.getGrupoMedidores().getVcidentificador(),
                        tareaParaProgramar);

                try {
                    // Esperar a que se complete la tarea
                    String resultadoTarea = future.get(); // Este método bloquea hasta que la tarea esté completa
                    String vcSeriesAReintentarFiltrado = resultadoTarea;

                    // Reintentar la tarea si hay medidores no leídos
                    if (!"[]".equalsIgnoreCase(vcSeriesAReintentarFiltrado) && reintentosRestantes > 0) {
                        System.out.println("Reintentando la tarea en 1 minuto debido a medidores no leídos.");
                        int LapsoMinReintentos = programacionAMI.getParametrizacionProg().getNdelayMin();
                        Long delayReintento = LapsoMinReintentos * 60 * 1000L;

                        // Programar la tarea para 1 minuto después solo con los medidores no leídos
                        programarReintentoTareaCaso6(scheduler, programacionAMI, delayReintento,
                                vcSeriesAReintentarFiltrado,
                                reintentosRestantes - 1);

                    } else if (reintentosRestantes == 0) {
                        System.out.println("Se alcanzó el número máximo de reintentos.");
                    } else {
                        System.out.println(
                                "Se leyeron todos los medidores de " + programacionAMI.getGrupoMedidores().getVcfiltro()
                                        + " " + programacionAMI.getGrupoMedidores().getVcidentificador());
                    }

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

            }, delay, TimeUnit.DAYS.toMillis(7), TimeUnit.MILLISECONDS); // Repetir cada 7 días
        }
    }

    private void programarReintentoTareaCaso6(ScheduledExecutorService scheduler, ProgramacionesAMI programacionAMI,
            long delay, String vcSeriesAReintentarFiltrado, int reintentosRestantes) {

        scheduler.schedule(() -> {
            System.out.println("Reintentando tarea para medidores no leídos...");

            // Crear la tarea para reintentar
            Callable<String> tareaParaReintentar = () -> conectorGeneralService
                    .usarConectorProgramacionFiltroConyMed("Lectura reintento caso 6", programacionAMI,
                            vcSeriesAReintentarFiltrado, reintentosRestantes);

            // Usar GeneradorDeColas para encolar la tarea
            CompletableFuture<String> future = generadorDeColas.encolarSolicitud("C_" +
                    programacionAMI.getGrupoMedidores().getVcidentificador(),
                    tareaParaReintentar);

            try {
                // Esperar a que se complete la tarea
                String resultadoReintento = future.get(); // Bloquea hasta que la tarea esté completa
                String vcSeriesAReintentarFiltradoNuevo = resultadoReintento;

                // Si quedan medidores por leer y hay reintentos disponibles, reprograma
                // nuevamente
                if (!"[]".equalsIgnoreCase(vcSeriesAReintentarFiltradoNuevo) && reintentosRestantes > 0) {
                    System.out.println("Reintentando la tarea nuevamente en 1 minuto...");
                    int LapsoMinReintentos = programacionAMI.getParametrizacionProg().getNdelayMin();
                    Long delayReintento = LapsoMinReintentos * 60 * 1000L;
                    programarReintentoTareaCaso6(scheduler, programacionAMI, delayReintento,
                            vcSeriesAReintentarFiltradoNuevo,
                            reintentosRestantes - 1);
                } else if (reintentosRestantes == 0) {
                    System.out.println("Se alcanzó el número máximo de reintentos.");
                } else {
                    System.out.println("Se completó la lectura de todos los medidores.");
                }

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        }, delay, TimeUnit.MILLISECONDS); // Programar para ejecutarse después de 1 minuto
    }

    private void programarTareasFrecuentesCaso7(List<DayOfWeek> diasSemana, LocalDateTime tiempoInicio,
            ProgramacionesAMI programacionAMI, String filtro, String vcSeriesAReintentarFiltrado_,
            int reintentosRestantes) {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        LocalDateTime ahora = LocalDateTime.now();

        // Iterar sobre cada día de la semana y programar la tarea
        for (DayOfWeek diaSemana : diasSemana) {
            LocalDateTime proximoDia = ahora.with(TemporalAdjusters.nextOrSame(diaSemana))
                    .withHour(tiempoInicio.getHour())
                    .withMinute(tiempoInicio.getMinute()).withSecond(0);

            System.out.println("Se programa para el siguiente dia: " + diaSemana);

            // Calcular el delay en milisegundos hasta el siguiente día de la semana
            long delay = Duration.between(ahora, proximoDia).toMillis();

            // Programar la tarea recurrente indefinidamente para ese día de la semana
            scheduler.scheduleAtFixedRate(() -> {

                System.out.println("Ejecutando tarea para " + diaSemana + " con filtro: " + filtro);
                // Aquí iría la lógica de ejecución de la tarea para este día

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
                            Callable<String> tareaParaProgramar = () -> conectorGeneralService
                                    .usarConectorProgramacionFiltroMedidores("Lectura caso 7", programacionAMI,
                                            vcserie, reintentosRestantes);

                            // Usar GeneradorDeColas para encolar la tarea
                            CompletableFuture<String> future = generadorDeColas.encolarSolicitud("M_" + vcserie,
                                    tareaParaProgramar);

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

                // Reintentar la tarea si hay medidores no leídos
                if (!"[]".equalsIgnoreCase(vcSeriesAReintentarFiltrado) && reintentosRestantes > 0) {
                    System.out.println("Reintentando la tarea en 1 minuto debido a medidores no leídos.");
                    int LapsoMinReintentos = programacionAMI.getParametrizacionProg().getNdelayMin();
                    Long delayReintento = LapsoMinReintentos * 60 * 1000L;

                    // Programar la tarea para 1 minuto después solo con los medidores no leídos
                    programarReintentoTareaCaso7(scheduler, programacionAMI, delayReintento,
                            vcSeriesAReintentarFiltrado,
                            reintentosRestantes - 1);

                } else if (reintentosRestantes == 0) {
                    System.out.println("Se alcanzó el número máximo de reintentos.");
                } else {
                    System.out.println(
                            "Se leyeron todos los medidores de " + programacionAMI.getGrupoMedidores().getVcfiltro()
                                    + " " + programacionAMI.getGrupoMedidores().getVcidentificador());
                }
            }, delay, TimeUnit.DAYS.toMillis(7), TimeUnit.MILLISECONDS); // Repetir cada 7 días
        }
    }

    private void programarReintentoTareaCaso7(ScheduledExecutorService scheduler, ProgramacionesAMI programacionAMI,
            long delay, String vcSeriesAReintentarFiltrado, int reintentosRestantes) {

        scheduler.schedule(() -> {
            System.out.println("Reintentando tarea para medidores no leídos...");

            String jsseriesMed = "";
            List<String> vcSeriesAReintentar = new ArrayList<>();
            // Crear una lista para almacenar los CompletableFutures
            List<CompletableFuture<String>> futuresList = new ArrayList<>();

            if (!"EstadoInicio".equalsIgnoreCase(vcSeriesAReintentarFiltrado)) {
                jsseriesMed = vcSeriesAReintentarFiltrado;
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
                        Callable<String> tareaParaProgramar = () -> conectorGeneralService
                                .usarConectorProgramacionFiltroMedidores("Lectura Reintento caso 7", programacionAMI,
                                        vcserie, reintentosRestantes);

                        // Usar GeneradorDeColas para encolar la tarea
                        CompletableFuture<String> future = generadorDeColas.encolarSolicitud("M_" + vcserie,
                                tareaParaProgramar);

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
            String vcSeriesAReintentarFiltradoNuevo = vcSeriesAReintentar.stream()
                    .filter(serie -> !serie.isEmpty()) // Filtra las cadenas vacías
                    .map(serie -> "\"" + serie + "\"") // Añade las comillas a cada serie
                    .collect(Collectors.joining(", ", "[", "]")); // Une con comas y encierra en corchetes

            // Imprimir el resultado
            System.out.println(vcSeriesAReintentarFiltrado);

            // Si quedan medidores por leer y hay reintentos disponibles, reprograma
            // nuevamente
            if (!"[]".equalsIgnoreCase(vcSeriesAReintentarFiltradoNuevo) && reintentosRestantes > 0) {
                System.out.println("Reintentando la tarea nuevamente en 1 minuto...");
                int LapsoMinReintentos = programacionAMI.getParametrizacionProg().getNdelayMin();
                Long delayReintento = LapsoMinReintentos * 60 * 1000L;
                programarReintentoTareaCaso7(scheduler, programacionAMI, delayReintento,
                        vcSeriesAReintentarFiltradoNuevo,
                        reintentosRestantes - 1);
            } else if (reintentosRestantes == 0) {
                System.out.println("Se alcanzó el número máximo de reintentos.");
            } else {
                System.out.println("Se completó la lectura de todos los medidores.");
            }

        }, delay, TimeUnit.MILLISECONDS); // Programar para ejecutarse después de 1 minuto
    }

    private void programarTareasFrecuentesCaso8(List<DayOfWeek> diasSemana, LocalDateTime tiempoInicio,
            ProgramacionesAMI programacionAMI, String filtro, String vcSeriesAReintentarFiltrado_,
            int reintentosRestantes) {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        LocalDateTime ahora = LocalDateTime.now();

        // Iterar sobre cada día de la semana y programar la tarea
        for (DayOfWeek diaSemana : diasSemana) {
            LocalDateTime proximoDia = ahora.with(TemporalAdjusters.nextOrSame(diaSemana))
                    .withHour(tiempoInicio.getHour())
                    .withMinute(tiempoInicio.getMinute()).withSecond(0);

            System.out.println("Se programa para el siguiente dia: " + diaSemana);

            // Calcular el delay en milisegundos hasta el siguiente día de la semana
            long delay = Duration.between(ahora, proximoDia).toMillis();

            // Programar la tarea recurrente indefinidamente para ese día de la semana
            scheduler.scheduleAtFixedRate(() -> {

                System.out.println("Ejecutando tarea para " + diaSemana + " con filtro: " + filtro);

                String jsseriesMed = "";
                List<String> vcSeriesAReintentar = new ArrayList<>();
                // Crear una lista para almacenar los CompletableFutures
                List<CompletableFuture<String>> futuresList = new ArrayList<>();

                if (!"EstadoInicio".equalsIgnoreCase(vcSeriesAReintentarFiltrado_)) {
                    jsseriesMed = vcSeriesAReintentarFiltrado_;
                } else {
                    String identidicadorSic = programacionAMI.getGrupoMedidores().getVcidentificador();
                    List<Medidores> medidores = medidoresService.findByVcsic(identidicadorSic);
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
                            Callable<String> tareaParaProgramar = () -> conectorGeneralService
                                    .usarConectorProgramacionFiltroSIC("Lectura caso 8", programacionAMI, vcserie, reintentosRestantes);

                            // Usar GeneradorDeColas para encolar la tarea
                            CompletableFuture<String> future = generadorDeColas.encolarSolicitud("M_" + vcserie,
                                    tareaParaProgramar);

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

                // Reintentar la tarea si hay medidores no leídos
                if (!"[]".equalsIgnoreCase(vcSeriesAReintentarFiltrado) && reintentosRestantes > 0) {
                    System.out.println("Reintentando la tarea en 1 minuto debido a medidores no leídos.");
                    int LapsoMinReintentos = programacionAMI.getParametrizacionProg().getNdelayMin();
                    Long delayReintento = LapsoMinReintentos * 60 * 1000L;

                    // Programar la tarea para 1 minuto después solo con los medidores no leídos
                    programarReintentoTareaCaso8(scheduler, programacionAMI, delayReintento,
                            vcSeriesAReintentarFiltrado,
                            reintentosRestantes - 1);

                } else if (reintentosRestantes == 0) {
                    System.out.println("Se alcanzó el número máximo de reintentos.");
                } else {
                    System.out.println(
                            "Se leyeron todos los medidores de " + programacionAMI.getGrupoMedidores().getVcfiltro()
                                    + " " + programacionAMI.getGrupoMedidores().getVcidentificador());
                }
            }, delay, TimeUnit.DAYS.toMillis(7), TimeUnit.MILLISECONDS); // Repetir cada 7 días
        }
    }

    private void programarReintentoTareaCaso8(ScheduledExecutorService scheduler, ProgramacionesAMI programacionAMI,
            long delay, String vcSeriesAReintentarFiltrado, int reintentosRestantes) {

        scheduler.schedule(() -> {
            System.out.println("Reintentando tarea para medidores no leídos...");

            String jsseriesMed = "";
            List<String> vcSeriesAReintentar = new ArrayList<>();
            // Crear una lista para almacenar los CompletableFutures
            List<CompletableFuture<String>> futuresList = new ArrayList<>();

            if (!"EstadoInicio".equalsIgnoreCase(vcSeriesAReintentarFiltrado)) {
                jsseriesMed = vcSeriesAReintentarFiltrado;
            } else {
                String identidicadorSic = programacionAMI.getGrupoMedidores().getVcidentificador();
                List<Medidores> medidores = medidoresService.findByVcsic(identidicadorSic);
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
                        Callable<String> tareaParaProgramar = () -> conectorGeneralService
                                .usarConectorProgramacionFiltroSIC("Lectura caso 8", programacionAMI, vcserie, reintentosRestantes);

                        // Usar GeneradorDeColas para encolar la tarea
                        CompletableFuture<String> future = generadorDeColas.encolarSolicitud("M_" + vcserie,
                                tareaParaProgramar);

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
            String vcSeriesAReintentarFiltradoNuevo = vcSeriesAReintentar.stream()
                    .filter(serie -> !serie.isEmpty()) // Filtra las cadenas vacías
                    .map(serie -> "\"" + serie + "\"") // Añade las comillas a cada serie
                    .collect(Collectors.joining(", ", "[", "]")); // Une con comas y encierra en corchetes

            // Imprimir el resultado
            System.out.println(vcSeriesAReintentarFiltradoNuevo);
            // Si quedan medidores por leer y hay reintentos disponibles, reprograma
            // nuevamente
            if (!"[]".equalsIgnoreCase(vcSeriesAReintentarFiltradoNuevo) && reintentosRestantes > 0) {
                System.out.println("Reintentando la tarea nuevamente en 1 minuto...");
                int LapsoMinReintentos = programacionAMI.getParametrizacionProg().getNdelayMin();
                Long delayReintento = LapsoMinReintentos * 60 * 1000L;
                programarReintentoTareaCaso8(scheduler, programacionAMI, delayReintento,
                        vcSeriesAReintentarFiltradoNuevo,
                        reintentosRestantes - 1);
            } else if (reintentosRestantes == 0) {
                System.out.println("Se alcanzó el número máximo de reintentos.");
            } else {
                System.out.println("Se completó la lectura de todos los medidores.");
            }

        }, delay, TimeUnit.MILLISECONDS); // Programar para ejecutarse después de 1 minuto
    }

    private void programarTareasRecurrenteCaso9(List<DayOfWeek> diasSemana, LocalDateTime tiempoInicio,
            ProgramacionesAMI programacionAMI, String filtro, String vcSeriesAReintentarFiltrado_,
            int reintentosRestantes) {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        LocalDateTime ahora = LocalDateTime.now();

        // Deserializamos la frecuencia de lectura
        String jsfrecuenciaLecturaLote = programacionAMI.getParametrizacionProg().getJsfrecuenciaLecturaLote();
        Map<String, Map<String, String>> frecuenciaLecturaLote = deserializarFrecuenciaLectura(jsfrecuenciaLecturaLote);

        // Iterar sobre cada día de la semana y programar la tarea según la hora de
        // inicio del JSON
        for (DayOfWeek diaSemana : diasSemana) {
            String diaSemanaStr = obtenerDiaSemanaString(diaSemana); // Método para convertir DayOfWeek a String en
                                                                     // español
            if (frecuenciaLecturaLote.containsKey(diaSemanaStr)) {
                // Obtenemos la hora de inicio y fin del JSON
                String horaInicioStr = frecuenciaLecturaLote.get(diaSemanaStr).get("horaInicio");
                // String horaFinStr = frecuenciaLecturaLote.get(diaSemanaStr).get("horaFin");

                // Convertir las horas de inicio y fin en LocalTime
                LocalTime horaInicio = convertirHoraString(horaInicioStr);
                // LocalTime horaFin = convertirHoraString(horaFinStr);

                // Programar las tareas dentro del rango de horas especificado
                LocalDateTime proximoDia = ahora.with(TemporalAdjusters.nextOrSame(diaSemana))
                        .withHour(horaInicio.getHour())
                        .withMinute(horaInicio.getMinute()).withSecond(0);

                System.out.println("Se programa para el siguiente dia: " + diaSemana + " a la hora: " + horaInicio);

                // Calcular el delay en milisegundos hasta el siguiente día de la semana y hora
                // de inicio
                long delay = Duration.between(ahora, proximoDia).toMillis();

                // Programar la tarea recurrente para ese día y dentro del rango de horas
                scheduler.scheduleAtFixedRate(() -> {
                    System.out.println("Ejecutando tarea para " + diaSemanaStr + " con filtro: " + filtro
                            + " a la hora: " + horaInicio);

                    // Crear la tarea que se encolará
                    Callable<String> tareaParaProgramar = () -> conectorGeneralService
                            .usarConectorProgramacionFiltroConcentrador("Lectura caso 9", programacionAMI,
                                    vcSeriesAReintentarFiltrado_, reintentosRestantes);

                    // Usar GeneradorDeColas para encolar la tarea
                    CompletableFuture<String> future = generadorDeColas.encolarSolicitud("C_" +
                            programacionAMI.getGrupoMedidores().getVcidentificador(),
                            tareaParaProgramar);

                    try {
                        // Esperar a que se complete la tarea
                        String resultadoTarea = future.get(); // Este método bloquea hasta que la tarea esté completa
                        String vcSeriesAReintentarFiltrado = resultadoTarea;

                        // Reintentar la tarea si hay medidores no leídos
                        if (!"[]".equalsIgnoreCase(vcSeriesAReintentarFiltrado) && reintentosRestantes > 0) {
                            System.out.println("Reintentando la tarea en 1 minuto debido a medidores no leídos.");
                            int LapsoMinReintentos = programacionAMI.getParametrizacionProg().getNdelayMin();
                            Long delayReintento = LapsoMinReintentos * 60 * 1000L;

                            // Programar la tarea para 1 minuto después solo con los medidores no leídos
                            programarReintentoTareaCaso9(scheduler, programacionAMI, delayReintento,
                                    vcSeriesAReintentarFiltrado,
                                    reintentosRestantes - 1);

                        } else if (reintentosRestantes == 0) {
                            System.out.println("Se alcanzó el número máximo de reintentos.");
                        } else {
                            System.out.println(
                                    "Se leyeron todos los medidores de "
                                            + programacionAMI.getGrupoMedidores().getVcfiltro()
                                            + " " + programacionAMI.getGrupoMedidores().getVcidentificador());
                        }

                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                }, delay, TimeUnit.DAYS.toMillis(7), TimeUnit.MILLISECONDS); // Repetir cada 7 días
            }
        }
    }

    private void programarReintentoTareaCaso9(ScheduledExecutorService scheduler, ProgramacionesAMI programacionAMI,
            long delay, String vcSeriesAReintentarFiltrado, int reintentosRestantes) {

        scheduler.schedule(() -> {
            System.out.println("Reintentando tarea para medidores no leídos...");

            // Crear la tarea que se encolará
            Callable<String> tareaParaProgramar = () -> conectorGeneralService
                    .usarConectorProgramacionFiltroConcentrador("Lectura caso 9", programacionAMI,
                            vcSeriesAReintentarFiltrado, reintentosRestantes);

            // Usar GeneradorDeColas para encolar la tarea
            CompletableFuture<String> future = generadorDeColas.encolarSolicitud("C_" +
                    programacionAMI.getGrupoMedidores().getVcidentificador(),
                    tareaParaProgramar);

            try {
                // Esperar a que se complete la tarea
                String resultadoReintento = future.get(); // Bloquea hasta que la tarea esté completa
                String vcSeriesAReintentarFiltradoNuevo = resultadoReintento;

                // Si quedan medidores por leer y hay reintentos disponibles, reprograma
                // nuevamente
                if (!"[]".equalsIgnoreCase(vcSeriesAReintentarFiltradoNuevo) && reintentosRestantes > 0) {
                    System.out.println("Reintentando la tarea nuevamente en 1 minuto...");
                    int LapsoMinReintentos = programacionAMI.getParametrizacionProg().getNdelayMin();
                    Long delayReintento = LapsoMinReintentos * 60 * 1000L;
                    programarReintentoTareaCaso9(scheduler, programacionAMI, delayReintento,
                            vcSeriesAReintentarFiltradoNuevo,
                            reintentosRestantes - 1);
                } else if (reintentosRestantes == 0) {
                    System.out.println("Se alcanzó el número máximo de reintentos.");
                } else {
                    System.out.println("Se completó la lectura de todos los medidores.");
                }

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        }, delay, TimeUnit.MILLISECONDS); // Programar para ejecutarse después de 1 minuto
    }

    private void programarTareasRecurrenteCaso10(List<DayOfWeek> diasSemana, LocalDateTime tiempoInicio,
            ProgramacionesAMI programacionAMI, String filtro, String vcSeriesAReintentarFiltrado_,
            int reintentosRestantes) {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        LocalDateTime ahora = LocalDateTime.now();

        // Deserializamos la frecuencia de lectura
        String jsfrecuenciaLecturaLote = programacionAMI.getParametrizacionProg().getJsfrecuenciaLecturaLote();
        Map<String, Map<String, String>> frecuenciaLecturaLote = deserializarFrecuenciaLectura(jsfrecuenciaLecturaLote);

        // Iterar sobre cada día de la semana y programar la tarea según la hora de
        // inicio del JSON
        for (DayOfWeek diaSemana : diasSemana) {
            String diaSemanaStr = obtenerDiaSemanaString(diaSemana); // Método para convertir DayOfWeek a String en
                                                                     // español
            if (frecuenciaLecturaLote.containsKey(diaSemanaStr)) {
                // Obtenemos la hora de inicio y fin del JSON
                String horaInicioStr = frecuenciaLecturaLote.get(diaSemanaStr).get("horaInicio");
                // String horaFinStr = frecuenciaLecturaLote.get(diaSemanaStr).get("horaFin");

                // Convertir las horas de inicio y fin en LocalTime
                LocalTime horaInicio = convertirHoraString(horaInicioStr);
                // LocalTime horaFin = convertirHoraString(horaFinStr);

                // Programar las tareas dentro del rango de horas especificado
                LocalDateTime proximoDia = ahora.with(TemporalAdjusters.nextOrSame(diaSemana))
                        .withHour(horaInicio.getHour())
                        .withMinute(horaInicio.getMinute()).withSecond(0);

                System.out.println("Se programa para el siguiente dia: " + diaSemana + " a la hora: " + horaInicio);

                // Calcular el delay en milisegundos hasta el siguiente día de la semana y hora
                // de inicio
                long delay = Duration.between(ahora, proximoDia).toMillis();

                // Programar la tarea recurrente para ese día y dentro del rango de horas
                scheduler.scheduleAtFixedRate(() -> {
                    System.out.println("Ejecutando tarea para " + diaSemanaStr + " con filtro: " + filtro
                            + " a la hora: " + horaInicio);

                    // Crear la tarea que se encolará
                    Callable<String> tareaParaProgramar = () -> conectorGeneralService
                            .usarConectorProgramacionFiltroConyMed("Lectura caso 10", programacionAMI,
                                    vcSeriesAReintentarFiltrado_, reintentosRestantes);

                    // Usar GeneradorDeColas para encolar la tarea
                    CompletableFuture<String> future = generadorDeColas.encolarSolicitud("C_" +
                            programacionAMI.getGrupoMedidores().getVcidentificador(),
                            tareaParaProgramar);

                    try {
                        // Esperar a que se complete la tarea
                        String resultadoTarea = future.get(); // Este método bloquea hasta que la tarea esté completa
                        String vcSeriesAReintentarFiltrado = resultadoTarea;

                        // Reintentar la tarea si hay medidores no leídos
                        if (!"[]".equalsIgnoreCase(vcSeriesAReintentarFiltrado) && reintentosRestantes > 0) {
                            System.out.println("Reintentando la tarea en 1 minuto debido a medidores no leídos.");
                            int LapsoMinReintentos = programacionAMI.getParametrizacionProg().getNdelayMin();
                            Long delayReintento = LapsoMinReintentos * 60 * 1000L;

                            // Programar la tarea para 1 minuto después solo con los medidores no leídos
                            programarReintentoTareaCaso10(scheduler, programacionAMI, delayReintento,
                                    vcSeriesAReintentarFiltrado,
                                    reintentosRestantes - 1);

                        } else if (reintentosRestantes == 0) {
                            System.out.println("Se alcanzó el número máximo de reintentos.");
                        } else {
                            System.out.println(
                                    "Se leyeron todos los medidores de "
                                            + programacionAMI.getGrupoMedidores().getVcfiltro()
                                            + " " + programacionAMI.getGrupoMedidores().getVcidentificador());
                        }

                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                }, delay, TimeUnit.DAYS.toMillis(7), TimeUnit.MILLISECONDS); // Repetir cada 7 días
            }
        }
    }

    private void programarReintentoTareaCaso10(ScheduledExecutorService scheduler, ProgramacionesAMI programacionAMI,
            long delay, String vcSeriesAReintentarFiltrado, int reintentosRestantes) {

        scheduler.schedule(() -> {
            System.out.println("Reintentando tarea para medidores no leídos...");

            // Crear la tarea que se encolará
            Callable<String> tareaParaProgramar = () -> conectorGeneralService
                    .usarConectorProgramacionFiltroConyMed("Lectura caso 10", programacionAMI,
                            vcSeriesAReintentarFiltrado, reintentosRestantes);

            // Usar GeneradorDeColas para encolar la tarea
            CompletableFuture<String> future = generadorDeColas.encolarSolicitud("C_" +
                    programacionAMI.getGrupoMedidores().getVcidentificador(),
                    tareaParaProgramar);

            try {
                // Esperar a que se complete la tarea
                String resultadoReintento = future.get(); // Bloquea hasta que la tarea esté completa
                String vcSeriesAReintentarFiltradoNuevo = resultadoReintento;

                // Si quedan medidores por leer y hay reintentos disponibles, reprograma
                // nuevamente
                if (!"[]".equalsIgnoreCase(vcSeriesAReintentarFiltradoNuevo) && reintentosRestantes > 0) {
                    System.out.println("Reintentando la tarea nuevamente en 1 minuto...");
                    int LapsoMinReintentos = programacionAMI.getParametrizacionProg().getNdelayMin();
                    Long delayReintento = LapsoMinReintentos * 60 * 1000L;
                    programarReintentoTareaCaso10(scheduler, programacionAMI, delayReintento,
                            vcSeriesAReintentarFiltradoNuevo,
                            reintentosRestantes - 1);
                } else if (reintentosRestantes == 0) {
                    System.out.println("Se alcanzó el número máximo de reintentos.");
                } else {
                    System.out.println("Se completó la lectura de todos los medidores.");
                }

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        }, delay, TimeUnit.MILLISECONDS); // Programar para ejecutarse después de 1 minuto
    }

    private void programarTareasRecurrenteCaso11(List<DayOfWeek> diasSemana, LocalDateTime tiempoInicio,
            ProgramacionesAMI programacionAMI, String filtro, String vcSeriesAReintentarFiltrado_,
            int reintentosRestantes) {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        LocalDateTime ahora = LocalDateTime.now();

        // Deserializamos la frecuencia de lectura
        String jsfrecuenciaLecturaLote = programacionAMI.getParametrizacionProg().getJsfrecuenciaLecturaLote();
        Map<String, Map<String, String>> frecuenciaLecturaLote = deserializarFrecuenciaLectura(jsfrecuenciaLecturaLote);

        // Iterar sobre cada día de la semana y programar la tarea según la hora de
        // inicio del JSON
        for (DayOfWeek diaSemana : diasSemana) {
            String diaSemanaStr = obtenerDiaSemanaString(diaSemana); // Método para convertir DayOfWeek a String en
                                                                     // español
            if (frecuenciaLecturaLote.containsKey(diaSemanaStr)) {
                // Obtenemos la hora de inicio y fin del JSON
                String horaInicioStr = frecuenciaLecturaLote.get(diaSemanaStr).get("horaInicio");
                // String horaFinStr = frecuenciaLecturaLote.get(diaSemanaStr).get("horaFin");

                // Convertir las horas de inicio y fin en LocalTime
                LocalTime horaInicio = convertirHoraString(horaInicioStr);
                // LocalTime horaFin = convertirHoraString(horaFinStr);

                // Programar las tareas dentro del rango de horas especificado
                LocalDateTime proximoDia = ahora.with(TemporalAdjusters.nextOrSame(diaSemana))
                        .withHour(horaInicio.getHour())
                        .withMinute(horaInicio.getMinute()).withSecond(0);

                System.out.println("Se programa para el siguiente dia: " + diaSemana + " a la hora: " + horaInicio);

                // Calcular el delay en milisegundos hasta el siguiente día de la semana y hora
                // de inicio
                long delay = Duration.between(ahora, proximoDia).toMillis();

                // Programar la tarea recurrente para ese día y dentro del rango de horas
                scheduler.scheduleAtFixedRate(() -> {
                    System.out.println("Ejecutando tarea para " + diaSemanaStr + " con filtro: " + filtro
                            + " a la hora: " + horaInicio);

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
                                Callable<String> tareaParaProgramar = () -> conectorGeneralService
                                        .usarConectorProgramacionFiltroMedidores("Lectura caso 11", programacionAMI,
                                                vcserie, reintentosRestantes);

                                // Usar GeneradorDeColas para encolar la tarea
                                CompletableFuture<String> future = generadorDeColas.encolarSolicitud("M_" + vcserie,
                                        tareaParaProgramar);

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

                    // Reintentar la tarea si hay medidores no leídos
                    if (!"[]".equalsIgnoreCase(vcSeriesAReintentarFiltrado) && reintentosRestantes > 0) {
                        System.out.println("Reintentando la tarea en 1 minuto debido a medidores no leídos.");
                        int LapsoMinReintentos = programacionAMI.getParametrizacionProg().getNdelayMin();
                        Long delayReintento = LapsoMinReintentos * 60 * 1000L;

                        // Programar la tarea para 1 minuto después solo con los medidores no leídos
                        programarReintentoTareaCaso11(scheduler, programacionAMI, delayReintento,
                                vcSeriesAReintentarFiltrado,
                                reintentosRestantes - 1);

                    } else if (reintentosRestantes == 0) {
                        System.out.println("Se alcanzó el número máximo de reintentos.");
                    } else {
                        System.out.println(
                                "Se leyeron todos los medidores de " + programacionAMI.getGrupoMedidores().getVcfiltro()
                                        + " " + programacionAMI.getGrupoMedidores().getVcidentificador());
                    }

                }, delay, TimeUnit.DAYS.toMillis(7), TimeUnit.MILLISECONDS); // Repetir cada 7 días
            }
        }
    }

    private void programarReintentoTareaCaso11(ScheduledExecutorService scheduler, ProgramacionesAMI programacionAMI,
            long delay, String vcSeriesAReintentarFiltrado, int reintentosRestantes) {

        scheduler.schedule(() -> {
            System.out.println("Reintentando tarea para medidores no leídos...");

            String jsseriesMed = "";
            List<String> vcSeriesAReintentar = new ArrayList<>();
            // Crear una lista para almacenar los CompletableFutures
            List<CompletableFuture<String>> futuresList = new ArrayList<>();

            if (!"EstadoInicio".equalsIgnoreCase(vcSeriesAReintentarFiltrado)) {
                jsseriesMed = vcSeriesAReintentarFiltrado;
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
                        Callable<String> tareaParaProgramar = () -> conectorGeneralService
                                .usarConectorProgramacionFiltroMedidores("Lectura Reintento caso 11", programacionAMI,
                                        vcserie, reintentosRestantes);

                        // Usar GeneradorDeColas para encolar la tarea
                        CompletableFuture<String> future = generadorDeColas.encolarSolicitud("M_" + vcserie,
                                tareaParaProgramar);

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
            String vcSeriesAReintentarFiltradoNuevo = vcSeriesAReintentar.stream()
                    .filter(serie -> !serie.isEmpty()) // Filtra las cadenas vacías
                    .map(serie -> "\"" + serie + "\"") // Añade las comillas a cada serie
                    .collect(Collectors.joining(", ", "[", "]")); // Une con comas y encierra en corchetes

            // Imprimir el resultado
            System.out.println(vcSeriesAReintentarFiltradoNuevo);

            // Si quedan medidores por leer y hay reintentos disponibles, reprograma
            // nuevamente
            if (!"[]".equalsIgnoreCase(vcSeriesAReintentarFiltradoNuevo) && reintentosRestantes > 0) {
                System.out.println("Reintentando la tarea nuevamente en 1 minuto...");
                int LapsoMinReintentos = programacionAMI.getParametrizacionProg().getNdelayMin();
                Long delayReintento = LapsoMinReintentos * 60 * 1000L;
                programarReintentoTareaCaso11(scheduler, programacionAMI, delayReintento,
                        vcSeriesAReintentarFiltradoNuevo,
                        reintentosRestantes - 1);
            } else if (reintentosRestantes == 0) {
                System.out.println("Se alcanzó el número máximo de reintentos.");
            } else {
                System.out.println("Se completó la lectura de todos los medidores.");
            }

        }, delay, TimeUnit.MILLISECONDS); // Programar para ejecutarse después de 1 minuto
    }

    private void programarTareasRecurrenteCaso12(List<DayOfWeek> diasSemana, LocalDateTime tiempoInicio,
            ProgramacionesAMI programacionAMI, String filtro, String vcSeriesAReintentarFiltrado_,
            int reintentosRestantes) {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        LocalDateTime ahora = LocalDateTime.now();

        // Deserializamos la frecuencia de lectura
        String jsfrecuenciaLecturaLote = programacionAMI.getParametrizacionProg().getJsfrecuenciaLecturaLote();
        Map<String, Map<String, String>> frecuenciaLecturaLote = deserializarFrecuenciaLectura(jsfrecuenciaLecturaLote);

        // Iterar sobre cada día de la semana y programar la tarea según la hora de
        // inicio del JSON
        for (DayOfWeek diaSemana : diasSemana) {
            String diaSemanaStr = obtenerDiaSemanaString(diaSemana); // Método para convertir DayOfWeek a String en
                                                                     // español
            if (frecuenciaLecturaLote.containsKey(diaSemanaStr)) {
                // Obtenemos la hora de inicio y fin del JSON
                String horaInicioStr = frecuenciaLecturaLote.get(diaSemanaStr).get("horaInicio");
                // String horaFinStr = frecuenciaLecturaLote.get(diaSemanaStr).get("horaFin");

                // Convertir las horas de inicio y fin en LocalTime
                LocalTime horaInicio = convertirHoraString(horaInicioStr);
                // LocalTime horaFin = convertirHoraString(horaFinStr);

                // Programar las tareas dentro del rango de horas especificado
                LocalDateTime proximoDia = ahora.with(TemporalAdjusters.nextOrSame(diaSemana))
                        .withHour(horaInicio.getHour())
                        .withMinute(horaInicio.getMinute()).withSecond(0);

                System.out.println("Se programa para el siguiente dia: " + diaSemana + " a la hora: " + horaInicio);

                // Calcular el delay en milisegundos hasta el siguiente día de la semana y hora
                // de inicio
                long delay = Duration.between(ahora, proximoDia).toMillis();

                // Programar la tarea recurrente para ese día y dentro del rango de horas
                scheduler.scheduleAtFixedRate(() -> {
                    System.out.println("Ejecutando tarea para " + diaSemanaStr + " con filtro: " + filtro
                            + " a la hora: " + horaInicio);

                    String jsseriesMed = "";
                    List<String> vcSeriesAReintentar = new ArrayList<>();
                    // Crear una lista para almacenar los CompletableFutures
                    List<CompletableFuture<String>> futuresList = new ArrayList<>();

                    if (!"EstadoInicio".equalsIgnoreCase(vcSeriesAReintentarFiltrado_)) {
                        jsseriesMed = vcSeriesAReintentarFiltrado_;
                    } else {
                        String identidicadorSic = programacionAMI.getGrupoMedidores().getVcidentificador();
                        List<Medidores> medidores = medidoresService.findByVcsic(identidicadorSic);
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
                                Callable<String> tareaParaProgramar = () -> conectorGeneralService
                                        .usarConectorProgramacionFiltroSIC("Lectura caso 12", programacionAMI, vcserie, reintentosRestantes);

                                // Usar GeneradorDeColas para encolar la tarea
                                CompletableFuture<String> future = generadorDeColas.encolarSolicitud("M_" + vcserie,
                                        tareaParaProgramar);

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

                    // Reintentar la tarea si hay medidores no leídos
                    if (!"[]".equalsIgnoreCase(vcSeriesAReintentarFiltrado) && reintentosRestantes > 0) {
                        System.out.println("Reintentando la tarea en 1 minuto debido a medidores no leídos.");
                        int LapsoMinReintentos = programacionAMI.getParametrizacionProg().getNdelayMin();
                        Long delayReintento = LapsoMinReintentos * 60 * 1000L;

                        // Programar la tarea para 1 minuto después solo con los medidores no leídos
                        programarReintentoTareaCaso12(scheduler, programacionAMI, delayReintento,
                                vcSeriesAReintentarFiltrado,
                                reintentosRestantes - 1);

                    } else if (reintentosRestantes == 0) {
                        System.out.println("Se alcanzó el número máximo de reintentos.");
                    } else {
                        System.out.println(
                                "Se leyeron todos los medidores de " + programacionAMI.getGrupoMedidores().getVcfiltro()
                                        + " " + programacionAMI.getGrupoMedidores().getVcidentificador());
                    }

                }, delay, TimeUnit.DAYS.toMillis(7), TimeUnit.MILLISECONDS); // Repetir cada 7 días
            }
        }
    }

    private void programarReintentoTareaCaso12(ScheduledExecutorService scheduler, ProgramacionesAMI programacionAMI,
            long delay, String vcSeriesAReintentarFiltrado, int reintentosRestantes) {

        scheduler.schedule(() -> {
            System.out.println("Reintentando tarea para medidores no leídos...");

            String jsseriesMed = "";
            List<String> vcSeriesAReintentar = new ArrayList<>();
            // Crear una lista para almacenar los CompletableFutures
            List<CompletableFuture<String>> futuresList = new ArrayList<>();

            if (!"EstadoInicio".equalsIgnoreCase(vcSeriesAReintentarFiltrado)) {
                jsseriesMed = vcSeriesAReintentarFiltrado;
            } else {
                String identidicadorSic = programacionAMI.getGrupoMedidores().getVcidentificador();
                List<Medidores> medidores = medidoresService.findByVcsic(identidicadorSic);
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
                        Callable<String> tareaParaProgramar = () -> conectorGeneralService
                                .usarConectorProgramacionFiltroSIC("Lectura caso 12", programacionAMI, vcserie, reintentosRestantes);

                        // Usar GeneradorDeColas para encolar la tarea
                        CompletableFuture<String> future = generadorDeColas.encolarSolicitud("M_" + vcserie,
                                tareaParaProgramar);

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
            String vcSeriesAReintentarFiltradoNuevo = vcSeriesAReintentar.stream()
                    .filter(serie -> !serie.isEmpty()) // Filtra las cadenas vacías
                    .map(serie -> "\"" + serie + "\"") // Añade las comillas a cada serie
                    .collect(Collectors.joining(", ", "[", "]")); // Une con comas y encierra en corchetes

            // Imprimir el resultado
            System.out.println(vcSeriesAReintentarFiltradoNuevo);

            // Si quedan medidores por leer y hay reintentos disponibles, reprograma
            // nuevamente
            if (!"[]".equalsIgnoreCase(vcSeriesAReintentarFiltradoNuevo) && reintentosRestantes > 0) {
                System.out.println("Reintentando la tarea nuevamente en 1 minuto...");
                int LapsoMinReintentos = programacionAMI.getParametrizacionProg().getNdelayMin();
                Long delayReintento = LapsoMinReintentos * 60 * 1000L;
                programarReintentoTareaCaso12(scheduler, programacionAMI, delayReintento,
                        vcSeriesAReintentarFiltradoNuevo,
                        reintentosRestantes - 1);
            } else if (reintentosRestantes == 0) {
                System.out.println("Se alcanzó el número máximo de reintentos.");
            } else {
                System.out.println("Se completó la lectura de todos los medidores.");
            }

        }, delay, TimeUnit.MILLISECONDS); // Programar para ejecutarse después de 1 minuto
    }

    // Método para convertir los días de la semana en español a DayOfWeek
    private List<DayOfWeek> convertirDiasSemana(String jsdiasSemana) {
        List<String> diasEnTexto = Arrays
                .asList(jsdiasSemana.replace("[", "").replace("]", "").replace("\"", "").split(","));

        return diasEnTexto.stream().map(dia -> {
            switch (dia.trim().toLowerCase(Locale.ROOT)) {
                case "lunes":
                    return DayOfWeek.MONDAY;
                case "martes":
                    return DayOfWeek.TUESDAY;
                case "miércoles":
                    return DayOfWeek.WEDNESDAY;
                case "jueves":
                    return DayOfWeek.THURSDAY;
                case "viernes":
                    return DayOfWeek.FRIDAY;
                case "sabado":
                    return DayOfWeek.SATURDAY;
                case "domingo":
                    return DayOfWeek.SUNDAY;
                default:
                    throw new IllegalArgumentException("Día no reconocido: " + dia);
            }
        }).collect(Collectors.toList());
    }

    private Map<String, Map<String, String>> deserializarFrecuenciaLectura(String jsfrecuenciaLecturaLote) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Map<String, String>> frecuenciaLecturaLote = null;
        try {
            // Deserializamos el JSON a un Map
            frecuenciaLecturaLote = objectMapper.readValue(jsfrecuenciaLecturaLote,
                    new TypeReference<Map<String, Map<String, String>>>() {
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return frecuenciaLecturaLote;
    }

    // Método para convertir DayOfWeek a String en español
    private String obtenerDiaSemanaString(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return "Lunes";
            case TUESDAY:
                return "Martes";
            case WEDNESDAY:
                return "Miércoles";
            case THURSDAY:
                return "Jueves";
            case FRIDAY:
                return "Viernes";
            case SATURDAY:
                return "Sabado";
            case SUNDAY:
                return "Domingo";
            default:
                throw new IllegalArgumentException("Día no reconocido");
        }
    }

    // Método para convertir la hora en formato de texto ("05:00am") a LocalTime
    private LocalTime convertirHoraString(String horaStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mma", Locale.US);
        return LocalTime.parse(horaStr.toUpperCase(), formatter);
    }
}

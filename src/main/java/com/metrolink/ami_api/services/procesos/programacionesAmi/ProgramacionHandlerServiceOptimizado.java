// package com.metrolink.ami_api.services.procesos.programacionesAmi;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;

// import com.fasterxml.jackson.core.type.TypeReference;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.metrolink.ami_api.models.medidor.Medidores;
// import com.metrolink.ami_api.models.procesos.programacionesAmi.ProgramacionesAMI;
// import com.metrolink.ami_api.services.medidor.MedidoresService;
// import com.metrolink.ami_api.services.procesos.conectorGeneral.ConectorGeneralService;
// import com.metrolink.ami_api.services.procesos.generadorDeColas.GeneradorDeColas;

// import java.sql.Timestamp;
// import java.time.*;
// import java.time.format.DateTimeFormatter;
// import java.time.temporal.TemporalAdjusters;
// import java.util.*;
// import java.util.concurrent.*;
// import java.util.stream.Collectors;

// @Component
// public class ProgramacionHandlerService {

//     @Autowired
//     private ConectorGeneralService conectorGeneralService;

//     @Autowired
//     private GeneradorDeColas generadorDeColas;

//     @Autowired
//     private MedidoresService medidoresService;

//     private enum TipoLectura {
//         UNICA("única"),
//         FRECUENTE_NO_RECURRENTE("frecuente no recurrente"),
//         RECURRENTE("recurrente");

//         private final String value;

//         TipoLectura(String value) {
//             this.value = value;
//         }

//         public static TipoLectura fromString(String text) {
//             for (TipoLectura b : TipoLectura.values()) {
//                 if (b.value.equalsIgnoreCase(text)) {
//                     return b;
//                 }
//             }
//             return null;
//         }
//     }

//     private enum Filtro {
//         CONCENTRADOR("concentrador"),
//         CONCENTRADOR_Y_MEDIDORES("concentradorymedidores"),
//         MEDIDORES("medidores"),
//         FRONTERA_SIC("frontera sic");

//         private final String value;

//         Filtro(String value) {
//             this.value = value;
//         }

//         public static Filtro fromString(String text) {
//             for (Filtro b : Filtro.values()) {
//                 if (b.value.equalsIgnoreCase(text)) {
//                     return b;
//                 }
//             }
//             return null;
//         }
//     }

//     public String manejarProgramacion(ProgramacionesAMI programacionAMI) {

//         TipoLectura tipoLectura = TipoLectura.fromString(programacionAMI.getParametrizacionProg().getVctipoDeLectura());
//         Filtro filtro = Filtro.fromString(programacionAMI.getGrupoMedidores().getVcfiltro());

//         System.out.println("Minutos de delay para cada reintento: " + programacionAMI.getParametrizacionProg().getNdelayMin());

//         if (tipoLectura == null || filtro == null) {
//             System.out.println("Tipo de lectura o filtro no definido");
//             return "CASO NO DEFINIDO";
//         }

//         switch (tipoLectura) {
//             case UNICA:
//                 System.out.println("LECTURA ÚNICA");
//                 return manejarLecturaUnica(filtro, programacionAMI);

//             case FRECUENTE_NO_RECURRENTE:
//                 System.out.println("LECTURA FRECUENTE NO RECURRENTE");
//                 return manejarLecturaFrecuenteNoRecurrente(filtro, programacionAMI);

//             case RECURRENTE:
//                 System.out.println("LECTURA RECURRENTE");
//                 return manejarLecturaRecurrente(filtro, programacionAMI);

//             default:
//                 System.out.println("CASO NO DEFINIDO");
//                 return "CASO NO DEFINIDO";
//         }
//     }

//     private String manejarLecturaUnica(Filtro filtro, ProgramacionesAMI programacionAMI) {
//         String resultado = String.format("Iniciando Programación: %d de periodicidad única con Filtro %s",
//                 programacionAMI.getNcodigo(), filtro.value);

//         Timestamp tiempoInicio = programacionAMI.getParametrizacionProg().getDfechaHoraInicio();
//         System.out.println("Tiempo de inicio programado: " + tiempoInicio);
//         Instant ahora = Instant.now();
//         long delay = Duration.between(ahora, tiempoInicio.toInstant()).toMillis();

//         if (delay <= 0) {
//             System.out.println("La fecha y hora ya han pasado, no se puede programar la tarea.");
//             return resultado;
//         }

//         ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//         int reintentosRestantes = programacionAMI.getParametrizacionProg().getNreintentos() - 1;
//         String estadoInicio = "EstadoInicio";

//         programarTarea(scheduler, programacionAMI, delay, estadoInicio, reintentosRestantes, filtro);

//         return resultado;
//     }

//     private String manejarLecturaFrecuenteNoRecurrente(Filtro filtro, ProgramacionesAMI programacionAMI) {
//         String resultado = String.format("Iniciando Programación: %d de lectura frecuente no recurrente con Filtro %s",
//                 programacionAMI.getNcodigo(), filtro.value);

//         String jsdiasSemana = programacionAMI.getParametrizacionProg().getJsdiasSemana();
//         List<DayOfWeek> diasSemana = convertirDiasSemana(jsdiasSemana);

//         LocalDateTime tiempoInicio = programacionAMI.getParametrizacionProg().getDfechaHoraInicio().toInstant()
//                 .atZone(ZoneId.systemDefault()).toLocalDateTime();

//         int reintentosRestantes = programacionAMI.getParametrizacionProg().getNreintentos() - 1;
//         String estadoInicio = "EstadoInicio";

//         programarTareasFrecuentes(diasSemana, tiempoInicio, programacionAMI, filtro, estadoInicio, reintentosRestantes);

//         return resultado;
//     }

//     private String manejarLecturaRecurrente(Filtro filtro, ProgramacionesAMI programacionAMI) {
//         String resultado = String.format("Iniciando Programación: %d de lectura recurrente con Filtro %s",
//                 programacionAMI.getNcodigo(), filtro.value);

//         String jsdiasSemana = programacionAMI.getParametrizacionProg().getJsdiasSemana();
//         List<DayOfWeek> diasSemana = convertirDiasSemana(jsdiasSemana);

//         String jsfrecuenciaLecturaLote = programacionAMI.getParametrizacionProg().getJsfrecuenciaLecturaLote();
//         Map<String, Map<String, String>> frecuenciaLecturaLote = deserializarFrecuenciaLectura(jsfrecuenciaLecturaLote);

//         int reintentosRestantes = programacionAMI.getParametrizacionProg().getNreintentos() - 1;
//         String estadoInicio = "EstadoInicio";

//         programarTareasRecurrentes(diasSemana, frecuenciaLecturaLote, programacionAMI, filtro, estadoInicio, reintentosRestantes);

//         return resultado;
//     }

//     private void programarTarea(ScheduledExecutorService scheduler, ProgramacionesAMI programacionAMI,
//                                 long delay, String vcSeriesAReintentarFiltrado_, int reintentosRestantes, Filtro filtro) {

//         scheduler.schedule(() -> {
//             try {
//                 Callable<String> tareaParaProgramar = crearTareaPorFiltro(filtro, programacionAMI, vcSeriesAReintentarFiltrado_, reintentosRestantes);
//                 CompletableFuture<String> future = generadorDeColas.encolarSolicitud(
//                         generarIdentificadorCola(filtro, programacionAMI), tareaParaProgramar);

//                 String resultadoTarea = future.get();
//                 String vcSeriesAReintentarFiltrado = resultadoTarea;

//                 if (!"[]".equalsIgnoreCase(vcSeriesAReintentarFiltrado) && reintentosRestantes > 0) {
//                     System.out.println("Reintentando la tarea debido a medidores no leídos.");
//                     int lapsoMinReintentos = programacionAMI.getParametrizacionProg().getNdelayMin();
//                     long delayReintento = lapsoMinReintentos * 60 * 1000L;

//                     programarTarea(scheduler, programacionAMI, delayReintento, vcSeriesAReintentarFiltrado, reintentosRestantes - 1, filtro);
//                 } else if (reintentosRestantes == 0) {
//                     System.out.println("Se alcanzó el número máximo de reintentos.");
//                 } else {
//                     System.out.println("Se leyeron todos los medidores del filtro " + filtro.value);
//                 }
//             } catch (Exception e) {
//                 e.printStackTrace();
//             }
//         }, delay, TimeUnit.MILLISECONDS);
//     }

//     private Callable<String> crearTareaPorFiltro(Filtro filtro, ProgramacionesAMI programacionAMI, String vcSeriesAReintentarFiltrado_, int reintentosRestantes) {
//         switch (filtro) {
//             case CONCENTRADOR:
//                 return () -> conectorGeneralService.usarConectorProgramacionFiltroConcentrador("Lectura", programacionAMI, vcSeriesAReintentarFiltrado_, reintentosRestantes);

//             case CONCENTRADOR_Y_MEDIDORES:
//                 return () -> conectorGeneralService.usarConectorProgramacionFiltroConyMed("Lectura", programacionAMI, vcSeriesAReintentarFiltrado_, reintentosRestantes);

//             case MEDIDORES:
//                 return () -> manejarTareaMedidores(programacionAMI, vcSeriesAReintentarFiltrado_, reintentosRestantes);

//             case FRONTERA_SIC:
//                 return () -> manejarTareaFronteraSIC(programacionAMI, vcSeriesAReintentarFiltrado_, reintentosRestantes);

//             default:
//                 throw new IllegalArgumentException("Filtro no reconocido");
//         }
//     }

//     private String generarIdentificadorCola(Filtro filtro, ProgramacionesAMI programacionAMI) {
//         switch (filtro) {
//             case CONCENTRADOR:
//             case CONCENTRADOR_Y_MEDIDORES:
//                 return "C_" + programacionAMI.getGrupoMedidores().getVcidentificador();

//             case MEDIDORES:
//             case FRONTERA_SIC:
//                 return "M_" + programacionAMI.getNcodigo();

//             default:
//                 throw new IllegalArgumentException("Filtro no reconocido");
//         }
//     }

//     private String manejarTareaMedidores(ProgramacionesAMI programacionAMI, String vcSeriesAReintentarFiltrado_, int reintentosRestantes) throws Exception {
//         List<String> vcSeriesAReintentar = new ArrayList<>();
//         List<CompletableFuture<String>> futuresList = new ArrayList<>();
//         ObjectMapper objectMapper = new ObjectMapper();

//         String jsseriesMed = !"EstadoInicio".equalsIgnoreCase(vcSeriesAReintentarFiltrado_)
//                 ? vcSeriesAReintentarFiltrado_
//                 : programacionAMI.getGrupoMedidores().getJsseriesMed();

//         List<String> jsseriesMedList = objectMapper.readValue(jsseriesMed, new TypeReference<List<String>>() {});

//         for (String vcserie : jsseriesMedList) {
//             Callable<String> tarea = () -> conectorGeneralService.usarConectorProgramacionFiltroMedidores("Lectura", programacionAMI, vcserie, reintentosRestantes);
//             CompletableFuture<String> future = generadorDeColas.encolarSolicitud("M_" + vcserie, tarea);
//             futuresList.add(future);
//         }

//         for (CompletableFuture<String> future : futuresList) {
//             String resultado = future.get();
//             if (!resultado.isEmpty()) {
//                 vcSeriesAReintentar.add(resultado);
//             }
//         }

//         return vcSeriesAReintentar.stream()
//                 .map(serie -> "\"" + serie + "\"")
//                 .collect(Collectors.joining(", ", "[", "]"));
//     }

//     private String manejarTareaFronteraSIC(ProgramacionesAMI programacionAMI, String vcSeriesAReintentarFiltrado_, int reintentosRestantes) throws Exception {
//         List<String> vcSeriesAReintentar = new ArrayList<>();
//         List<CompletableFuture<String>> futuresList = new ArrayList<>();
//         ObjectMapper objectMapper = new ObjectMapper();

//         String jsseriesMed = !"EstadoInicio".equalsIgnoreCase(vcSeriesAReintentarFiltrado_)
//                 ? vcSeriesAReintentarFiltrado_
//                 : obtenerSeriesMedidoresPorSIC(programacionAMI.getGrupoMedidores().getVcidentificador());

//         List<String> jsseriesMedList = objectMapper.readValue(jsseriesMed, new TypeReference<List<String>>() {});

//         for (String vcserie : jsseriesMedList) {
//             Callable<String> tarea = () -> conectorGeneralService.usarConectorProgramacionFiltroSIC("Lectura", programacionAMI, vcserie, reintentosRestantes);
//             CompletableFuture<String> future = generadorDeColas.encolarSolicitud("M_" + vcserie, tarea);
//             futuresList.add(future);
//         }

//         for (CompletableFuture<String> future : futuresList) {
//             String resultado = future.get();
//             if (!resultado.isEmpty()) {
//                 vcSeriesAReintentar.add(resultado);
//             }
//         }

//         return vcSeriesAReintentar.stream()
//                 .map(serie -> "\"" + serie + "\"")
//                 .collect(Collectors.joining(", ", "[", "]"));
//     }

//     private String obtenerSeriesMedidoresPorSIC(String identificadorSIC) {
//         List<Medidores> medidores = medidoresService.findByVcsic(identificadorSIC);
//         return medidores.stream()
//                 .map(Medidores::getVcSerie)
//                 .map(serie -> "\"" + serie + "\"")
//                 .collect(Collectors.joining(", ", "[", "]"));
//     }

//     private void programarTareasFrecuentes(List<DayOfWeek> diasSemana, LocalDateTime tiempoInicio,
//                                            ProgramacionesAMI programacionAMI, Filtro filtro,
//                                            String estadoInicio, int reintentosRestantes) {

//         ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//         LocalDateTime ahora = LocalDateTime.now();

//         for (DayOfWeek diaSemana : diasSemana) {
//             LocalDateTime proximoDia = ahora.with(TemporalAdjusters.nextOrSame(diaSemana))
//                     .withHour(tiempoInicio.getHour())
//                     .withMinute(tiempoInicio.getMinute()).withSecond(0);

//             long delay = Duration.between(ahora, proximoDia).toMillis();

//             scheduler.scheduleAtFixedRate(() -> {
//                 System.out.println("Ejecutando tarea para " + diaSemana + " con filtro: " + filtro.value);
//                 programarTarea(scheduler, programacionAMI, 0, estadoInicio, reintentosRestantes, filtro);
//             }, delay, TimeUnit.DAYS.toMillis(7), TimeUnit.MILLISECONDS);
//         }
//     }

//     private void programarTareasRecurrentes(List<DayOfWeek> diasSemana, Map<String, Map<String, String>> frecuenciaLecturaLote,
//                                             ProgramacionesAMI programacionAMI, Filtro filtro,
//                                             String estadoInicio, int reintentosRestantes) {

//         ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//         LocalDateTime ahora = LocalDateTime.now();

//         for (DayOfWeek diaSemana : diasSemana) {
//             String diaSemanaStr = obtenerDiaSemanaString(diaSemana);
//             if (frecuenciaLecturaLote.containsKey(diaSemanaStr)) {
//                 String horaInicioStr = frecuenciaLecturaLote.get(diaSemanaStr).get("horaInicio");
//                 LocalTime horaInicio = convertirHoraString(horaInicioStr);

//                 LocalDateTime proximoDia = ahora.with(TemporalAdjusters.nextOrSame(diaSemana))
//                         .withHour(horaInicio.getHour())
//                         .withMinute(horaInicio.getMinute()).withSecond(0);

//                 long delay = Duration.between(ahora, proximoDia).toMillis();

//                 scheduler.scheduleAtFixedRate(() -> {
//                     System.out.println("Ejecutando tarea para " + diaSemanaStr + " con filtro: " + filtro.value + " a la hora: " + horaInicio);
//                     programarTarea(scheduler, programacionAMI, 0, estadoInicio, reintentosRestantes, filtro);
//                 }, delay, TimeUnit.DAYS.toMillis(7), TimeUnit.MILLISECONDS);
//             }
//         }
//     }

//     // Métodos auxiliares

//     private List<DayOfWeek> convertirDiasSemana(String jsdiasSemana) {
//         List<String> diasEnTexto = Arrays
//                 .asList(jsdiasSemana.replace("[", "").replace("]", "").replace("\"", "").split(","));

//         return diasEnTexto.stream().map(dia -> {
//             switch (dia.trim().toLowerCase(Locale.ROOT)) {
//                 case "lunes":
//                     return DayOfWeek.MONDAY;
//                 case "martes":
//                     return DayOfWeek.TUESDAY;
//                 case "miércoles":
//                     return DayOfWeek.WEDNESDAY;
//                 case "jueves":
//                     return DayOfWeek.THURSDAY;
//                 case "viernes":
//                     return DayOfWeek.FRIDAY;
//                 case "sabado":
//                     return DayOfWeek.SATURDAY;
//                 case "domingo":
//                     return DayOfWeek.SUNDAY;
//                 default:
//                     throw new IllegalArgumentException("Día no reconocido: " + dia);
//             }
//         }).collect(Collectors.toList());
//     }

//     private Map<String, Map<String, String>> deserializarFrecuenciaLectura(String jsfrecuenciaLecturaLote) {
//         ObjectMapper objectMapper = new ObjectMapper();
//         try {
//             return objectMapper.readValue(jsfrecuenciaLecturaLote,
//                     new TypeReference<Map<String, Map<String, String>>>() {});
//         } catch (Exception e) {
//             e.printStackTrace();
//             return Collections.emptyMap();
//         }
//     }

//     private String obtenerDiaSemanaString(DayOfWeek dayOfWeek) {
//         switch (dayOfWeek) {
//             case MONDAY:
//                 return "Lunes";
//             case TUESDAY:
//                 return "Martes";
//             case WEDNESDAY:
//                 return "Miércoles";
//             case THURSDAY:
//                 return "Jueves";
//             case FRIDAY:
//                 return "Viernes";
//             case SATURDAY:
//                 return "Sabado";
//             case SUNDAY:
//                 return "Domingo";
//             default:
//                 throw new IllegalArgumentException("Día no reconocido");
//         }
//     }

//     private LocalTime convertirHoraString(String horaStr) {
//         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mma", Locale.US);
//         return LocalTime.parse(horaStr.toUpperCase(), formatter);
//     }
// }

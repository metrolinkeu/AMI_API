package com.metrolink.ami_api.services.procesos.programacionesAmi;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
            return manejarFiltro(filtro, programacionAMI);
        } else if ("frecuente no recurrente".equalsIgnoreCase(tipoLectura)) {
            System.out.println("LECTURA FRECUENTE NO RECURRENTE");
            return manejarFiltro(filtro, programacionAMI);
        } else if ("recurrente".equalsIgnoreCase(tipoLectura)) {
            System.out.println("LECTURA RECURRENTE");
            return manejarFiltro(filtro, programacionAMI);
        } else {
            System.out.println("CASO NO DEFINIDO");
        }

        return "CASO NO DEFINIDO";
    }

    // Método para manejar la lógica de filtrado
    private String manejarFiltro(String filtro, ProgramacionesAMI programacionAMI) {

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

    // Método para programar y reintentar la tarea
    private void programarTarea(ScheduledExecutorService scheduler, ProgramacionesAMI programacionAMI, long delay) {
        scheduler.schedule(() -> {
            try {
                // Crear la tarea que se encolará
                Callable<String> PedirPeticionesPorConcentrador = () -> conectorGeneralService
                        .usarConectorProgramacion("Hola Mundo", programacionAMI);

                // Usar GeneradorDeColas para encolar la tarea
                CompletableFuture<String> future = generadorDeColas.encolarSolicitud("C_" +
                        programacionAMI.getGrupoMedidores().getVcidentificador(),
                        PedirPeticionesPorConcentrador);

                // Esperar a que se complete la tarea
                String resultadoTarea = future.get(); // Este método bloquea hasta que la tarea esté completa

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(resultadoTarea);
                // Extraer los valores de "medidoresLeidos" y "medidoresNoLeidos"
                int medidoresLeidos = rootNode.path("medidoresLeidos").asInt();
                int medidoresNoLeidos = rootNode.path("medidoresNoLeidos").asInt();

                // Imprimir los resultados
                System.out.println("Medidores leídos: " + medidoresLeidos);
                System.out.println("Medidores no leídos: " + medidoresNoLeidos);

                // Reintentar la tarea si hay medidores no leídos
                if (medidoresNoLeidos > 0) {
                    System.out.println("Reintentando la tarea en 1 minuto debido a medidores no leídos.");
                    programarTarea(scheduler, programacionAMI, 60000); // Reprograma la tarea en 1 minuto (60000 ms)
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, delay, TimeUnit.MILLISECONDS);
    }
}

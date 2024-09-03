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

    public void manejarProgramacion(ProgramacionesAMI programacionAMI) {

        String tipoLectura = programacionAMI.getParametrizacionProg().getVctipoDeLectura();
        String filtro = programacionAMI.getGrupoMedidores().getVcfiltro();
        boolean isRecurrente = "recurrente".equalsIgnoreCase(tipoLectura);
        boolean isFrecuenteNoRecurrente = "frecuente no recurrente".equalsIgnoreCase(tipoLectura);
        boolean isUnica = "única".equalsIgnoreCase(tipoLectura);

        if (isUnica && "Concentrador".equalsIgnoreCase(filtro)) {
            System.out.println("CASO 1: LECTURA ÚNICA Y FILTRO POR CONCENTRADOR");

            Timestamp tiempoInicio = programacionAMI.getParametrizacionProg().getDfechaHoraInicio();
            System.out.println("Tiempo de inicio programado: " + tiempoInicio);

            Instant ahora = Instant.now();
            long delay = Duration.between(ahora, tiempoInicio.toInstant()).toMillis();

            if (delay > 0) {
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                scheduler.schedule(() -> {
                    try {
                        // Crear la tarea que se encolará
                        Callable<String> tareaHolaMundo = () -> conectorGeneralService
                                .usarConectorProgramacion("Hola Mundo");

                        // Usar GeneradorDeColas para encolar la tarea
                        CompletableFuture<String> future = generadorDeColas.encolarSolicitud(
                                programacionAMI.getGrupoMedidores().getVcidentificador(), tareaHolaMundo);

                        // Esperar a que se complete la tarea
                        String resultado = future.get(); // Este método bloquea hasta que la tarea esté completa
                        System.out.println("Resultado de la tarea: " + resultado);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, delay, TimeUnit.MILLISECONDS);
            } else {
                System.out.println("La fecha y hora ya han pasado, no se puede programar la tarea.");
            }

        } else if (isUnica && "ConcentradorYmedidores".equalsIgnoreCase(filtro)) {
            System.out.println("CASO 2: LECTURA ÚNICA Y FILTRO POR CONCENTRADOR Y MEDIDORES");
        } else if (isUnica && "Medidores".equalsIgnoreCase(filtro)) {
            System.out.println("CASO 3: LECTURA ÚNICA Y FILTRO POR MEDIDORES");
        } else if (isUnica && "Frontera SIC".equalsIgnoreCase(filtro)) {
            System.out.println("CASO 4: LECTURA ÚNICA Y CASO FRONTERA SIC");
        } else if (isFrecuenteNoRecurrente && "Concentrador".equalsIgnoreCase(filtro)) {
            System.out.println("CASO 5: LECTURA FRECUENTE NO RECURRENTE Y FILTRO POR CONCENTRADOR");
        } else if (isFrecuenteNoRecurrente && "ConcentradorYmedidores".equalsIgnoreCase(filtro)) {
            System.out.println("CASO 6: LECTURA FRECUENTE NO RECURRENTE Y FILTRO POR CONCENTRADOR Y MEDIDORES");
        } else if (isFrecuenteNoRecurrente && "Medidores".equalsIgnoreCase(filtro)) {
            System.out.println("CASO 7: LECTURA FRECUENTE NO RECURRENTE Y FILTRO POR MEDIDORES");
        } else if (isFrecuenteNoRecurrente && "Frontera SIC".equalsIgnoreCase(filtro)) {
            System.out.println("CASO 8: LECTURA FRECUENTE NO RECURRENTE Y CASO FRONTERA SIC");
        } else if (isRecurrente && "Concentrador".equalsIgnoreCase(filtro)) {
            System.out.println("CASO 9: LECTURA RECURRENTE Y FILTRO POR CONCENTRADOR");
        } else if (isRecurrente && "ConcentradorYmedidores".equalsIgnoreCase(filtro)) {
            System.out.println("CASO 10: LECTURA RECURRENTE Y FILTRO POR CONCENTRADOR Y MEDIDORES");
        } else if (isRecurrente && "Medidores".equalsIgnoreCase(filtro)) {
            System.out.println("CASO 11: LECTURA RECURRENTE Y FILTRO POR MEDIDORES");
        } else if (isRecurrente && "Frontera SIC".equalsIgnoreCase(filtro)) {
            System.out.println("CASO 12: LECTURA RECURRENTE Y CASO FRONTERA SIC");
        } else {
            System.out.println("CASO NO DEFINIDO");
        }
    }
}

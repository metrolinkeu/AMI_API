package com.metrolink.ami_api.services.procesos.programacionesAmi;

import com.metrolink.ami_api.models.procesos.programacionesAmi.ProgramacionesAMI;

public class ProgramacionHandler {

    public static void manejarProgramacion(ProgramacionesAMI programacionAMI) {
        String tipoLectura = programacionAMI.getParametrizacionProg().getVctipoDeLectura();
        String filtro = programacionAMI.getGrupoMedidores().getVcfiltro();
        boolean isRecurrente = "recurrente".equalsIgnoreCase(tipoLectura);
        boolean isFrecuenteNoRecurrente = "frecuente no recurrente".equalsIgnoreCase(tipoLectura);
        boolean isUnica = "única".equalsIgnoreCase(tipoLectura);

        if (isUnica && "Concentrador".equalsIgnoreCase(filtro)) {
            System.out.println("CASO 1: LECTURA ÚNICA Y FILTRO POR CONCENTRADOR");
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

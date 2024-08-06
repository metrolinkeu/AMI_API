package com.metrolink.ami_api.models.procesos.programacionesAmi;

import lombok.Data;

@Data
public class MedidoresAgenda {

    private String vcSerie;
    private AgendaProgramacionesAMI enAgendaProgramacionesAMI;
    private String estadoEnAgenda;

}

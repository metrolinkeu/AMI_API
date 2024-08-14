package com.metrolink.ami_api.models.primeraLectura;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class AutoconfMedidor {

    private String vcSerie;
    private AutoConfCanalesPerfilCarga autoConfcanalesPerfilCarga;
    private Timestamp dfechaHoraUltimaLectura;
    private String vcdíasdeRegDíariosMensuales;
    private String vcdiasdeEventos;
    private String vcperiodoIntegracion;
    private String vcultimoEstadoRele;
    private String vcfirmware;
}
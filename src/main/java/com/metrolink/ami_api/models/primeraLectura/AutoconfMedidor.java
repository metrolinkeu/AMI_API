package com.metrolink.ami_api.models.primeraLectura;

import lombok.Data;

@Data
public class AutoconfMedidor {

    private String vcSerie;
    private AutoConfCanalesPerfilCarga autoConfcanalesPerfilCarga;
    private String vcfechaHoraUltimaLectura;
    private String vcdíasdeRegDíariosMensuales;
    private String vcdiasdeEventos;
    private String vcperiodoIntegracion;
    private String vcultimoEstadoRele;
    private String vcfirmware;
}
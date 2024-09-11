package com.metrolink.ami_api.services.procesos.conectorGeneral;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import com.metrolink.ami_api.models.primeraLectura.AutoconfMedidor;
import com.metrolink.ami_api.models.procesos.programacionesAmi.ProgramacionesAMI;
import com.metrolink.ami_api.services.procesos.autoconfiguracion.ConectorAutoConfService2;

import com.metrolink.ami_api.services.procesos.deteccionMed.ConectorDetecMedService2;

import com.metrolink.ami_api.services.procesos.programacionesAmi.ConectorProgramacionService;

@Service
public class ConectorGeneralService {

    @Autowired
    private ConectorDetecMedService2 conectorDetecMedService2;

    @Autowired
    private ConectorAutoConfService2 conectorAutoConfService2;

    @Autowired
    private ConectorProgramacionService conectorProgramacionService;

    public String usarConectorDeteccion(String json) {
        String newJson = conectorDetecMedService2.usarConectorDeteccion(json);
        return newJson;
    }

    public List<AutoconfMedidor> UsarConectorAutoConfMed(String vcnoSerie) {
        List<AutoconfMedidor> autoconfMedidores = conectorAutoConfService2.UsarConectorAutoConfMed(vcnoSerie);
        return autoconfMedidores;
    }

    public AutoconfMedidor UsarConectorAutoConfMed2(String vcserie, String vcnoSerie, String vcSIC,
            JsonNode vcserialesNode) {
        AutoconfMedidor autoconfMedidor = conectorAutoConfService2.UsarConectorAutoConfMed_solo(vcserie, vcnoSerie,
                vcSIC, vcserialesNode);
        return autoconfMedidor;
    }

    public String usarConectorProgramacionFiltroConcentrador(String mensaje, ProgramacionesAMI programacionAMI,
            String vcSeriesAReintentarFiltrado) {
        String Impreso = conectorProgramacionService.UsarConectorProgramacionFiltroConcentrador(mensaje,
                programacionAMI,
                vcSeriesAReintentarFiltrado);

        return Impreso; // Retorna el mensaje para confirmar la ejecución
    }

    public String usarConectorProgramacionFiltroConyMed(String mensaje, ProgramacionesAMI programacionAMI,
            String vcSeriesAReintentarFiltrado) {
        String Impreso = conectorProgramacionService.UsarConectorProgramacionFiltroConyMed(mensaje, programacionAMI,
                vcSeriesAReintentarFiltrado);

        return Impreso; // Retorna el mensaje para confirmar la ejecución
    }

    public String usarConectorProgramacionFiltroMedidores(String mensaje, ProgramacionesAMI programacionAMI,
            String vcserie) {
        String Impreso = conectorProgramacionService.UsarConectorProgramacionFiltroMedidores(mensaje, programacionAMI,
                vcserie);

        return Impreso; // Retorna el mensaje para confirmar la ejecución
    }

    public String usarConectorProgramacionFiltroSIC(String mensaje, ProgramacionesAMI programacionAMI, String vcserie) {
        String Impreso = conectorProgramacionService.UsarConectorProgramacionFiltroSIC(mensaje, programacionAMI,
                vcserie);

        return Impreso; // Retorna el mensaje para confirmar la ejecución
    }

}

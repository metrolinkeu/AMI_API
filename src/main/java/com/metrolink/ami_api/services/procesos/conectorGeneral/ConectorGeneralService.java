package com.metrolink.ami_api.services.procesos.conectorGeneral;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import com.metrolink.ami_api.models.primeraLectura.AutoconfMedidor;
import com.metrolink.ami_api.models.procesos.programacionesAmi.ProgramacionesAMI;
import com.metrolink.ami_api.services.procesos.autoconfiguracion.ConectorAutoConfService;

import com.metrolink.ami_api.services.procesos.deteccionMed.ConectorDetecMedService;

import com.metrolink.ami_api.services.procesos.programacionesAmi.ConectorProgramacionService;

@Service
public class ConectorGeneralService {

    @Autowired
    private ConectorDetecMedService conectorDetecMedService;

    @Autowired
    private ConectorAutoConfService conectorAutoConfService;

    @Autowired
    private ConectorProgramacionService conectorProgramacionService;

    public String usarConectorDeteccion(String json) {
        String newJson = conectorDetecMedService.usarConectorDeteccion(json);
        return newJson;
    }

    public List<AutoconfMedidor> UsarConectorAutoConfMed(String vcnoSerie, JsonNode rootNode) {
        List<AutoconfMedidor> autoconfMedidores = conectorAutoConfService.UsarConectorAutoConfMed(vcnoSerie, rootNode);
        return autoconfMedidores;
    }

    public AutoconfMedidor UsarConectorAutoConfMed_solo(String vcserie, String vcnoSerie, String vcSIC,
            JsonNode vcserialesNode, JsonNode rootNode) {
        AutoconfMedidor autoconfMedidor = conectorAutoConfService.UsarConectorAutoConfMed_solo(vcserie, vcnoSerie,
                vcSIC, vcserialesNode, rootNode);
        return autoconfMedidor;
    }

    public String usarConectorProgramacionFiltroConcentrador(String mensaje, ProgramacionesAMI programacionAMI,
            String vcSeriesAReintentarFiltrado, int reintentosRestantes) {
        String medidoresFaltantesPorLeer = conectorProgramacionService.UsarConectorProgramacionFiltroConcentrador(mensaje,
                programacionAMI,
                vcSeriesAReintentarFiltrado, reintentosRestantes);

        return medidoresFaltantesPorLeer; // Retorna el mensaje para confirmar la ejecución
    }

    public String usarConectorProgramacionFiltroConyMed(String mensaje, ProgramacionesAMI programacionAMI,
            String vcSeriesAReintentarFiltrado) {
        String medidoresFaltantesPorLeer = conectorProgramacionService.UsarConectorProgramacionFiltroConyMed(mensaje, programacionAMI,
                vcSeriesAReintentarFiltrado);

        return medidoresFaltantesPorLeer; // Retorna el mensaje para confirmar la ejecución
    }

    public String usarConectorProgramacionFiltroMedidores(String mensaje, ProgramacionesAMI programacionAMI,
            String vcserie) {
        String vcSerieMedAReintentar = conectorProgramacionService.UsarConectorProgramacionFiltroMedidores(mensaje, programacionAMI,
                vcserie);

        return vcSerieMedAReintentar; // Retorna el mensaje para confirmar la ejecución
    }

    public String usarConectorProgramacionFiltroSIC(String mensaje, ProgramacionesAMI programacionAMI, String vcserie) {
        String vcSerieMedAReintentar = conectorProgramacionService.UsarConectorProgramacionFiltroSIC(mensaje, programacionAMI,
                vcserie);

        return vcSerieMedAReintentar; // Retorna el mensaje para confirmar la ejecución
    }

}

package com.metrolink.ami_api.services.procesos.deteccionMed;

import java.io.IOException;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metrolink.ami_api.models.procesos.ejecucionesLecturas.EjecucionesLecturaDetect;
import com.metrolink.ami_api.models.procesos.ejecucionesLecturas.EjecucionesLecturas;
import com.metrolink.ami_api.services.procesos.ejecucionesLecturas.EjecucionesLecturasService;
import com.metrolink.ami_api.services.procesos.ejecucionesLecturas.EjecucionesLectHandlerService;

@Service
public class ConectorDetecMedService {

    @Autowired
    private EjecucionesLecturasService ejecucionesLecturasService;

    @Autowired
    private EjecucionesLectHandlerService ejecucionesLectHandlerService;

    @Transactional
    public String usarConectorDeteccion(String json) {

        String newJson = "";
        // Procesar el JSON como antes
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String vcnoSerie = jsonNode.get("vcnoSerie").asText();
        System.out.println("vcnoSerie: " + vcnoSerie);

        EjecucionesLecturas ejecucionLecturaToSave = new EjecucionesLecturas();
        ejecucionLecturaToSave.setNidEjecucionLectura(0L);
        EjecucionesLecturas ejecucionLecturaSaved = ejecucionesLecturasService.save(ejecucionLecturaToSave, false);
        EjecucionesLecturas ejecucionLectura = ejecucionesLecturasService
                .findById(ejecucionLecturaSaved.getNidEjecucionLectura());

        ejecucionLectura.setNintentoLecturaNumero(1);
        ejecucionLectura.setNidAnteriorIntentoEjecucionLectura((long) 0);
        ejecucionLectura.setDinicioEjecucionLectura(new Timestamp(System.currentTimeMillis()));

        EjecucionesLecturaDetect ejecucionLecturaDetect = new EjecucionesLecturaDetect();
        ejecucionLecturaDetect.setVcdescripcionDetect("Deteccion de tabla de medidores del concentrador: " + vcnoSerie);
        ejecucionLecturaDetect.setVcnoserie(vcnoSerie);
        ejecucionLectura.setEjecucionLecturaDetect(ejecucionLecturaDetect);

        //// <----------------------------
        Object resultado = ejecucionesLectHandlerService.EnviarAEjecucionesLectHandler(ejecucionLectura);

        // Si esperas un String (en el caso de medidores faltantes por leer)
        if (resultado instanceof String) {
            newJson = (String) resultado;
            System.out.println(newJson);
        }
        //// <----------------------------

        if (!newJson.equals("{}")) {
            ejecucionLecturaDetect.setJsTablaMedidoresDetec(newJson);
            ejecucionLecturaDetect.setLdeteccionOK(true);

        } else {
            ejecucionLecturaDetect.setLdeteccionOK(false);
        }

        ejecucionLectura.setEjecucionLecturaDetect(ejecucionLecturaDetect);

        ejecucionLectura.setDfinEjecucionLectura(new Timestamp(System.currentTimeMillis()));

        return newJson;

    }

}

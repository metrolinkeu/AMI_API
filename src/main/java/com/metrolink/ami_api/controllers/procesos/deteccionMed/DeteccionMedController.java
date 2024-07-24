package com.metrolink.ami_api.controllers.procesos.deteccionMed;

import com.metrolink.ami_api.services.procesos.deteccionMed.DeteccionMedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/deteccionMed")
public class DeteccionMedController {

    @Autowired
    private DeteccionMedService deteccionMedService;

    @PostMapping("/concentrador")
    public ResponseEntity<String> recibirConcentrador(HttpServletRequest request) {
        try {
            // Leer el cuerpo de la solicitud como texto
            String json = request.getReader().lines()
                            .reduce("", (accumulator, actual) -> accumulator + actual);

            // Utilizar el servicio para procesar el JSON y obtener el valor de "vcnoSerie"
            String vcnoSerie = deteccionMedService.procesarConcentrador(json);

            // Responder con un mensaje de éxito
            return ResponseEntity.ok(json);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al procesar el JSON");
        }
    }
}

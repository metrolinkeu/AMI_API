package com.metrolink.ami_api.controllers.procesos.deteccionMed;

import com.metrolink.ami_api.models.tablasFront.Empresas;
import com.metrolink.ami_api.services.procesos.deteccionMed.DeteccionMedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/primeraLectura")
public class DeteccionMedController {

    @Autowired
    private DeteccionMedService deteccionMedService;

    @PostMapping("/detectMed")
    public ResponseEntity<Empresas> recibirConcentrador(HttpServletRequest request) {
        try {
            // Leer el cuerpo de la solicitud como texto
            String json = request.getReader().lines()
                    .reduce("", (accumulator, actual) -> accumulator + actual);

            // Procesar el JSON utilizando el servicio
            Empresas MedidoresGuardados = deteccionMedService.procesarDeteccionByCon(json);

            // Responder con el mismo JSON recibido
            return ResponseEntity.ok(MedidoresGuardados);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }
}

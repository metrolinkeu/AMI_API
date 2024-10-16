package com.metrolink.ami_api.controllers.procesos.deteccionMed;

import com.metrolink.ami_api.models.medidor.Medidores;
import com.metrolink.ami_api.services.procesos.deteccionMed.DeteccionMedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/primeraLectura")
public class DeteccionMedController {
 
    @Autowired
    private DeteccionMedService deteccionMedService;

    @PostMapping("/detectMed")
    public ResponseEntity<List<Medidores>> recibirConcentrador(HttpServletRequest request) throws ExecutionException, InterruptedException {
        try {
            // Leer el cuerpo de la solicitud como texto
            String json = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
            // Procesar el JSON utilizando el servicio
            List<Medidores> MedidoresGuardados = deteccionMedService.procesarDeteccionByCon(json);
            return ResponseEntity.ok(MedidoresGuardados);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }
}

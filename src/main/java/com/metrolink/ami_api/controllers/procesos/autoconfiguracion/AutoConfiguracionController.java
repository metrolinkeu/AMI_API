package com.metrolink.ami_api.controllers.procesos.autoconfiguracion;


import com.metrolink.ami_api.models.primeraLectura.AutoconfMedidor;
import com.metrolink.ami_api.services.procesos.autoconfiguracion.AutoConfiguracionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/autoconfiguracion")
public class AutoConfiguracionController {

    @Autowired
    private AutoConfiguracionService autoConfiguracionService;

    @GetMapping("/ObtenerConfig")
    public ResponseEntity<List<AutoconfMedidor>> recibirConfiguracion(HttpServletRequest request) {
        try {
            // Leer el cuerpo de la solicitud como texto
            String json = request.getReader().lines()
                    .reduce("", (accumulator, actual) -> accumulator + actual);
            // Procesar el JSON utilizando el servicio
            List<AutoconfMedidor> medidoresConfigurados = autoConfiguracionService.procesarConfiguracion(json);
            return ResponseEntity.ok(medidoresConfigurados);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }
}

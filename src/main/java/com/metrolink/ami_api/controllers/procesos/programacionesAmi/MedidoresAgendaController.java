package com.metrolink.ami_api.controllers.procesos.programacionesAmi;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

import com.metrolink.ami_api.models.procesos.programacionesAmi.MedidoresAgenda;
import com.metrolink.ami_api.services.procesos.programacionesAmi.MedidoresAgendaService;


@RestController
@RequestMapping("/api/medidoresAgenda")
public class MedidoresAgendaController {

    @Autowired
    private MedidoresAgendaService medidoresAgendaService;

    @PostMapping("/mostrarAgenda")
    public ResponseEntity<List<MedidoresAgenda>> recibirMedidores(HttpServletRequest request) {
        try {
            // Leer el cuerpo de la solicitud como texto
            String json = request.getReader().lines()
                    .reduce("", (accumulator, actual) -> accumulator + actual);
            // Procesar el JSON utilizando el servicio
            List<MedidoresAgenda> medidoresAgendaTraidos = medidoresAgendaService.procesarMedidoresAgenda(json);
            return ResponseEntity.ok(medidoresAgendaTraidos);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

}

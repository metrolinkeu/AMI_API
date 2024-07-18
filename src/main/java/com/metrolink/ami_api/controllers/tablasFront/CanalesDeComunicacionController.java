package com.metrolink.ami_api.controllers.tablasFront;

import com.metrolink.ami_api.models.tablasFront.CanalesDeComunicacion;
import com.metrolink.ami_api.services.tablasFront.CanalesDeComunicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api/canalesDeComunicacion")
public class CanalesDeComunicacionController {

    @Autowired
    private CanalesDeComunicacionService canalesDeComunicacionService;

    @PostMapping
    public ResponseEntity<CanalesDeComunicacion> createCanalDeComunicacion(@RequestBody CanalesDeComunicacion canal) {

        CanalesDeComunicacion savedCanal = canalesDeComunicacionService.save(canal, false);
        return new ResponseEntity<>(savedCanal, HttpStatus.CREATED);

    }

  

    @GetMapping
    public ResponseEntity<List<CanalesDeComunicacion>> getAllCanalesDeComunicacion() {
        List<CanalesDeComunicacion> canales = canalesDeComunicacionService.findAll();
        return ResponseEntity.ok(canales);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CanalesDeComunicacion> getCanalDeComunicacionById(@PathVariable Long id) {
        CanalesDeComunicacion canal = canalesDeComunicacionService.findById(id);
        return ResponseEntity.ok(canal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CanalesDeComunicacion> updateCanalDeComunicacion(@PathVariable Long id,
            @RequestBody CanalesDeComunicacion canalDetails) {
        CanalesDeComunicacion updatedCanal = canalesDeComunicacionService.update(id, canalDetails);
        return ResponseEntity.ok(updatedCanal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCanalDeComunicacion(@PathVariable Long id) {
        canalesDeComunicacionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

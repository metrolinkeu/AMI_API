package com.metrolink.ami_api.controllers.medidor;

import com.metrolink.ami_api.models.medidor.Medidores;
import com.metrolink.ami_api.services.medidor.MedidoresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/medidores")
public class MedidoresController {

    @Autowired
    private MedidoresService medidoresService;

    @PostMapping
    public ResponseEntity<Medidores> createMedidor(@RequestBody Medidores medidor) {
        try{
        Medidores createdMedidor = medidoresService.save(medidor, false);
        return new ResponseEntity<>(createdMedidor, HttpStatus.CREATED);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<Medidores>> getAllMedidores() {
        List<Medidores> medidores = medidoresService.findAll();
        return ResponseEntity.ok(medidores);
    }

    @GetMapping("/{vcSerie}")
    public ResponseEntity<Medidores> getMedidorById(@PathVariable String vcSerie) {
        Medidores medidor = medidoresService.findById(vcSerie);
        return ResponseEntity.ok(medidor);
    }

    @PutMapping("/{vcSerie}")
    public ResponseEntity<Medidores> updateMedidor(@PathVariable String vcSerie, @RequestBody Medidores medidorDetails) {
        Medidores updatedMedidor = medidoresService.update(vcSerie, medidorDetails);
        return ResponseEntity.ok(updatedMedidor);
    }

    @DeleteMapping("/{vcSerie}")
    public ResponseEntity<Void> deleteMedidor(@PathVariable String vcSerie) {
        medidoresService.deleteById(vcSerie);
        return ResponseEntity.noContent().build();
    }
}

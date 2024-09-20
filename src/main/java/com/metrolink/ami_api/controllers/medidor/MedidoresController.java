package com.metrolink.ami_api.controllers.medidor;

import com.metrolink.ami_api.models.medidor.Medidores;
import com.metrolink.ami_api.services.medidor.MedidoresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medidores")
public class MedidoresController {

    @Autowired
    private MedidoresService medidoresService;

    @PostMapping
    public ResponseEntity<Medidores> createMedidor(@RequestBody Medidores medidor) {
        try {
            Medidores createdMedidor = medidoresService.save(medidor, false);
            return new ResponseEntity<>(createdMedidor, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
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

    @GetMapping("/concentrador/{vcnoSerie}")
    public ResponseEntity<List<Medidores>> getMedidoresByConcentradorVcnoSerie(@PathVariable String vcnoSerie) {
        List<Medidores> medidores = medidoresService.findByConcentradorVcnoSerie(vcnoSerie);
        return ResponseEntity.ok(medidores);
    }

    @GetMapping("/vcsic/{vcsic}")
    public ResponseEntity<List<Medidores>> getMedidoresByVcsic(@PathVariable String vcsic) {
        List<Medidores> medidores = medidoresService.findByVcsic(vcsic);
        return ResponseEntity.ok(medidores);
    }

    @PatchMapping("/{vcSerie}")
    public ResponseEntity<Medidores> updatePartialMedidor(@PathVariable String vcSerie,
            @RequestBody Map<String, Object> updates) {
        try {
            Medidores updatedMedidor = medidoresService.updatePartial(vcSerie, updates);
            return ResponseEntity.ok(updatedMedidor);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }






    
    @DeleteMapping("/{vcSerie}")
    public ResponseEntity<Void> deleteMedidor(@PathVariable String vcSerie) {
        medidoresService.deleteById(vcSerie);
        return ResponseEntity.noContent().build();
    }
}

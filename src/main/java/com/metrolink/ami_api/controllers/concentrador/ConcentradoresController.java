package com.metrolink.ami_api.controllers.concentrador;

import com.metrolink.ami_api.models.concentrador.Concentradores;
import com.metrolink.ami_api.services.concentrador.ConcentradoresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;



@RestController
@RequestMapping("/api/concentradores")
public class ConcentradoresController {

    @Autowired
    private ConcentradoresService concentradoresService;

    @PostMapping
    public ResponseEntity<Concentradores> createConcentrador(@RequestBody Concentradores concentrador) {
  
        try {
            Concentradores createdConcentrador = concentradoresService.save(concentrador, false);
            return new ResponseEntity<>(createdConcentrador, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    

    @GetMapping
    public ResponseEntity<List<Concentradores>> getAllConcentradores() {
        List<Concentradores> concentradores = concentradoresService.findAll();
        return ResponseEntity.ok(concentradores);
    }

    @GetMapping("/{vcnoSerie}")
    public ResponseEntity<Concentradores> getConcentradorById(@PathVariable String vcnoSerie) {
        Concentradores concentrador = concentradoresService.findById(vcnoSerie);
        return ResponseEntity.ok(concentrador);
    }

    @PutMapping("/{vcnoSerie}")
    public ResponseEntity<Concentradores> updateConcentrador(@PathVariable String vcnoSerie,
            @RequestBody Concentradores concentradorDetails) {
        Concentradores updatedConcentrador = concentradoresService.update(vcnoSerie, concentradorDetails);
        return ResponseEntity.ok(updatedConcentrador);
    }

    @DeleteMapping("/{vcnoSerie}")
    public ResponseEntity<Void> deleteConcentrador(@PathVariable String vcnoSerie) {
        concentradoresService.deleteById(vcnoSerie);
        return ResponseEntity.noContent().build();
    }
}

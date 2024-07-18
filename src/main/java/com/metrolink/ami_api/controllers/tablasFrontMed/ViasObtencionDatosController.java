package com.metrolink.ami_api.controllers.tablasFrontMed;

import com.metrolink.ami_api.models.tablasFrontMed.ViasObtencionDatos;
import com.metrolink.ami_api.services.tablasFrontMed.ViasObtencionDatosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api/viasObtencionDatos")
public class ViasObtencionDatosController {

    @Autowired
    private ViasObtencionDatosService viasObtencionDatosService;

    @PostMapping
    public ResponseEntity<ViasObtencionDatos> createViaObtencionDatos(@RequestBody ViasObtencionDatos viaObtencionDatos) {
        ViasObtencionDatos createdViaObtencionDatos = viasObtencionDatosService.save(viaObtencionDatos, false);
        return new ResponseEntity<>(createdViaObtencionDatos, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ViasObtencionDatos>> getAllViasObtencionDatos() {
        List<ViasObtencionDatos> viasObtencionDatos = viasObtencionDatosService.findAll();
        return ResponseEntity.ok(viasObtencionDatos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ViasObtencionDatos> getViaObtencionDatosById(@PathVariable Long id) {
        ViasObtencionDatos viaObtencionDatos = viasObtencionDatosService.findById(id);
        return ResponseEntity.ok(viaObtencionDatos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ViasObtencionDatos> updateViaObtencionDatos(@PathVariable Long id, @RequestBody ViasObtencionDatos viaObtencionDatosDetails) {
        ViasObtencionDatos updatedViaObtencionDatos = viasObtencionDatosService.update(id, viaObtencionDatosDetails);
        return ResponseEntity.ok(updatedViaObtencionDatos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteViaObtencionDatos(@PathVariable Long id) {
        viasObtencionDatosService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

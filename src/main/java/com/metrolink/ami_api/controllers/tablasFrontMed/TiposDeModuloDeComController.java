package com.metrolink.ami_api.controllers.tablasFrontMed;

import com.metrolink.ami_api.models.tablasFrontMed.TiposDeModuloDeCom;
import com.metrolink.ami_api.services.tablasFrontMed.TiposDeModuloDeComService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api/tiposDeModuloDeCom")
public class TiposDeModuloDeComController {

    @Autowired
    private TiposDeModuloDeComService tiposDeModuloDeComService;

    @PostMapping
    public ResponseEntity<TiposDeModuloDeCom> createTiposDeModuloDeCom(@RequestBody TiposDeModuloDeCom tiposDeModuloDeCom) {
        TiposDeModuloDeCom createdTiposDeModuloDeCom = tiposDeModuloDeComService.save(tiposDeModuloDeCom, false);
        return new ResponseEntity<>(createdTiposDeModuloDeCom, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TiposDeModuloDeCom>> getAllTiposDeModuloDeCom() {
        List<TiposDeModuloDeCom> tiposDeModuloDeCom = tiposDeModuloDeComService.findAll();
        return ResponseEntity.ok(tiposDeModuloDeCom);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TiposDeModuloDeCom> getTiposDeModuloDeComById(@PathVariable Long id) {
        TiposDeModuloDeCom tiposDeModuloDeCom = tiposDeModuloDeComService.findById(id);
        return ResponseEntity.ok(tiposDeModuloDeCom);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TiposDeModuloDeCom> updateTiposDeModuloDeCom(@PathVariable Long id, @RequestBody TiposDeModuloDeCom tiposDeModuloDeComDetails) {
        TiposDeModuloDeCom updatedTiposDeModuloDeCom = tiposDeModuloDeComService.update(id, tiposDeModuloDeComDetails);
        return ResponseEntity.ok(updatedTiposDeModuloDeCom);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTiposDeModuloDeCom(@PathVariable Long id) {
        tiposDeModuloDeComService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

package com.metrolink.ami_api.controllers.bl;

import com.metrolink.ami_api.models.bl.BlMovConfigActivos;
import com.metrolink.ami_api.services.bl.BlMovConfigActivosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/configActivos")
public class BlMovConfigActivosController {

    @Autowired
    private BlMovConfigActivosService configActivosService;

    @PostMapping
    public ResponseEntity<BlMovConfigActivos> createConfigActivo(@RequestBody BlMovConfigActivos configActivo) {
        BlMovConfigActivos createdConfigActivo = configActivosService.save(configActivo, false);
        return new ResponseEntity<>(createdConfigActivo, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BlMovConfigActivos>> getAllConfigActivos() {
        List<BlMovConfigActivos> configActivos = configActivosService.findAll();
        return ResponseEntity.ok(configActivos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlMovConfigActivos> getConfigActivoById(@PathVariable Integer id) {
        BlMovConfigActivos configActivo = configActivosService.findById(id);
        return ResponseEntity.ok(configActivo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlMovConfigActivos> updateConfigActivo(@PathVariable Integer id, @RequestBody BlMovConfigActivos configActivoDetails) {
        BlMovConfigActivos updatedConfigActivo = configActivosService.update(id, configActivoDetails);
        return ResponseEntity.ok(updatedConfigActivo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConfigActivo(@PathVariable Integer id) {
        configActivosService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

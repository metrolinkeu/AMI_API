package com.metrolink.ami_api.controllers.procesos.ejecucionesLecturas;

import com.metrolink.ami_api.models.procesos.ejecucionesLecturas.EjecucionesLecturas;
import com.metrolink.ami_api.services.procesos.ejecucionesLecturas.EjecucionesLecturasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ejecuciones-lecturas")
public class EjecucionesLecturasController {

    @Autowired
    private EjecucionesLecturasService ejecucionesLecturasService;

    @PostMapping
    public ResponseEntity<EjecucionesLecturas> createEjecucion(@RequestBody EjecucionesLecturas ejecucion) {
        EjecucionesLecturas createdEjecucion = ejecucionesLecturasService.save(ejecucion, false);
        return new ResponseEntity<>(createdEjecucion, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EjecucionesLecturas>> getAllEjecuciones() {
        List<EjecucionesLecturas> ejecuciones = ejecucionesLecturasService.findAll();
        return ResponseEntity.ok(ejecuciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EjecucionesLecturas> getEjecucionById(@PathVariable Long id) {
        EjecucionesLecturas ejecucion = ejecucionesLecturasService.findById(id);
        return ResponseEntity.ok(ejecucion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EjecucionesLecturas> updateEjecucion(@PathVariable Long id, @RequestBody EjecucionesLecturas ejecucionDetails) {
        EjecucionesLecturas updatedEjecucion = ejecucionesLecturasService.update(id, ejecucionDetails);
        return ResponseEntity.ok(updatedEjecucion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEjecucion(@PathVariable Long id) {
        ejecucionesLecturasService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
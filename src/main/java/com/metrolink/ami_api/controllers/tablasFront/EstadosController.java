package com.metrolink.ami_api.controllers.tablasFront;


import com.metrolink.ami_api.models.tablasFront.Estados;
import com.metrolink.ami_api.services.tablasFront.EstadosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api/estados")
public class EstadosController {

    @Autowired
    private EstadosService estadosService;

    @PostMapping
    public ResponseEntity<Estados> createEstado(@RequestBody Estados estado) {
        Estados createdEstado = estadosService.save(estado, false);
        return new ResponseEntity<>(createdEstado, HttpStatus.CREATED);
    }



    @GetMapping
    public ResponseEntity<List<Estados>> getAllEstados() {
        List<Estados> estados = estadosService.findAll();
        return ResponseEntity.ok(estados);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Estados> getEstadoById(@PathVariable Long id) {
        Estados estado = estadosService.findById(id);
        return ResponseEntity.ok(estado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Estados> updateEstado(@PathVariable Long id, @RequestBody Estados estadoDetails) {
        Estados updatedEstado = estadosService.update(id, estadoDetails);
        return ResponseEntity.ok(updatedEstado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEstado(@PathVariable Long id) {
        estadosService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

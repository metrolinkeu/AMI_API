package com.metrolink.ami_api.controllers.tablasFront;

import com.metrolink.ami_api.models.tablasFront.Marcas;
import com.metrolink.ami_api.services.tablasFront.MarcasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/marcas")
public class MarcasController {

    @Autowired
    private MarcasService marcasService;

    @PostMapping
    public ResponseEntity<Marcas> createMarca(@RequestBody Marcas marca) {
        Marcas createdMarca = marcasService.save(marca, false);
        return new ResponseEntity<>(createdMarca, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Marcas>> getAllMarcas() {
        List<Marcas> marcas = marcasService.findAll();
        return ResponseEntity.ok(marcas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Marcas> getMarcaById(@PathVariable Long id) {
        Marcas marca = marcasService.findById(id);
        return ResponseEntity.ok(marca);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Marcas> updateMarca(@PathVariable Long id, @RequestBody Marcas marcaDetails) {
        Marcas updatedMarca = marcasService.update(id, marcaDetails);
        return ResponseEntity.ok(updatedMarca);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMarca(@PathVariable Long id) {
        marcasService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

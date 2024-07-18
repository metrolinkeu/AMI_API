package com.metrolink.ami_api.controllers.tablasFrontMed;

import com.metrolink.ami_api.models.tablasFrontMed.MarcasMed;
import com.metrolink.ami_api.services.tablasFrontMed.MarcasMedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api/marcasMed")
public class MarcasMedController {

    @Autowired
    private MarcasMedService marcasMedService;

    @PostMapping
    public ResponseEntity<MarcasMed> createMarcaMed(@RequestBody MarcasMed marcaMed) {
        MarcasMed createdMarcaMed = marcasMedService.save(marcaMed, false);
        return new ResponseEntity<>(createdMarcaMed, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MarcasMed>> getAllMarcasMed() {
        List<MarcasMed> marcasMed = marcasMedService.findAll();
        return ResponseEntity.ok(marcasMed);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarcasMed> getMarcaMedById(@PathVariable Long id) {
        MarcasMed marcaMed = marcasMedService.findById(id);
        return ResponseEntity.ok(marcaMed);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MarcasMed> updateMarcaMed(@PathVariable Long id, @RequestBody MarcasMed marcaMedDetails) {
        MarcasMed updatedMarcaMed = marcasMedService.update(id, marcaMedDetails);
        return ResponseEntity.ok(updatedMarcaMed);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMarcaMed(@PathVariable Long id) {
        marcasMedService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

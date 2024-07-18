package com.metrolink.ami_api.controllers.tablasFront;

import com.metrolink.ami_api.models.tablasFront.TiposDeComunicacion;
import com.metrolink.ami_api.services.tablasFront.TiposDeComunicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api/tiposDeComunicacion")
public class TiposDeComunicacionController {

    @Autowired
    private TiposDeComunicacionService tiposDeComunicacionService;

    @PostMapping
    public ResponseEntity<TiposDeComunicacion> createTipoDeComunicacion(@RequestBody TiposDeComunicacion tipo) {

        TiposDeComunicacion createdTipo = tiposDeComunicacionService.save(tipo, false);
        return new ResponseEntity<>(createdTipo, HttpStatus.CREATED);

    }

    @GetMapping
    public ResponseEntity<List<TiposDeComunicacion>> getAllTiposDeComunicacion() {
        List<TiposDeComunicacion> tipos = tiposDeComunicacionService.findAll();
        return ResponseEntity.ok(tipos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TiposDeComunicacion> getTipoDeComunicacionById(@PathVariable Long id) {
        TiposDeComunicacion tipo = tiposDeComunicacionService.findById(id);
        return ResponseEntity.ok(tipo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TiposDeComunicacion> updateTipoDeComunicacion(@PathVariable Long id,
            @RequestBody TiposDeComunicacion tipoDetails) {
        TiposDeComunicacion updatedTipo = tiposDeComunicacionService.update(id, tipoDetails);
        return ResponseEntity.ok(updatedTipo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTipoDeComunicacion(@PathVariable Long id) {
        tiposDeComunicacionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

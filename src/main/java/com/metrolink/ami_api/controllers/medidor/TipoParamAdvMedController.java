package com.metrolink.ami_api.controllers.medidor;

import com.metrolink.ami_api.models.medidor.TipoParamAdvMed;
import com.metrolink.ami_api.services.medidor.TipoParamAdvMedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipoParamAdvMed")
public class TipoParamAdvMedController {

    @Autowired
    private TipoParamAdvMedService tipoParamAdvMedService;

    @GetMapping
    public ResponseEntity<List<TipoParamAdvMed>> getAllTipoParamAdvMed() {
        List<TipoParamAdvMed> tipoParamAdvMedList = tipoParamAdvMedService.findAll();
        return ResponseEntity.ok(tipoParamAdvMedList);
    }

    @GetMapping("/{ncod}")
    public ResponseEntity<TipoParamAdvMed> getTipoParamAdvMedById(@PathVariable Long ncod) {
        TipoParamAdvMed tipoParamAdvMed = tipoParamAdvMedService.findById(ncod);
        return ResponseEntity.ok(tipoParamAdvMed);
    }

    
}

package com.metrolink.ami_api.controllers.concentrador;

import com.metrolink.ami_api.models.concentrador.TipoParamAdvCon;
import com.metrolink.ami_api.services.concentrador.TipoParamAdvConService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipoParamAdvCon")
public class TipoParamAdvConController {

    @Autowired
    private TipoParamAdvConService tipoParamAdvConService;

    @GetMapping
    public ResponseEntity<List<TipoParamAdvCon>> getAllTipoParamAdvCon() {
        List<TipoParamAdvCon> tipoParamAdvCons = tipoParamAdvConService.findAll();
        return ResponseEntity.ok(tipoParamAdvCons);
    }

    @GetMapping("/{ncod}")
    public ResponseEntity<TipoParamAdvCon> getTipoParamAdvConById(@PathVariable Long ncod) {
        TipoParamAdvCon tipoParamAdvCon = tipoParamAdvConService.findById(ncod);
        return ResponseEntity.ok(tipoParamAdvCon);
    }
}

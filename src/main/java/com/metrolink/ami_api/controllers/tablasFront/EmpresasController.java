package com.metrolink.ami_api.controllers.tablasFront;

import com.metrolink.ami_api.models.tablasFront.Empresas;
import com.metrolink.ami_api.services.tablasFront.EmpresasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empresas")
public class EmpresasController {

    @Autowired
    private EmpresasService empresasService;

    @PostMapping
    public ResponseEntity<Empresas> createEmpresa(@RequestBody Empresas empresa) {
        Empresas savedEmpresa = empresasService.save(empresa, false);
        return new ResponseEntity<>(savedEmpresa, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Empresas>> getAllEmpresas() {
        List<Empresas> empresas = empresasService.findAll();
        return ResponseEntity.ok(empresas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empresas> getEmpresaById(@PathVariable Long id) {
        Empresas empresa = empresasService.findById(id);
        return ResponseEntity.ok(empresa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Empresas> updateEmpresa(@PathVariable Long id, @RequestBody Empresas empresaDetails) {
        Empresas updatedEmpresa = empresasService.update(id, empresaDetails);
        return ResponseEntity.ok(updatedEmpresa);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmpresa(@PathVariable Long id) {
        empresasService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

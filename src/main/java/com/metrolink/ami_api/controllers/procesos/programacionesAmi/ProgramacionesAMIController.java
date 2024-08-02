package com.metrolink.ami_api.controllers.procesos.programacionesAmi;

import com.metrolink.ami_api.models.procesos.programacionesAmi.ProgramacionesAMI;
import com.metrolink.ami_api.services.procesos.programacionesAmi.ProgramacionesAMIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api/programacionesAmi")
public class ProgramacionesAMIController {

    @Autowired
    private ProgramacionesAMIService programacionesAMIService;

    @PostMapping
    public ResponseEntity<ProgramacionesAMI> createProgramacionAMI(@RequestBody ProgramacionesAMI programacionAMI) {
        ProgramacionesAMI createdProgramacionAMI = programacionesAMIService.save(programacionAMI, false);
        return new ResponseEntity<>(createdProgramacionAMI, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProgramacionesAMI>> getAllProgramacionesAMI() {
        List<ProgramacionesAMI> programacionesAMI = programacionesAMIService.findAll();
        return ResponseEntity.ok(programacionesAMI);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProgramacionesAMI> getProgramacionAMIById(@PathVariable Long id) {
        ProgramacionesAMI programacionAMI = programacionesAMIService.findById(id);
        return ResponseEntity.ok(programacionAMI);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProgramacionesAMI> updateProgramacionAMI(@PathVariable Long id, @RequestBody ProgramacionesAMI programacionAMIDetails) {
        ProgramacionesAMI updatedProgramacionAMI = programacionesAMIService.update(id, programacionAMIDetails);
        return ResponseEntity.ok(updatedProgramacionAMI);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProgramacionAMI(@PathVariable Long id) {
        programacionesAMIService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

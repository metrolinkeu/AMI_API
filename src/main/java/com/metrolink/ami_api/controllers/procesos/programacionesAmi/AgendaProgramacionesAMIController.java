package com.metrolink.ami_api.controllers.procesos.programacionesAmi;

import com.metrolink.ami_api.models.procesos.programacionesAmi.AgendaProgramacionesAMI;
import com.metrolink.ami_api.services.procesos.programacionesAmi.AgendaProgramacionesAMIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agendaProgramacionesAMI")
public class AgendaProgramacionesAMIController {

    @Autowired
    private AgendaProgramacionesAMIService agendaProgramacionesAMIService;

    @PostMapping
    public ResponseEntity<AgendaProgramacionesAMI> createAgendaProgramacionesAMI(@RequestBody AgendaProgramacionesAMI agendaProgramacionesAMI) {
        AgendaProgramacionesAMI createdAgendaProgramacionesAMI = agendaProgramacionesAMIService.save(agendaProgramacionesAMI, false);
        return new ResponseEntity<>(createdAgendaProgramacionesAMI, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AgendaProgramacionesAMI>> getAllAgendaProgramacionesAMI() {
        List<AgendaProgramacionesAMI> agendaProgramacionesAMI = agendaProgramacionesAMIService.findAll();
        return ResponseEntity.ok(agendaProgramacionesAMI);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendaProgramacionesAMI> getAgendaProgramacionesAMIById(@PathVariable Long id) {
        AgendaProgramacionesAMI agendaProgramacionesAMI = agendaProgramacionesAMIService.findById(id);
        return ResponseEntity.ok(agendaProgramacionesAMI);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgendaProgramacionesAMI> updateAgendaProgramacionesAMI(@PathVariable Long id, @RequestBody AgendaProgramacionesAMI agendaProgramacionesAMIDetails) {
        AgendaProgramacionesAMI updatedAgendaProgramacionesAMI = agendaProgramacionesAMIService.update(id, agendaProgramacionesAMIDetails);
        return ResponseEntity.ok(updatedAgendaProgramacionesAMI);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgendaProgramacionesAMI(@PathVariable Long id) {
        agendaProgramacionesAMIService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

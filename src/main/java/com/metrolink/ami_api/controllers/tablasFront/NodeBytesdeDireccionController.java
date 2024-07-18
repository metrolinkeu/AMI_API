package com.metrolink.ami_api.controllers.tablasFront;

import com.metrolink.ami_api.models.tablasFront.NodeBytesdeDireccion;
import com.metrolink.ami_api.services.tablasFront.NodeBytesdeDireccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api/nodebytesdedireccion")
public class NodeBytesdeDireccionController {

    @Autowired
    private NodeBytesdeDireccionService nodeBytesdeDireccionService;

    @PostMapping
    public ResponseEntity<NodeBytesdeDireccion> createNodeBytesdeDireccion(@RequestBody NodeBytesdeDireccion nodeBytesdeDireccion) {
        NodeBytesdeDireccion createdNodeBytesdeDireccion = nodeBytesdeDireccionService.save(nodeBytesdeDireccion, false);
        return new ResponseEntity<>(createdNodeBytesdeDireccion, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<NodeBytesdeDireccion>> getAllNodeBytesdeDireccion() {
        List<NodeBytesdeDireccion> nodeBytesdeDireccionList = nodeBytesdeDireccionService.findAll();
        return ResponseEntity.ok(nodeBytesdeDireccionList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NodeBytesdeDireccion> getNodeBytesdeDireccionById(@PathVariable Long id) {
        NodeBytesdeDireccion nodeBytesdeDireccion = nodeBytesdeDireccionService.findById(id);
        return ResponseEntity.ok(nodeBytesdeDireccion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NodeBytesdeDireccion> updateNodeBytesdeDireccion(@PathVariable Long id, @RequestBody NodeBytesdeDireccion nodeBytesdeDireccionDetails) {
        NodeBytesdeDireccion updatedNodeBytesdeDireccion = nodeBytesdeDireccionService.update(id, nodeBytesdeDireccionDetails);
        return ResponseEntity.ok(updatedNodeBytesdeDireccion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNodeBytesdeDireccion(@PathVariable Long id) {
        nodeBytesdeDireccionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

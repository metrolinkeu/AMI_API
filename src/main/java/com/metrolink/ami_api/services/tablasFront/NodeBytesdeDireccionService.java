package com.metrolink.ami_api.services.tablasFront;

import com.metrolink.ami_api.models.tablasFront.NodeBytesdeDireccion;
import com.metrolink.ami_api.repositories.tablasFront.NodeBytesdeDireccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
public class NodeBytesdeDireccionService {

    @Autowired
    private NodeBytesdeDireccionRepository nodeBytesdeDireccionRepository;

    @Transactional
    public NodeBytesdeDireccion save(NodeBytesdeDireccion nodeBytesdeDireccion, boolean isUpdate) {
        Optional<NodeBytesdeDireccion> existingNodeBytesdeDireccion = nodeBytesdeDireccionRepository.findById(nodeBytesdeDireccion.getNcodigo());
        if (existingNodeBytesdeDireccion.isPresent() && !isUpdate) {
            throw new IllegalArgumentException("NodeBytesdeDireccion with ncodigo " + nodeBytesdeDireccion.getNcodigo() + " already exists.");
        }
        return nodeBytesdeDireccionRepository.save(nodeBytesdeDireccion);
    }

    public List<NodeBytesdeDireccion> findAll() {
        return nodeBytesdeDireccionRepository.findAll();
    }

    public NodeBytesdeDireccion findById(Long id) {
        return nodeBytesdeDireccionRepository.findById(id).orElseThrow(() -> new RuntimeException("NodeBytesdeDireccion not found"));
    }

    public NodeBytesdeDireccion update(Long id, NodeBytesdeDireccion nodeBytesdeDireccionDetails) {
        NodeBytesdeDireccion nodeBytesdeDireccion = findById(id);
        nodeBytesdeDireccion.setVcnodeBytesdeDireccion(nodeBytesdeDireccionDetails.getVcnodeBytesdeDireccion());
        nodeBytesdeDireccion.setVcconcat(nodeBytesdeDireccionDetails.getVcconcat());
        return nodeBytesdeDireccionRepository.save(nodeBytesdeDireccion);
    }

    public void deleteById(Long id) {
        nodeBytesdeDireccionRepository.deleteById(id);
    }
}

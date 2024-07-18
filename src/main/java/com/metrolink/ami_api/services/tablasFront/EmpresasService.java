package com.metrolink.ami_api.services.tablasFront;

import com.metrolink.ami_api.models.tablasFront.Empresas;
import com.metrolink.ami_api.repositories.tablasFront.EmpresasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
public class EmpresasService {

    @Autowired
    private EmpresasRepository empresasRepository;

    @Transactional
    public Empresas save(Empresas empresa, boolean isUpdate) {
        // Verificar si la Empresa ya existe
        Optional<Empresas> existingEmpresa = empresasRepository.findById(empresa.getNcodigo());
        if (existingEmpresa.isPresent() && !isUpdate) {
            throw new IllegalArgumentException("Empresa with ncodigo " + empresa.getNcodigo() + " already exists.");
        }
        return empresasRepository.save(empresa);
    }

    public List<Empresas> findAll() {
        return empresasRepository.findAll();
    }

    public Empresas findById(Long id) {
        return empresasRepository.findById(id).orElseThrow(() -> new RuntimeException("Empresa not found"));
    }

    public Empresas update(Long id, Empresas empresaDetails) {
        Empresas empresa = findById(id);
        empresa.setVcempresa(empresaDetails.getVcempresa());
        empresa.setVcconcat(empresaDetails.getVcconcat());
        return empresasRepository.save(empresa);
    }

    public void deleteById(Long id) {
        empresasRepository.deleteById(id);
    }
}

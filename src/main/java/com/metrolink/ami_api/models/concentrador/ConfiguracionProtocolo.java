package com.metrolink.ami_api.models.concentrador;



import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.metrolink.ami_api.models.tablasFront.NodeBytesdeDireccion;

import javax.persistence.JoinColumn;


@Data
@Entity
@Table(name = "ami_mov_ConfiguracionProtocolo")
public class ConfiguracionProtocolo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nid;

    private String vcdireccionFisica;
    private String vcdireccionLogica;
    private String vcdireccionCliente;

    @ManyToOne
    @JoinColumn(name = "nCodNodeBytesdeDireccion", referencedColumnName = "ncodigo")
    private NodeBytesdeDireccion nodeBytesdeDireccion;
    
   
}

package com.metrolink.ami_api.models.tablasFront;


import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "ami_mov_ParametrosTiposDeComunicacion")
public class ParamTiposDeComunicacion {
    @Id
    private String vctiposDeComunicacion;
    
    private String vcip;
    private String vcpuerto;
    private String vcendpoint;

    @ManyToOne
    @JoinColumn(name = "ncodigo", referencedColumnName = "ncodigo")
    private TiposDeComunicacion tiposDeComunicacion;
 
}

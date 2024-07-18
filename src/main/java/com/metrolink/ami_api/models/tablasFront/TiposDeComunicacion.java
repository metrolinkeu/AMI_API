package com.metrolink.ami_api.models.tablasFront;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.Table;




@Data
@Entity
@Table(name = "ami_m_TiposDeComunicacion")
public class TiposDeComunicacion {
    @Id
    private Long ncodigo;

    private String vctiposDeComunicacion;
    private String vcconcat;




}

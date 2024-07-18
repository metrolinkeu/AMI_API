package com.metrolink.ami_api.models.concentrador;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Data
@Entity
@Table(name = "ami_m_TipoParamAdvCon")
public class TipoParamAdvCon {
    @Id
    private Long ncod;
    private String vcDescripcion;
}

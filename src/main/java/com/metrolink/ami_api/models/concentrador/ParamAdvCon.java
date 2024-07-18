package com.metrolink.ami_api.models.concentrador;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;



@Data
@Entity
@Table(name = "ami_mov_ParamAdvCon")
public class ParamAdvCon {
    @Id
    private String vcSerie;

    private String vcValue;

    @ManyToOne
    @JoinColumn(name = "ncod", referencedColumnName = "ncod")
    private TipoParamAdvCon tipoParamAdvCon;
}

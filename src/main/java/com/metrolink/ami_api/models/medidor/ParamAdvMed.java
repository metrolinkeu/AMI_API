package com.metrolink.ami_api.models.medidor;

import lombok.Data;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "ami_mov_ParamAdvMed")
public class ParamAdvMed {
    @Id
    private String vcSerieP;

    private String vcValue;

    @ManyToOne
    @JoinColumn(name = "ncod", referencedColumnName = "ncod")
    private TipoParamAdvMed tipoParamAdvMed;


}


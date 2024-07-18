package com.metrolink.ami_api.models.medidor;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "ami_m_TipoParamAdvMed")

public class TipoParamAdvMed {

    @Id
    private Long ncod;
    private String vcDescripcion; 
}

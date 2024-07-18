package com.metrolink.ami_api.models.tablasFront;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "ami_m_Empresas")
public class Empresas {
    @Id
    private Long ncodigo;

    private String vcempresa;
    private String vcconcat;
}

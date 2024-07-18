package com.metrolink.ami_api.models.tablasFront;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "ami_m_CanalesDeComunicacion")
public class CanalesDeComunicacion {
    @Id
    private Long ncodigo;

    private String vccanalDeComunicacion;
    private String vcconcat;
}

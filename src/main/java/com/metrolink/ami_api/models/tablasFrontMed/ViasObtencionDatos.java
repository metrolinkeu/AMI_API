package com.metrolink.ami_api.models.tablasFrontMed;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "ami_m_ViasObtencionDatos")
public class ViasObtencionDatos {
    @Id
    private Long ncodigo;

    private String vcviaObtencionDatos;
    private String vcconcat;
}

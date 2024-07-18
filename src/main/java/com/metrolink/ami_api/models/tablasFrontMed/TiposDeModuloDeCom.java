package com.metrolink.ami_api.models.tablasFrontMed;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "ami_m_TiposDeModuloDeCom")
public class TiposDeModuloDeCom {
    @Id
    private Long ncodigo;

    private String vctiposDeModuloDeCom;
    private String vcconcat;
}

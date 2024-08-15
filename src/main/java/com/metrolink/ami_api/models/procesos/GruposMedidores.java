package com.metrolink.ami_api.models.procesos;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Data
@Entity
@Table(name = "ami_mov_GruposMedidores")
public class GruposMedidores {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nid;

    private String vcfiltro;
    private String vcidentificador;

    @Column(columnDefinition = "CLOB")
    private String jsseriesMed; // Almacenar el JSON como String en un CLOB
}

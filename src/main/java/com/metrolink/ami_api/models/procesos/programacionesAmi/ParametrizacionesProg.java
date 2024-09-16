package com.metrolink.ami_api.models.procesos.programacionesAmi;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "ami_mov_ParametrizacionesProg")
public class ParametrizacionesProg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ncodigo;

    private String vctipoDeLectura;

    @Column(name = "dfechaHoraInicio")
    private Timestamp dfechaHoraInicio;

    private int nreintentos;

    private int ndelayMin;

    @Column(columnDefinition = "CLOB")
    private String jsdiasSemana; // Almacenar el JSON como String en un CLOB

    @Column(columnDefinition = "CLOB")
    private String jsfrecuenciaLecturaLote;
}

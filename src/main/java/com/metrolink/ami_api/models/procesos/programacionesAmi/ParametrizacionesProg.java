package com.metrolink.ami_api.models.procesos.programacionesAmi;


import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Data
@Entity
@Table(name = "ami_mov_ParametrizacionesProg")
public class ParametrizacionesProg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ncodigo;

    private String vctipoDeLectura;
    private String vcfechaInicio;
    private String vchoraInicio;
    private int nreintentos;
    private String vcdiasSemana;
    private String vcfrecuencia;
}

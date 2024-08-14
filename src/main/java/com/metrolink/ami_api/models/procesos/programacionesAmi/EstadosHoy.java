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
@Table(name = "ami_mov_EstadosHoy")
public class EstadosHoy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ncodigo;
    private String vcdescripcion;
    private boolean lproximo;

    @Column(name = "dfechaHoraProximo")
    private Timestamp dfechaHoraProximo;

}

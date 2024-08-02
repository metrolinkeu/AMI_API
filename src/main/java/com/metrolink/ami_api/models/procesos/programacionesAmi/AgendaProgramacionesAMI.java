package com.metrolink.ami_api.models.procesos.programacionesAmi;


import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "ami_mov_AgendaProgramacionesAMI")
public class AgendaProgramacionesAMI {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ncodigo;

    @ManyToOne
    @JoinColumn(name = "ncodProgAMI", referencedColumnName = "ncodigo")
    private ProgramacionesAMI programacionAMI;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ncodEstadoHoy", referencedColumnName = "ncodigo")
    private EstadosHoy estadoHoy;



}

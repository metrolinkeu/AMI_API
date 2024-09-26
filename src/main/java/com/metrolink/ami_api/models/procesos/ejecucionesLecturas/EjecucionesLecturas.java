package com.metrolink.ami_api.models.procesos.ejecucionesLecturas;

import lombok.Data;
import javax.persistence.*;

import com.metrolink.ami_api.models.procesos.programacionesAmi.ProgramacionesAMI;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "ami_mov_EjecucionesLecturas")
public class EjecucionesLecturas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEjecucionLectura;

    private Long idAnteriorIntentoEjecucionLectura;


    @Column(name = "dinicioEjecucionLectura")
    private Timestamp dinicioEjecucionLectura;

    @Column(name = "dFinEjecucionLectura")
    private Timestamp dFinEjecucionLectura;

    private int nIntentoLecturaNumero;



    @ManyToOne
    @JoinColumn(name = "ncodProgAMI", referencedColumnName = "ncodigo")
    private ProgramacionesAMI programacionAMI;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idDetect", referencedColumnName = "idEjecucionLecturaDetect")
    private EjecucionesLecturaDetect ejecucionLecturaDetect;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idAutoConf", referencedColumnName = "idEjecucionLecturaAutoConf")
    private EjecucionesLecturaAutoConf ejecucionLecturaAutoConf;




    
}

package com.metrolink.ami_api.models.procesos.ejecucionesLecturas;

import lombok.Data;
import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "ami_mov_EjecucionesLecturas")
public class EjecucionesLecturas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nidEjecucionLectura;

    private Long nidAnteriorIntentoEjecucionLectura;


    @Column(name = "dinicioEjecucionLectura")
    private Timestamp dinicioEjecucionLectura;

    @Column(name = "dFinEjecucionLectura")
    private Timestamp dfinEjecucionLectura;

    private int nintentoLecturaNumero;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idDetect", referencedColumnName = "nidEjecucionLecturaDetect")
    private EjecucionesLecturaDetect ejecucionLecturaDetect;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idProg", referencedColumnName = "nidEjecucionesLecturaProg")
    private EjecucionesLecturaProg ejecucionLecturaProg;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idAutoConf", referencedColumnName = "nidEjecucionLecturaAutoConf")
    private EjecucionesLecturaAutoConf ejecucionLecturaAutoConf;




    
}

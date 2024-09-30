package com.metrolink.ami_api.models.procesos.ejecucionesLecturas;


import lombok.Data;
import javax.persistence.*;
import com.metrolink.ami_api.models.procesos.programacionesAmi.ProgramacionesAMI;

@Data
@Entity
@Table(name = "ami_mov_EjecucionesLecturaProg")
public class EjecucionesLecturaProg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nidEjecucionesLecturaProg;

    private String vcdescripcionProg;

    private String vcnoserie;

    private String vcserie;

    private boolean llecturaOK;

    @Column(columnDefinition = "CLOB")
    private String jsseriesMed;

    @Column(columnDefinition = "CLOB")
    private String jsmedidoresFaltantesPorLeer;


    @ManyToOne
    @JoinColumn(name = "ncodProgAMI", referencedColumnName = "ncodigo")
    private ProgramacionesAMI programacionAMI;

}

package com.metrolink.ami_api.models.procesos.ejecucionesLecturas;

import lombok.Data;
import javax.persistence.*;



@Data
@Entity
@Table(name = "ami_mov_EjecucionesLecturaDetect")

public class EjecucionesLecturaDetect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEjecucionLecturaDetect;

    private String vcnoserie;

    @Column(columnDefinition = "CLOB")
    private String jsTablaMedidoresDetec; // Almacenar el JSON como String en un CLOB

}

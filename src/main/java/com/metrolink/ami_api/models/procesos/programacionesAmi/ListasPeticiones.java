package com.metrolink.ami_api.models.procesos.programacionesAmi;


import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Data
@Entity
@Table(name = "ami_mov_ListasPeticiones")
public class ListasPeticiones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ncodigo;

    private boolean llectura_perfil_1;
    private boolean leventos;
    private boolean lregistros;
    private boolean lfactorPotencia;
    private boolean linstantaneos;
    private String vcaccionRele;
    private String vcfechaSincronizacion;
    private String vchoraSincronizacion;
}


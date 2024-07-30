package com.metrolink.ami_api.models.medidor;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "ami_mov_CodigosObisCanal")
public class CodigosObisCanal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nid;

    private String vcobis_1;
    private String vcobis_2;
    private String vcobis_3;
    private String vcobis_4;
    private String vcobis_5;
    private String vcobis_6;
    private String vcobis_7;
    private String vcobis_8;
    private String vcobis_9;
    private String vcobis_10;

    
}

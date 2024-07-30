package com.metrolink.ami_api.models.medidor;

import lombok.Data;





import javax.persistence.*;


@Data
@Entity
@Table(name = "ami_mov_CanalesPerfilCarga")
public class CanalesPerfilCarga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nid;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "canal_1", referencedColumnName = "nid")
    private CodigosObisCanal codigosObisCanal_1;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "canal_2", referencedColumnName = "nid")
    private CodigosObisCanal codigosObisCanal_2;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "canal_3", referencedColumnName = "nid")
    private CodigosObisCanal codigosObisCanal_3;
    
}

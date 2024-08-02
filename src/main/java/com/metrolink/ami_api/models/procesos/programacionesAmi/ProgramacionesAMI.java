package com.metrolink.ami_api.models.procesos.programacionesAmi;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.CascadeType;

import com.metrolink.ami_api.models.procesos.GruposMedidores;

@Data
@Entity
@Table(name = "ami_mov_ProgramacionesAMI")
public class ProgramacionesAMI {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ncodigo;

    private String vcestado;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ncodGrupoM", referencedColumnName = "nid")
    private GruposMedidores grupoMedidores;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ncodListPet", referencedColumnName = "ncodigo")
    private ListasPeticiones listaPeticiones;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ncodParaMProg", referencedColumnName = "ncodigo")
    private ParametrizacionesProg parametrizacionProg;
}

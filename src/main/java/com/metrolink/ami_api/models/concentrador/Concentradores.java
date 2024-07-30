package com.metrolink.ami_api.models.concentrador;

import com.metrolink.ami_api.models.tablasFront.Estados;

import com.metrolink.ami_api.models.tablasFront.Marcas;
import com.metrolink.ami_api.models.tablasFront.ParamTiposDeComunicacion;
import com.metrolink.ami_api.models.tablasFront.Empresas;
import com.metrolink.ami_api.models.tablasFront.CanalesDeComunicacion;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "ami_m_Concentradores")
public class Concentradores {

    @Id
    private String vcnoSerie;

    private String vcdescripcion;

    @ManyToOne
    @JoinColumn(name = "ncodMarca", referencedColumnName = "ncodigo")
    private Marcas marca;

    @ManyToOne
    @JoinColumn(name = "ncodEmpresa", referencedColumnName = "ncodigo")
    private Empresas empresa;

    private String vccodigoCaja;

    private String vclongitudLatitud;

    private String vcfechaInstalacion;

    @ManyToOne
    @JoinColumn(name = "ncodEstado", referencedColumnName = "ncodigo")
    private Estados estado;

    @ManyToOne
    @JoinColumn(name = "ncodCanalDeComunicacion", referencedColumnName = "ncodigo")
    private CanalesDeComunicacion canalDeComunicacion;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "nconfiguracionProtocolo_id", referencedColumnName = "nid")
    private ConfiguracionProtocolo configuracionProtocolo;

    @ManyToOne
    @JoinColumn(name = "vctipoDeComunicacion", referencedColumnName = "vctiposDeComunicacion")
    private ParamTiposDeComunicacion paramTiposDeComunicacion;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "vcSerie", referencedColumnName = "vcSerie")
    private ParamAdvCon paramAdvCon;

}

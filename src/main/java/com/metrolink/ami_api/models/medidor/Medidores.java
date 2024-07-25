package com.metrolink.ami_api.models.medidor; //comentario

import lombok.Data;

import javax.persistence.*;


import com.metrolink.ami_api.models.bl.BlMovConfigActivos;
import com.metrolink.ami_api.models.concentrador.Concentradores;
import com.metrolink.ami_api.models.concentrador.ConfiguracionProtocolo;
import com.metrolink.ami_api.models.tablasFront.CanalesDeComunicacion;
import com.metrolink.ami_api.models.tablasFront.Estados;
import com.metrolink.ami_api.models.tablasFrontMed.MarcasMed;
import com.metrolink.ami_api.models.tablasFrontMed.TiposDeModuloDeCom;
import com.metrolink.ami_api.models.tablasFrontMed.ViasObtencionDatos;



@Data
@Entity
@Table(name = "ami_m_Medidores")

public class Medidores {

    @Id
    private String vcSerie;

    private String vcidCliente;
    private String vcdescripcion;

    @ManyToOne
    @JoinColumn(name = "ncodMarcaMed", referencedColumnName = "ncodigo")
    private MarcasMed marcaMed;

    private boolean lisMacro;
    private String vclongitudLatitud;
    private String vcfechaInstalacion;

    @ManyToOne
    @JoinColumn(name = "ncodEstado", referencedColumnName = "ncodigo")
    private Estados estado;

    @ManyToOne
    @JoinColumn(name = "ubicacionEnInfra", referencedColumnName = "ID_CONFIGURACION_ACTIVO")
    private BlMovConfigActivos configuracionActivo;

 
    private String vcfechaHoraUltimaLectura;
    private String vcdíasdeRegDíariosMensuales;
    private String vcdiasdeEventos;

    private String vcperiodoIntegracion;
    private String vcultimoEstadoRele;
    private String vcfirmware;

    @ManyToOne
    @JoinColumn(name = "ncodVia", referencedColumnName = "ncodigo")
    private ViasObtencionDatos viaObtencionDatos;

    @ManyToOne
    @JoinColumn(name = "noConcentrador", referencedColumnName = "vcnoSerie")
    private Concentradores concentrador;

    @ManyToOne
    @JoinColumn(name = "ncodCanalDeComunicacion", referencedColumnName = "ncodigo")
    private CanalesDeComunicacion canalDeComunicacion;

    private String vcip;
    private String vcpuerto;

    @ManyToOne
    @JoinColumn(name = "ncodTipoDeModulo", referencedColumnName = "ncodigo")
    private TiposDeModuloDeCom tipoDeModuloDeCom;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "nconfiguracionProtocolo_id", referencedColumnName = "nid")
    private ConfiguracionProtocolo configuracionProtocolo;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "vcSerieP", referencedColumnName = "vcSerieP")
    private ParamAdvMed paramAdvMed;

    @Transient
    private boolean esExistente;

    

}



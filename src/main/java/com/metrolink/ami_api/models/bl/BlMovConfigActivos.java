package com.metrolink.ami_api.models.bl;

import lombok.Data;
import javax.persistence.*;



@Data
@Entity
@Table(name = "BL_MOV_CONFIG_ACTIVOS")
public class BlMovConfigActivos {

    @Id
    @Column(name = "ID_CONFIGURACION_ACTIVO", nullable = false)
    private Integer idConfiguracionActivo;

    @Column(name = "TIPO_ACTIVO", length = 50)
    private String tipoActivo;

    @Column(name = "VC_NOMBRE", length = 100)
    private String vcNombre;

    @Column(name = "VC_DESCRIPCION", length = 100)
    private String vcDescripcion;

    @Column(name = "VC_ZONA", length = 100)
    private String vcZona;

    @Column(name = "VC_SUBZONA", length = 100)
    private String vcSubzona;

    @Column(name = "VC_SUBESTACION", length = 100)
    private String vcSubestacion;

    @Column(name = "VC_BARRA", length = 100)
    private String vcBarra;

    @Column(name = "NIVEL_TENSION", length = 100)
    private String nivelTension;

    @Column(name = "CLASE_TRANSFORMADOR", length = 100)
    private String claseTransformador;

    @Column(name = "VC_CAPACIDAD_MAXIMA", length = 100)
    private String vcCapacidadMaxima;

    @Column(name = "VC_NIVEL_TENSION_PRIMARIO", length = 100)
    private String vcNivelTensionPrimario;

    @Column(name = "VC_NIVEL_TENSION_SECUNDARIO", length = 100)
    private String vcNivelTensionSecundario;

    @Column(name = "VC_NIVEL_TENSION_TERCIARIO", length = 100)
    private String vcNivelTensionTerciario;

}

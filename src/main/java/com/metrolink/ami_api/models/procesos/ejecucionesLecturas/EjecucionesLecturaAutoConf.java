package com.metrolink.ami_api.models.procesos.ejecucionesLecturas;

import lombok.Data;
import javax.persistence.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.metrolink.ami_api.converters.JsonNodeConverter;

@Data
@Entity
@Table(name = "ami_mov_EjecucionesLecturaAutoConf")
public class EjecucionesLecturaAutoConf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEjecucionLecturaAutoConf;

    private String Caso;

    
    @Column(columnDefinition = "CLOB")
    @Convert(converter = JsonNodeConverter.class)
    private JsonNode jsequiposAutoconfigurar; // Almacenar el JSON como String en un CLOB


}

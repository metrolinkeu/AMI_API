package com.metrolink.ami_api.services.medidor;

import java.util.List;
import java.util.stream.Collectors;

import com.metrolink.ami_api.models.medidor.Medidores;

public class MedidorUtils {

        public static String obtenerStringDeVcSerie(List<Medidores> medidores) {
        // Verifica si la lista de medidores no es nula o está vacía
        if (medidores == null || medidores.isEmpty()) {
            return "[]";  // Retorna un array vacío en formato de String si no hay medidores
        }

        // Usa Streams para mapear cada medidor a su vcSerie y luego unirlos en el formato deseado
        String vcSerieString = medidores.stream()
            .map(Medidores::getVcSerie)  // Mapea cada Medidor a su campo vcSerie
            .map(vcSerie -> "\"" + vcSerie + "\"")  // Agrega comillas alrededor de cada vcSerie
            .collect(Collectors.joining(", ", "[", "]"));  // Une todos los elementos en un String con formato de array JSON

        return vcSerieString;
    }
    
}

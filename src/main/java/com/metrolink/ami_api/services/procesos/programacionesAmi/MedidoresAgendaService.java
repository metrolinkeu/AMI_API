package com.metrolink.ami_api.services.procesos.programacionesAmi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metrolink.ami_api.models.procesos.programacionesAmi.MedidoresAgenda;
import org.springframework.stereotype.Service;

/**
 * Servicio para procesar la agenda de medidores a partir de datos en formato JSON.
 *
 * <p>
 * Esta clase está diseñada para leer un archivo JSON que contiene información sobre medidores y transformarlo en una lista de objetos 
 * {@link MedidoresAgenda}. Un <b>medidor</b> es un dispositivo que registra datos, y una <b>agenda</b> aquí podría representar una lista o un plan de 
 * medidores a ser gestionados o procesados.
 * </p>
 *
 * <p>
 * <b>Método Principal:</b> {@code procesarMedidoresAgenda}
 * <ul>
 *   <li>Este es el único método en la clase y hace todo el trabajo de procesamiento del JSON.</li>
 *   <li><b>Parámetro de Entrada:</b> Toma un {@code String} llamado {@code json}, que es un texto en formato JSON con la información de los medidores.</li>
 *   <li><b>Salida del Método:</b> Devuelve una lista de objetos {@link MedidoresAgenda} después de procesar el JSON.</li>
 *   <li><b>Excepciones:</b> Puede lanzar una {@code IOException} si hay un problema al leer o procesar el JSON.</li>
 * </ul>
 * </p>
 *
 * <p>
 * <b>Cómo Funciona el Método {@code procesarMedidoresAgenda}:</b>
 * <ol>
 *   <li><b>Paso 1:</b> Convierte el texto JSON a un formato que la aplicación puede entender (usando {@code ObjectMapper} de la biblioteca Jackson).</li>
 *   <li><b>Paso 2:</b> Verifica qué tipo de información contiene el JSON:
 *     <ul>
 *       <li>Si tiene una clave llamada {@code "vcnoSerie"}, esto significa que el JSON proporciona un número de serie único para un medidor.</li>
 *       <li>Si tiene una clave llamada {@code "vcseriales"}, esto significa que el JSON proporciona una lista de números de serie.</li>
 *     </ul>
 *   </li>
 *   <li><b>Paso 3:</b> Procesa cada caso:
 *     <ul>
 *       <li><b>Para "vcnoSerie":</b> Extrae el número de serie y crea un nuevo objeto {@link MedidoresAgenda} con ese número de serie. Agrega este objeto a la lista {@code medidoresAgenda}.</li>
 *       <li><b>Para "vcseriales":</b> Extrae cada número de serie de la lista y para cada uno, crea un nuevo objeto {@link MedidoresAgenda}. Agrega cada objeto a la lista {@code medidoresAgenda}.</li>
 *     </ul>
 *   </li>
 *   <li><b>Paso 4:</b> Si el JSON no contiene ninguna de estas claves esperadas, imprime un mensaje diciendo que el formato del JSON no es reconocido.</li>
 * </ol>
 * </p>
 *
 * <p>
 * <b>Uso de la Clase:</b>
 * Esta clase podría ser usada en una aplicación donde se necesita importar o gestionar datos de medidores a partir de archivos JSON. Puede estar conectada a otros servicios que manejan la lógica de negocio o almacenamiento en una base de datos.
 * </p>
 *
 * <p>
 * <b>Resumen:</b>
 * La clase {@code MedidoresAgendaService} es un componente que toma datos en formato JSON sobre medidores, verifica qué tipo de información está presente, procesa los datos y devuelve una lista de objetos representando esos medidores. Utiliza herramientas de procesamiento de JSON para analizar y convertir el texto JSON en objetos manejables dentro de la aplicación.
 * </p>
 */

@Service
public class MedidoresAgendaService {

    /**
     * Procesa un JSON que contiene información de la agenda de medidores y retorna una lista de objetos {@link MedidoresAgenda}.
     * 
     * @param json El JSON que contiene la información de los medidores. Puede contener una clave "vcnoSerie" o "vcseriales".
     * @return Una lista de objetos {@link MedidoresAgenda} procesados a partir del JSON.
     * @throws IOException Si hay un error al leer o procesar el JSON.
     */
    public List<MedidoresAgenda> procesarMedidoresAgenda(String json) throws IOException {

        // Parsear el JSON para obtener los valores
        ObjectMapper objectMapperCon = new ObjectMapper();
        JsonNode jsonNode = objectMapperCon.readTree(json);

        List<MedidoresAgenda> medidoresAgenda = new ArrayList<>();

        // Verificar si el JSON contiene "vcnoSerie" o "vcseriales"
        if (jsonNode.has("vcnoSerie")) {
            String vcnoSerie = jsonNode.get("vcnoSerie").asText();
            // Acción para "vcnoSerie"
            System.out.println("Número de serie (vcnoSerie): " + vcnoSerie);
            // Lógica para manejar "vcnoSerie"
            // Por ejemplo, agregar un MedidoresAgenda con este número de serie
            MedidoresAgenda medidor = new MedidoresAgenda();
            medidor.setVcSerie(vcnoSerie);
            medidoresAgenda.add(medidor);
        } else if (jsonNode.has("vcseriales")) {
            JsonNode vcserialesNode = jsonNode.get("vcseriales");
            // Acción para "vcseriales"
            System.out.println("Lista de series (vcseriales): ");
            // Recorrer los números de serie y realizar acciones
            vcserialesNode.fields().forEachRemaining(entry -> {
                String serial = entry.getValue().asText();
                System.out.println("Serie: " + serial);
                // Lógica para manejar cada número de serie
                // Por ejemplo, agregar un MedidoresAgenda con cada número de serie
                MedidoresAgenda medidor = new MedidoresAgenda();
                medidor.setVcSerie(serial);
                medidoresAgenda.add(medidor);
            });
        } else {
            System.out.println("Formato de JSON no reconocido.");
            // Opcionalmente, podrías lanzar una excepción o manejar este caso de otra forma
        }

        return medidoresAgenda;
    }
}

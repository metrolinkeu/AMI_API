package com.metrolink.ami_api.services.procesos.autoconfiguracion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metrolink.ami_api.models.medidor.CanalesPerfilCarga;
import com.metrolink.ami_api.models.medidor.CodigosObisCanal;
import com.metrolink.ami_api.models.medidor.Medidores;
import com.metrolink.ami_api.models.primeraLectura.AutoConfCanalesPerfilCarga;
import com.metrolink.ami_api.models.primeraLectura.AutoConfCodigosObisCanal;
import com.metrolink.ami_api.models.primeraLectura.AutoconfMedidor;
import com.metrolink.ami_api.repositories.medidor.MedidoresRepository;
import com.metrolink.ami_api.services.medidor.MedidoresService;
import com.metrolink.ami_api.services.procesos.conectorGeneral.ConectorGeneralService;
import com.metrolink.ami_api.services.procesos.generadorDeColas.GeneradorDeColas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class AutoConfiguracionService {

    @Autowired
    private ConectorAutoConfService conectorAutoConfService;

    @Autowired
    private ConectorGeneralService conectorGeneralService;

    @Autowired
    private MedidoresRepository medidoresRepository;

    @Autowired
    private MedidoresService medidoresService;

    @Autowired
    private GeneradorDeColas generadorDeColas;

    public List<AutoconfMedidor> procesarConfiguracion(String json) throws ExecutionException, InterruptedException {
        System.out.println(json);

        // Crear ObjectMapper para trabajar con JSON
        ObjectMapper mapper = new ObjectMapper();

        try {
            // Leer el JSON como árbol de nodos
            JsonNode rootNode = mapper.readTree(json);

            // Obtener el número de serie del concentrador desde el JSON
            String vcnoSerie = rootNode.get("vcnoSerie").asText();

            // Usar CompletableFuture para esperar el resultado
        CompletableFuture<List<AutoconfMedidor>> futureAutoConf = generadorDeColas.encolarSolicitud(vcnoSerie, () -> {
            System.out.println("Estoy en la tarea para encolar (AutoConfiguracionService)");
            return conectorGeneralService.UsarConectorAutoConfMed(rootNode);
        });

        // Esperar a que se complete la tarea y obtener el resultado
        List<AutoconfMedidor> autoConfiguraciones = futureAutoConf.get(); // Este método bloquea hasta que autoConfiguraciones esté disponible


            // Iterar sobre las autoconfiguraciones para actualizar Medidores
            for (AutoconfMedidor autoconfMedidor : autoConfiguraciones) {
                String vcSerie = autoconfMedidor.getVcSerie();
                Timestamp dfechaHoraUltimaLectura = autoconfMedidor.getDfechaHoraUltimaLectura();
                String vcdíasdeRegDíariosMensuales = autoconfMedidor.getVcdíasdeRegDíariosMensuales();
                String vcdiasdeEventos = autoconfMedidor.getVcdiasdeEventos();
                String vcperiodoIntegracion = autoconfMedidor.getVcperiodoIntegracion();
                String vcultimoEstadoRele = autoconfMedidor.getVcultimoEstadoRele();
                String vcfirmware = autoconfMedidor.getVcfirmware();

                AutoConfCanalesPerfilCarga autoConfcanalesPerfilCarga = autoconfMedidor.getAutoConfcanalesPerfilCarga();

                // Crear una nueva instancia de CanalesPerfilCarga
                CanalesPerfilCarga canalesPerfilCarga = new CanalesPerfilCarga();

                // Copiar los valores de autoConfCanalesPerfilCarga a canalesPerfilCarga
                if (autoConfcanalesPerfilCarga != null) {
                    // Copiar CodigosObisCanal_1
                    AutoConfCodigosObisCanal autoConfCodigosObisCanal1 = autoConfcanalesPerfilCarga
                            .getAutoConfCodigosObisCanal_1();
                    if (autoConfCodigosObisCanal1 != null) {
                        CodigosObisCanal codigosObisCanal1 = new CodigosObisCanal();
                        codigosObisCanal1.setVcobis_1(autoConfCodigosObisCanal1.getVcobis_1());
                        codigosObisCanal1.setVcobis_2(autoConfCodigosObisCanal1.getVcobis_2());
                        codigosObisCanal1.setVcobis_3(autoConfCodigosObisCanal1.getVcobis_3());
                        codigosObisCanal1.setVcobis_4(autoConfCodigosObisCanal1.getVcobis_4());
                        codigosObisCanal1.setVcobis_5(autoConfCodigosObisCanal1.getVcobis_5());
                        codigosObisCanal1.setVcobis_6(autoConfCodigosObisCanal1.getVcobis_6());
                        codigosObisCanal1.setVcobis_7(autoConfCodigosObisCanal1.getVcobis_7());
                        codigosObisCanal1.setVcobis_8(autoConfCodigosObisCanal1.getVcobis_8());
                        codigosObisCanal1.setVcobis_9(autoConfCodigosObisCanal1.getVcobis_9());
                        codigosObisCanal1.setVcobis_10(autoConfCodigosObisCanal1.getVcobis_10());

                        canalesPerfilCarga.setCodigosObisCanal_1(codigosObisCanal1);
                    }

                    // Copiar CodigosObisCanal_2
                    AutoConfCodigosObisCanal autoConfCodigosObisCanal2 = autoConfcanalesPerfilCarga
                            .getAutoConfCodigosObisCanal_2();
                    if (autoConfCodigosObisCanal2 != null) {
                        CodigosObisCanal codigosObisCanal2 = new CodigosObisCanal();
                        codigosObisCanal2.setVcobis_1(autoConfCodigosObisCanal2.getVcobis_1());
                        codigosObisCanal2.setVcobis_2(autoConfCodigosObisCanal2.getVcobis_2());
                        codigosObisCanal2.setVcobis_3(autoConfCodigosObisCanal2.getVcobis_3());
                        codigosObisCanal2.setVcobis_4(autoConfCodigosObisCanal2.getVcobis_4());
                        codigosObisCanal2.setVcobis_5(autoConfCodigosObisCanal2.getVcobis_5());
                        codigosObisCanal2.setVcobis_6(autoConfCodigosObisCanal2.getVcobis_6());
                        codigosObisCanal2.setVcobis_7(autoConfCodigosObisCanal2.getVcobis_7());
                        codigosObisCanal2.setVcobis_8(autoConfCodigosObisCanal2.getVcobis_8());
                        codigosObisCanal2.setVcobis_9(autoConfCodigosObisCanal2.getVcobis_9());
                        codigosObisCanal2.setVcobis_10(autoConfCodigosObisCanal2.getVcobis_10());

                        canalesPerfilCarga.setCodigosObisCanal_2(codigosObisCanal2);
                    }

                    // Copiar CodigosObisCanal_3
                    AutoConfCodigosObisCanal autoConfCodigosObisCanal3 = autoConfcanalesPerfilCarga
                            .getAutoConfCodigosObisCanal_3();
                    if (autoConfCodigosObisCanal3 != null) {
                        CodigosObisCanal codigosObisCanal3 = new CodigosObisCanal();
                        codigosObisCanal3.setVcobis_1(autoConfCodigosObisCanal3.getVcobis_1());
                        codigosObisCanal3.setVcobis_2(autoConfCodigosObisCanal3.getVcobis_2());
                        codigosObisCanal3.setVcobis_3(autoConfCodigosObisCanal3.getVcobis_3());
                        codigosObisCanal3.setVcobis_4(autoConfCodigosObisCanal3.getVcobis_4());
                        codigosObisCanal3.setVcobis_5(autoConfCodigosObisCanal3.getVcobis_5());
                        codigosObisCanal3.setVcobis_6(autoConfCodigosObisCanal3.getVcobis_6());
                        codigosObisCanal3.setVcobis_7(autoConfCodigosObisCanal3.getVcobis_7());
                        codigosObisCanal3.setVcobis_8(autoConfCodigosObisCanal3.getVcobis_8());
                        codigosObisCanal3.setVcobis_9(autoConfCodigosObisCanal3.getVcobis_9());
                        codigosObisCanal3.setVcobis_10(autoConfCodigosObisCanal3.getVcobis_10());

                        canalesPerfilCarga.setCodigosObisCanal_3(codigosObisCanal3);
                    }
                }

                // Buscar Medidor por vcSerie
                Medidores medidor = medidoresService.findById(vcSerie);

                if (medidor != null) {
                    // Actualizar el campo vcfirmware
                    medidor.setDfechaHoraUltimaLectura(dfechaHoraUltimaLectura);
                    medidor.setVcdíasdeRegDíariosMensuales(vcdíasdeRegDíariosMensuales);
                    medidor.setVcdiasdeEventos(vcdiasdeEventos);
                    medidor.setVcperiodoIntegracion(vcperiodoIntegracion);
                    medidor.setVcultimoEstadoRele(vcultimoEstadoRele);
                    medidor.setVcfirmware(vcfirmware);
                    medidor.setCanalesPerfilCarga(canalesPerfilCarga);

                    // Guardar el medidor actualizado en la base de datos
                    medidoresRepository.save(medidor);
                } else {
                    System.out.println("Medidor con vcSerie " + vcSerie + " no encontrado.");
                }
            }

            // Pasar el JsonNode a ConectorAutoConfService para su procesamiento
            return autoConfiguraciones;

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Retornar una lista vacía en caso de error
        return new ArrayList<>();
    }
}

package com.metrolink.ami_api.services.medidor;

import com.metrolink.ami_api.models.concentrador.ConfiguracionProtocolo;
import com.metrolink.ami_api.models.medidor.CanalesPerfilCarga;
import com.metrolink.ami_api.models.medidor.CodigosObisCanal;
import com.metrolink.ami_api.models.medidor.Medidores;
import com.metrolink.ami_api.models.medidor.ParamAdvMed;
import com.metrolink.ami_api.models.medidor.TipoParamAdvMed;
import com.metrolink.ami_api.models.tablasFront.NodeBytesdeDireccion;
import com.metrolink.ami_api.repositories.bl.BlMovConfigActivosRepository;
import com.metrolink.ami_api.repositories.concentrador.ConcentradoresRepository;
import com.metrolink.ami_api.repositories.medidor.MedidoresRepository;
import com.metrolink.ami_api.repositories.medidor.TipoParamAdvMedRepository;
import com.metrolink.ami_api.repositories.tablasFront.EstadosRepository;
import com.metrolink.ami_api.repositories.tablasFront.NodeBytesdeDireccionRepository;
import com.metrolink.ami_api.repositories.tablasFront.TiposDeComunicacionRepository;
import com.metrolink.ami_api.repositories.tablasFrontMed.MarcasMedRepository;
import com.metrolink.ami_api.repositories.tablasFrontMed.TiposDeModuloDeComRepository;
import com.metrolink.ami_api.repositories.tablasFrontMed.ViasObtencionDatosRepository;
import com.metrolink.ami_api.repositories.tablasFront.CanalesDeComunicacionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class MedidoresService {

    @Autowired
    private MedidoresRepository medidoresRepository;

    @Autowired
    private MarcasMedRepository marcaMedRepository;

    @Autowired
    private EstadosRepository estadoRepository;

    @Autowired
    private BlMovConfigActivosRepository configuracionActivoRepository;

    @Autowired
    private ViasObtencionDatosRepository viasObtencionDatosRepository;

    @Autowired
    private ConcentradoresRepository concentradorRepository;

    @Autowired
    private CanalesDeComunicacionRepository canalDeComunicacionRepository;

    @Autowired
    private TiposDeModuloDeComRepository tipoDeModuloDeComRepository;

    @Autowired
    private NodeBytesdeDireccionRepository nodeBytesdeDireccionRepository;

    @Autowired
    private TipoParamAdvMedRepository tipoParamAdvMedRepository;

    @Transactional
    public Medidores save(Medidores medidor, boolean isUpdate) {
        Optional<Medidores> existingMedidor = medidoresRepository.findById(medidor.getVcSerie());
        if (existingMedidor.isPresent() && !isUpdate) {
            throw new IllegalArgumentException("Medidor with vcSerie " + medidor.getVcSerie() + " already exists.");
        }
        return medidoresRepository.save(medidor);
    }

    public List<Medidores> findAll() {
        return medidoresRepository.findAll();
    }

    public Medidores findById(String vcSerie) {
        return medidoresRepository.findById(vcSerie).orElseThrow(() -> new RuntimeException("Medidor not found"));
    }

    public List<Medidores> findByConcentradorVcnoSerie(String vcnoSerie) {
        return medidoresRepository.findByConcentradorVcnoSerie(vcnoSerie);
    }

    public List<Medidores> findByVcsic(String vcsic) {
        return medidoresRepository.findByVcsic(vcsic);
    }

    public Medidores update(String vcSerie, Medidores medidorDetails) {
        Medidores medidor = findById(vcSerie);
        medidor.setVcidCliente(medidorDetails.getVcidCliente());
        medidor.setVcdescripcion(medidorDetails.getVcdescripcion());
        medidor.setMarcaMed(medidorDetails.getMarcaMed());
        medidor.setLisMacro(medidorDetails.isLisMacro());
        medidor.setVclongitudLatitud(medidorDetails.getVclongitudLatitud());
        medidor.setDfechaInstalacion(medidorDetails.getDfechaInstalacion());
        medidor.setEstado(medidorDetails.getEstado());
        medidor.setDfechaHoraUltimaLectura(medidorDetails.getDfechaHoraUltimaLectura());
        medidor.setVcperiodoIntegracion(medidorDetails.getVcperiodoIntegracion());
        medidor.setVcultimoEstadoRele(medidorDetails.getVcultimoEstadoRele());
        medidor.setVcfirmware(medidorDetails.getVcfirmware());
        medidor.setViaObtencionDatos(medidorDetails.getViaObtencionDatos());
        medidor.setConcentrador(medidorDetails.getConcentrador());
        medidor.setCanalDeComunicacion(medidorDetails.getCanalDeComunicacion());
        medidor.setVcip(medidorDetails.getVcip());
        medidor.setVcpuerto(medidorDetails.getVcpuerto());
        medidor.setTipoDeModuloDeCom(medidorDetails.getTipoDeModuloDeCom());
        medidor.setConfiguracionProtocolo(medidorDetails.getConfiguracionProtocolo());
        medidor.setParamAdvMed(medidorDetails.getParamAdvMed());
        medidor.setConfiguracionActivo(medidorDetails.getConfiguracionActivo());
        medidor.setVcsic(medidorDetails.getVcsic());
        medidor.setCanalesPerfilCarga(medidorDetails.getCanalesPerfilCarga());

        return medidoresRepository.save(medidor);
    }

    public void deleteById(String vcSerie) {
        medidoresRepository.deleteById(vcSerie);
    }

    public Medidores updatePartial(String vcSerie, Map<String, Object> updates) {
        Medidores medidor = findById(vcSerie); // Lanza excepción si no encuentra el medidor

        // Iterar sobre el mapa de actualizaciones y aplicarlas a la entidad Medidores
        updates.forEach((key, value) -> {
            switch (key) {
                case "vcidCliente":
                    medidor.setVcidCliente((String) value);
                    break;
                case "vcdescripcion":
                    medidor.setVcdescripcion((String) value);
                    break;
                case "marcaMed":
                    Long nCodigoMarcaMed = Long.valueOf(value.toString());
                    medidor.setMarcaMed(marcaMedRepository.findById(nCodigoMarcaMed)
                            .orElseThrow(() -> new RuntimeException("MarcaMed not found")));
                    break;
                case "lisMacro":
                    medidor.setLisMacro((Boolean) value);
                    break;
                case "vclongitudLatitud":
                    medidor.setVclongitudLatitud((String) value);
                    break;
                case "dfechaInstalacion":
                    DateTimeFormatter formatterFechaInstala = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                    LocalDateTime dateTimeFechaInstala = LocalDateTime.parse((String) value, formatterFechaInstala);
                    medidor.setDfechaInstalacion(Timestamp.valueOf(dateTimeFechaInstala));
                    break;
                case "estado":
                    Long nCodigoEstado = Long.valueOf(value.toString());
                    medidor.setEstado(estadoRepository.findById(nCodigoEstado)
                            .orElseThrow(() -> new RuntimeException("Estado not found")));
                    break;
                case "configuracionActivo":
                    medidor.setConfiguracionActivo(configuracionActivoRepository.findById((Integer) value)
                            .orElseThrow(() -> new RuntimeException("Configuracion Activo not found")));
                    break;
                case "dfechaHoraUltimaLectura":
                    DateTimeFormatter formatterUltimaLec = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                    LocalDateTime dateTimeUltimaLec = LocalDateTime.parse((String) value, formatterUltimaLec);
                    medidor.setDfechaHoraUltimaLectura(Timestamp.valueOf(dateTimeUltimaLec));
                    break;
                case "vcdíasdeRegDíariosMensuales":
                    medidor.setVcdíasdeRegDíariosMensuales((String) value);
                    break;
                case "vcdiasdeEventos":
                    medidor.setVcdiasdeEventos((String) value);
                    break;

                case "vcperiodoIntegracion":
                    medidor.setVcperiodoIntegracion((String) value);
                    break;

                case "vcultimoEstadoRele":
                    medidor.setVcultimoEstadoRele((String) value);
                    break;

                case "vcfirmware":
                    medidor.setVcfirmware((String) value);
                    break;

                case "viaObtencionDatos":
                    Long nCodigoviaObtencionDatos = Long.valueOf(value.toString());
                    medidor.setViaObtencionDatos(viasObtencionDatosRepository.findById(nCodigoviaObtencionDatos)
                            .orElseThrow(() -> new RuntimeException("Estado not found")));
                    break;

                case "concentrador":
                    String nCodigoconcentrador = value.toString();
                    medidor.setConcentrador(concentradorRepository.findById(nCodigoconcentrador)
                            .orElseThrow(() -> new RuntimeException("Estado not found")));
                    break;

                case "canalDeComunicacion":
                    Long nCodigocanalDeComunicacion = Long.valueOf(value.toString());
                    medidor.setCanalDeComunicacion(canalDeComunicacionRepository.findById(nCodigocanalDeComunicacion)
                            .orElseThrow(() -> new RuntimeException("Estado not found")));
                    break;

                case "vcip":
                    medidor.setVcip((String) value);
                    break;

                case "vcpuerto":
                    medidor.setVcpuerto((String) value);
                    break;

                case "tipoDeModuloDeCom":
                    Long nCodigotipoDeModuloDeCom = Long.valueOf(value.toString());
                    medidor.setTipoDeModuloDeCom(tipoDeModuloDeComRepository.findById(nCodigotipoDeModuloDeCom)
                            .orElseThrow(() -> new RuntimeException("Estado not found")));
                    break;

                case "configuracionProtocolo":
                    Map<String, Object> configuracionProtocoloMap = (Map<String, Object>) value;
                    ConfiguracionProtocolo configuracionProtocolo = medidor.getConfiguracionProtocolo();
                    if (configuracionProtocolo == null) {
                        configuracionProtocolo = new ConfiguracionProtocolo();
                    }
                    if (configuracionProtocoloMap.containsKey("vcdireccionFisica")) {
                        configuracionProtocolo
                                .setVcdireccionFisica((String) configuracionProtocoloMap.get("vcdireccionFisica"));
                    }
                    if (configuracionProtocoloMap.containsKey("vcdireccionLogica")) {
                        configuracionProtocolo
                                .setVcdireccionLogica((String) configuracionProtocoloMap.get("vcdireccionLogica"));
                    }
                    if (configuracionProtocoloMap.containsKey("vcdireccionCliente")) {
                        configuracionProtocolo
                                .setVcdireccionCliente((String) configuracionProtocoloMap.get("vcdireccionCliente"));
                    }
                    if (configuracionProtocoloMap.containsKey("nodeBytesdeDireccion")) {
                        Map<String, Object> nodeBytesdeDireccionMap = (Map<String, Object>) configuracionProtocoloMap
                                .get("nodeBytesdeDireccion");
                        if (nodeBytesdeDireccionMap.containsKey("ncodigo")) {
                            Long ncodigo = Long.valueOf(nodeBytesdeDireccionMap.get("ncodigo").toString());
                            NodeBytesdeDireccion nodeBytesdeDireccion = nodeBytesdeDireccionRepository.findById(ncodigo)
                                    .orElseThrow(() -> new RuntimeException("NodeBytesdeDireccion not found"));
                            configuracionProtocolo.setNodeBytesdeDireccion(nodeBytesdeDireccion);
                        }
                    }
                    medidor.setConfiguracionProtocolo(configuracionProtocolo);
                    break;

                case "paramAdvMed":
                    Map<String, Object> paramAdvMedMap = (Map<String, Object>) value;
                    ParamAdvMed paramAdvMed = medidor.getParamAdvMed();
                    if (paramAdvMed == null) {
                        paramAdvMed = new ParamAdvMed();
                    }
                    if (paramAdvMedMap.containsKey("vcSerieP")) {
                        paramAdvMed.setVcSerieP((String) paramAdvMedMap.get("vcSerieP"));
                    }
                    if (paramAdvMedMap.containsKey("vcValue")) {
                        paramAdvMed.setVcValue((String) paramAdvMedMap.get("vcValue"));
                    }
                    if (paramAdvMedMap.containsKey("tipoParamAdvMed")) {
                        Map<String, Object> tipoParamAdvMedMap = (Map<String, Object>) paramAdvMedMap
                                .get("tipoParamAdvMed");
                        if (tipoParamAdvMedMap.containsKey("ncod")) {
                            Long ncodigo = Long.valueOf(tipoParamAdvMedMap.get("ncod").toString());
                            TipoParamAdvMed tipoParamAdvMed = tipoParamAdvMedRepository.findById(ncodigo)
                                    .orElseThrow(() -> new RuntimeException("NodeBytesdeDireccion not found"));
                            paramAdvMed.setTipoParamAdvMed(tipoParamAdvMed);
                        }
                    }
                    medidor.setParamAdvMed(paramAdvMed);
                    break;

                case "vcsic":
                    medidor.setVcsic((String) value);
                    break;

                case "canalesPerfilCarga":
                    Map<String, Object> canalesPerfilCargaMap = (Map<String, Object>) value;
                    CanalesPerfilCarga canalesPerfilCarga = medidor.getCanalesPerfilCarga();

                    if (canalesPerfilCarga == null) {
                        canalesPerfilCarga = new CanalesPerfilCarga();
                    }

                    // Actualizar CodigosObisCanal_1
                    if (canalesPerfilCargaMap.containsKey("codigosObisCanal_1")) {
                        Map<String, Object> codigosObisCanal_1Map = (Map<String, Object>) canalesPerfilCargaMap
                                .get("codigosObisCanal_1");
                        CodigosObisCanal codigosObisCanal_1 = canalesPerfilCarga.getCodigosObisCanal_1();
                        if (codigosObisCanal_1 == null) {
                            codigosObisCanal_1 = new CodigosObisCanal();
                        }
                        actualizarCodigosObis(codigosObisCanal_1, codigosObisCanal_1Map);
                        canalesPerfilCarga.setCodigosObisCanal_1(codigosObisCanal_1);
                    }

                    // Actualizar CodigosObisCanal_2
                    if (canalesPerfilCargaMap.containsKey("codigosObisCanal_2")) {
                        Map<String, Object> codigosObisCanal_2Map = (Map<String, Object>) canalesPerfilCargaMap
                                .get("codigosObisCanal_2");
                        CodigosObisCanal codigosObisCanal_2 = canalesPerfilCarga.getCodigosObisCanal_2();
                        if (codigosObisCanal_2 == null) {
                            codigosObisCanal_2 = new CodigosObisCanal();
                        }
                        actualizarCodigosObis(codigosObisCanal_2, codigosObisCanal_2Map);
                        canalesPerfilCarga.setCodigosObisCanal_2(codigosObisCanal_2);
                    }

                    // Actualizar CodigosObisCanal_3
                    if (canalesPerfilCargaMap.containsKey("codigosObisCanal_3")) {
                        Map<String, Object> codigosObisCanal_3Map = (Map<String, Object>) canalesPerfilCargaMap
                                .get("codigosObisCanal_3");
                        CodigosObisCanal codigosObisCanal_3 = canalesPerfilCarga.getCodigosObisCanal_3();
                        if (codigosObisCanal_3 == null) {
                            codigosObisCanal_3 = new CodigosObisCanal();
                        }
                        actualizarCodigosObis(codigosObisCanal_3, codigosObisCanal_3Map);
                        canalesPerfilCarga.setCodigosObisCanal_3(codigosObisCanal_3);
                    }

                    medidor.setCanalesPerfilCarga(canalesPerfilCarga);
                    break;

                // Añadir más campos según sea necesario...
                default:
                    throw new IllegalArgumentException("Campo no reconocido: " + key);
            }
        });

        return medidoresRepository.save(medidor);
    }

    private void actualizarCodigosObis(CodigosObisCanal codigosObisCanal, Map<String, Object> obisMap) {
        if (obisMap.containsKey("vcobis_1")) {
            codigosObisCanal.setVcobis_1((String) obisMap.get("vcobis_1"));
        }
        if (obisMap.containsKey("vcobis_2")) {
            codigosObisCanal.setVcobis_2((String) obisMap.get("vcobis_2"));
        }
        if (obisMap.containsKey("vcobis_3")) {
            codigosObisCanal.setVcobis_3((String) obisMap.get("vcobis_3"));
        }
        if (obisMap.containsKey("vcobis_4")) {
            codigosObisCanal.setVcobis_4((String) obisMap.get("vcobis_4"));
        }
        if (obisMap.containsKey("vcobis_5")) {
            codigosObisCanal.setVcobis_5((String) obisMap.get("vcobis_5"));
        }
        if (obisMap.containsKey("vcobis_6")) {
            codigosObisCanal.setVcobis_6((String) obisMap.get("vcobis_6"));
        }
        if (obisMap.containsKey("vcobis_7")) {
            codigosObisCanal.setVcobis_7((String) obisMap.get("vcobis_7"));
        }
        if (obisMap.containsKey("vcobis_8")) {
            codigosObisCanal.setVcobis_8((String) obisMap.get("vcobis_8"));
        }
        if (obisMap.containsKey("vcobis_9")) {
            codigosObisCanal.setVcobis_9((String) obisMap.get("vcobis_9"));
        }
        if (obisMap.containsKey("vcobis_10")) {
            codigosObisCanal.setVcobis_10((String) obisMap.get("vcobis_10"));
        }
    }

}

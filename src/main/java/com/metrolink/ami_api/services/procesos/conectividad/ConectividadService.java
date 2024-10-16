package com.metrolink.ami_api.services.procesos.conectividad;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.metrolink.ami_api.comunications.conectividadTCP.ConectividadTCP;
import com.metrolink.ami_api.comunications.conectividadTCP.ConexionStreams;
import com.metrolink.ami_api.comunications.conectividadTCP.TcpServerService;
import com.metrolink.ami_api.models.concentrador.Concentradores;
import com.metrolink.ami_api.models.medidor.Medidores;
import com.metrolink.ami_api.services.concentrador.ConcentradoresService;
import com.metrolink.ami_api.services.medidor.MedidoresService;

@Service
public class ConectividadService {

    @Autowired
    private ConcentradoresService concentradoresService;

    @Autowired
    private MedidoresService medidoresService;

    @Autowired
    private TcpServerService tcpServerService;

    @Autowired
    private ConectividadTCP conectividadTCP;

    public ConexionStreams conectividad(String serie) {
        // Primero detectar el equipo
        if (serie.startsWith("C_")) {
            return manejarConcentrador(serie.substring(2));
        } else if (serie.startsWith("M_")) {
            return manejarMedidor(serie.substring(2));
        } else {
            System.out.println("La serie no empieza ni con C_ ni con M_.");
            return null;
        }
    }

    private ConexionStreams manejarConcentrador(String nSerie) {
        System.out.println("La serie empieza con C_. Número extraído: " + nSerie);

        Concentradores concentrador = concentradoresService.findById(nSerie);
        if (concentrador == null) {
            throw new RuntimeException("Concentrador no encontrado para vcnoSerie: " + nSerie);
        }

        String tipoComunicacion = obtenerTipoComunicacion(concentrador);

        if ("Servidor".equals(tipoComunicacion)) {
            // La API actúa como cliente, obtenemos los IOStreams
            return manejarComunicacionCliente("concentrador", nSerie);
        } else if ("Cliente".equals(tipoComunicacion)) {
            // La parte del servidor aún no se implementa, retornamos null
            System.out.println("El servidor TCP no está implementado aún.");
            return null;
        } else {
            System.out.println("Tipo de comunicación desconocido para el concentrador.");
            return null;
        }
    }

    private ConexionStreams manejarMedidor(String nSerie) {
        System.out.println("La serie empieza con M_. Número extraído: " + nSerie);

        Medidores medidor = medidoresService.findById(nSerie);
        if (medidor == null) {
            throw new RuntimeException("Medidor no encontrado para vcnoSerie: " + nSerie);
        }

        if (medidor.getConcentrador() != null) {
            return manejarConcentradorMedidor(medidor);
        } else {
            return manejarComunicacionCliente("medidor", nSerie);
        }
    }

    private ConexionStreams manejarConcentradorMedidor(Medidores medidor) {
        String vcnoSerieConcentrador = medidor.getConcentrador().getVcnoSerie();
        Concentradores concentrador = concentradoresService.findById(vcnoSerieConcentrador);

        if (concentrador == null) {
            throw new RuntimeException("Concentrador no encontrado para vcnoSerie: " + vcnoSerieConcentrador);
        }

        String tipoComunicacion = obtenerTipoComunicacion(concentrador);

        if ("Servidor".equals(tipoComunicacion)) {
            return manejarComunicacionCliente("concentrador", vcnoSerieConcentrador);
        } else if ("Cliente".equals(tipoComunicacion)) {
            // La parte del servidor aún no se implementa, retornamos null
            System.out.println("El servidor TCP no está implementado aún.");
            return null;
        } else {
            System.out.println("Tipo de comunicación desconocido para el concentrador.");
            return null;
        }
    }

    private String obtenerTipoComunicacion(Concentradores concentrador) {
        return concentrador.getParamTiposDeComunicacion().getVctiposDeComunicacion();
    }

    private ConexionStreams manejarComunicacionCliente(String tipoEquipo, String nSerie) {
        // La API se comporta en modo cliente, por lo que necesita los IOStreams
        ConexionStreams conexionStreams = conectividadTCP.obtenerIOStreams(tipoEquipo, nSerie);

        if (conexionStreams != null) {
            return conexionStreams;

        } else {
            System.out.println("No se pudo establecer la conexión con el " + tipoEquipo + ".");
            return null;
        }
    }

    private void cerrarConexion(ConexionStreams conexionStreams) {
        try {
            if (conexionStreams.getSocket() != null && !conexionStreams.getSocket().isClosed()) {
                conexionStreams.getSocket().close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

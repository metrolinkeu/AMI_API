package com.metrolink.ami_api.comunications.conectividadTCP;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.metrolink.ami_api.models.concentrador.Concentradores;
import com.metrolink.ami_api.models.medidor.Medidores;
import com.metrolink.ami_api.services.concentrador.ConcentradoresService;
import com.metrolink.ami_api.services.medidor.MedidoresService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;

@Service
public class ConectividadTCP {

    @Autowired
    private MedidoresService medidoresService;

    @Autowired
    private ConcentradoresService concentradoresService;

    public ConexionStreams obtenerIOStreams(String tipoEquipo, String serie) {

        System.out.println("Este es el metodo definir conexion para " + tipoEquipo + " con serie: "+  serie);

        String ipAddress = "";
        int puerto = 0;

        if (tipoEquipo.equals("medidor")) {
            Medidores medidor = medidoresService.findById(serie);
            if (medidor != null) {
                ipAddress = medidor.getVcip();
                puerto = Integer.parseInt(medidor.getVcpuerto());
            } else {
                System.out.println("No se encontró el medidor con serie: " + serie);
                return null; // O lanza una excepción si prefieres
            }
        } else if (tipoEquipo.equals("concentrador")) {
            Concentradores concentrador = concentradoresService.findById(serie);
            if (concentrador != null) {
                ipAddress = concentrador.getParamTiposDeComunicacion().getVcip();
                puerto = Integer.parseInt(concentrador.getParamTiposDeComunicacion().getVcpuerto());

                System.out.println("Ip: " + ipAddress + " Puerto: " + puerto + " del servidor TCP a conectar");
            } else {
                System.out.println("No se encontró el concentrador con noserie: " + serie);
                return null; // O lanza una excepción si prefieres
            }
        }

        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ipAddress, puerto), 5000); // Tiempo de espera de 5 segundos

            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            // Retorna los streams y el socket encapsulados en la nueva clase
            return new ConexionStreams(in, out, socket);

        } catch (IOException e) {
            e.printStackTrace();
            return null; // O lanza una excepción personalizada
        }

    }
}
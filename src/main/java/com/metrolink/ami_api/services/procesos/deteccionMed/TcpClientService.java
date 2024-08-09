package com.metrolink.ami_api.services.procesos.deteccionMed;

import org.springframework.stereotype.Service;

import java.io.IOException;

import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;

@Service
public class TcpClientService {

    public String sendBytesToAddressAndPort(byte[] bytes, String address, int port) {
        try (Socket socket = new Socket(address, port);
                OutputStream out = socket.getOutputStream();
                InputStream in = socket.getInputStream()) {

            // Enviar los bytes al servidor
            out.write(bytes);
            out.flush();

            // Buffer para leer la respuesta
            byte[] responseBuffer = new byte[1024]; // Ajusta el tamaño según la respuesta esperada
            int bytesRead = in.read(responseBuffer);

            if (bytesRead != -1) {
                // Convertir los bytes recibidos a un formato hexadecimal
                StringBuilder hexResponse = new StringBuilder();
                for (int i = 0; i < bytesRead; i++) {
                    if (i < 6) {
                        // Imprimir los primeros 6 bytes en color rojo con fondo azul
                        hexResponse.append("\033[41;34m") // Secuencia ANSI para fondo azul y texto rojo
                                .append(String.format("%02X", responseBuffer[i]))
                                .append("\033[0m"); // Reset de color
                    } else {
                        // Imprimir el resto de los bytes en color estándar
                        hexResponse.append(String.format("%02X", responseBuffer[i]));
                    }
                }
                return hexResponse.toString();
            } else {
                return "No response received from the server.";
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "Error al comunicarse con el servidor: " + e.getMessage();
        }
    }
}

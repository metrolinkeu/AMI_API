package com.metrolink.ami_api.comunications.conectividadTCP;

import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConexionStreams {

    private InputStream inputStream;
    private OutputStream outputStream;
    private Socket socket;

}
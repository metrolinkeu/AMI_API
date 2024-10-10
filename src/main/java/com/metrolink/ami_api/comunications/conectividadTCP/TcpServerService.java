package com.metrolink.ami_api.comunications.conectividadTCP;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TcpServerService {

    private ServerSocket serverSocket;
    private ExecutorService clientHandlerPool = Executors.newCachedThreadPool();
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private Future<?> serverFuture;

    public void startServer(int port, long durationInMinutes) {
        if (serverSocket != null && !serverSocket.isClosed()) {
            System.out.println("El servidor TCP ya está en ejecución en el puerto " + port);
            return;
        }

        serverFuture = Executors.newSingleThreadExecutor().submit(() -> {
            try {
                serverSocket = new ServerSocket(port);
                System.out.println("Servidor TCP iniciado en el puerto " + port);

                while (!serverSocket.isClosed()) {
                    Socket clientSocket = serverSocket.accept();
                    handleClient(clientSocket);
                }

            } catch (IOException e) {
                if (!serverSocket.isClosed()) {
                    e.printStackTrace();
                }
            }
        });

        // Programar el cierre del servidor después de la duración especificada
        scheduler.schedule(this::stopServer, durationInMinutes, TimeUnit.MINUTES);
    }

    public void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                System.out.println("Cerrando el servidor TCP...");
                serverSocket.close();
            }
            if (serverFuture != null && !serverFuture.isDone()) {
                serverFuture.cancel(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        clientHandlerPool.submit(() -> {
            try (
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(), true)
            ) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Mensaje recibido: " + inputLine);
                    out.println("Eco: " + inputLine); // Respuesta al cliente
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

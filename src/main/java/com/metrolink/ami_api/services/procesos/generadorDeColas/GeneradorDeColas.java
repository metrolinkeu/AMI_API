package com.metrolink.ami_api.services.procesos.generadorDeColas;

import com.metrolink.ami_api.models.concentrador.Concentradores;
import com.metrolink.ami_api.models.medidor.Medidores;
import com.metrolink.ami_api.services.concentrador.ConcentradoresService;
import com.metrolink.ami_api.services.medidor.MedidoresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Service
public class GeneradorDeColas {

    @Autowired
    private ConcentradoresService concentradoresService;

    @Autowired
    private MedidoresService medidoresService;

    private final Map<String, BlockingQueue<Pair<Callable<?>, CompletableFuture<?>>>> colasPorDireccion = new ConcurrentHashMap<>();
    private final Map<String, ExecutorService> procesadoresPorDireccion = new ConcurrentHashMap<>();

    private String obtenerClave(String ip, String puerto) {
        return ip + ":" + puerto;
    }

    public <T> CompletableFuture<T> encolarSolicitud(String Serie, Callable<T> tarea) {
        String nSerie = "";

        if (Serie.startsWith("C_")) {
            nSerie = Serie.substring(2); // Extrae el número después de "C_"
            System.out.println("La serie empieza con C_. Número extraído: " + nSerie);

            Concentradores concentrador = concentradoresService.findById(nSerie);
            if (concentrador == null) {
                throw new RuntimeException("Concentrador no encontrado para vcnoSerie: " + nSerie);
            }

            return procesarTarea(tarea, concentrador.getParamTiposDeComunicacion().getVcip(),
                                 concentrador.getParamTiposDeComunicacion().getVcpuerto());

        } else if (Serie.startsWith("M_")) {
            nSerie = Serie.substring(2); // Extrae el número después de "M_"
            System.out.println("La serie empieza con M_. Número extraído: " + nSerie);

            Medidores medidor = medidoresService.findById(nSerie);
            if (medidor == null) {
                throw new RuntimeException("Medidor no encontrado para vcnoSerie: " + nSerie);
            }

            return procesarTarea(tarea, medidor.getVcip(), medidor.getVcpuerto());

        } else {
            System.out.println("La serie no empieza ni con C_ ni con M_.");
            return CompletableFuture.completedFuture(null);
        }
    }

    private <T> CompletableFuture<T> procesarTarea(Callable<T> tarea, String ip, String puerto) {
        String clave = obtenerClave(ip, puerto);

        // Inicializar la cola y el procesador para la clave si no existen
        colasPorDireccion.computeIfAbsent(clave, k -> new LinkedBlockingQueue<>());
        procesadoresPorDireccion.computeIfAbsent(clave, k -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> procesarCola(clave));
            return executor;
        });

        CompletableFuture<T> future = new CompletableFuture<>();
        BlockingQueue<Pair<Callable<?>, CompletableFuture<?>>> cola = colasPorDireccion.get(clave);

        // Añadir la tarea y el CompletableFuture a la cola como un par
        cola.offer(new Pair<>(tarea, future));

        return future;
    }

    private void procesarCola(String clave) {
        BlockingQueue<Pair<Callable<?>, CompletableFuture<?>>> cola = colasPorDireccion.get(clave);
        while (true) {
            try {
                Pair<Callable<?>, CompletableFuture<?>> pair = cola.take();
                Callable<?> tarea = pair.getKey();
                CompletableFuture<?> future = pair.getValue();

                try {
                    Object result = tarea.call();
                    completarFuture(future, result);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void completarFuture(CompletableFuture<?> future, T result) {
        ((CompletableFuture<T>) future).complete(result);
    }

    // Clase Pair para almacenar pares de Callable y CompletableFuture
    private static class Pair<K, V> {
        private final K key;
        private final V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }
}

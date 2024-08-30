package com.metrolink.ami_api.services.procesos.generadorDeColas;

import com.metrolink.ami_api.models.concentrador.Concentradores;
import com.metrolink.ami_api.services.concentrador.ConcentradoresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Service
public class GeneradorDeColas {

    @Autowired
    private ConcentradoresService concentradoresService;

    // Cambiar la cola para almacenar Pares de Callable y CompletableFuture usando generics
    private final Map<String, BlockingQueue<Pair<Callable<?>, CompletableFuture<?>>>> colasPorDireccion = new ConcurrentHashMap<>();
    private final Map<String, ExecutorService> procesadoresPorDireccion = new ConcurrentHashMap<>();

    private String obtenerClave(String ip, String puerto) {
        return ip + ":" + puerto;
    }

    public <T> CompletableFuture<T> encolarSolicitud(String vcnoSerie, Callable<T> tarea) {
        Concentradores concentrador = concentradoresService.findById(vcnoSerie);

        if (concentrador != null) {
            String ip = concentrador.getParamTiposDeComunicacion().getVcip();
            String puerto = concentrador.getParamTiposDeComunicacion().getVcpuerto();
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
        } else {
            throw new RuntimeException("Concentrador no encontrado para vcnoSerie: " + vcnoSerie);
        }
    }

    private void procesarCola(String clave) {
        BlockingQueue<Pair<Callable<?>, CompletableFuture<?>>> cola = colasPorDireccion.get(clave);
        while (true) {
            try {
                Pair<Callable<?>, CompletableFuture<?>> pair = cola.take();
                Callable<?> tarea = pair.getKey();
                CompletableFuture<?> future = pair.getValue();

                try {
                    // Ejecutar la tarea y obtener el resultado
                    Object result = tarea.call();
                    // Completar el CompletableFuture con el resultado de la tarea
                    completarFuture(future, result);
                } catch (Exception e) {
                    // Si ocurre una excepción, completar el future excepcionalmente
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

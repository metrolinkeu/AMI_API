package com.metrolink.ami_api.services.concentrador.hilo_colaCompartidaConcentrador;

import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.CompletableFuture;

@Component
public class SharedTaskQueueConc {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor(); // Un único hilo para procesar las tareas
    private final LinkedBlockingQueue<CompletableFutureTask<?>> requestQueue = new LinkedBlockingQueue<>(); // Cola compartida para las tareas

    public SharedTaskQueueConc() {
        executorService.submit(() -> {
            while (true) {
                try {
                    CompletableFutureTask<?> task = requestQueue.take();
                    Object result = task.getTask().processRequest(task.getJson());

                    // Casting the result to the expected type
                    @SuppressWarnings("unchecked")
                    CompletableFuture<Object> future = (CompletableFuture<Object>) task.getFuture();
                    //System.out.println(result);
                    future.complete(result);

                    Thread.sleep(10); // Pausa opcional
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    public void submitTask(CompletableFutureTask<?> task) {
        requestQueue.offer(task);
    }
    
    // Clase auxiliar para encapsular la tarea y el JSON, con soporte para tipos genéricos
    public static class CompletableFutureTask<T> {
        private final CompletableFuture<T> future;
        private final String json;
        private final TaskProcessor<T> task;

        public CompletableFutureTask(CompletableFuture<T> future, String json, TaskProcessor<T> task) {
            this.future = future;
            this.json = json;
            this.task = task;
        }

        public CompletableFuture<T> getFuture() {
            return future;
        }

        public String getJson() {
            return json;
        }

        public TaskProcessor<T> getTask() {
            return task;
        }
    }

    // Interfaz para las clases que procesarán las tareas, con soporte para tipos genéricos
    public interface TaskProcessor<T> {
        T processRequest(String json);
    }
}

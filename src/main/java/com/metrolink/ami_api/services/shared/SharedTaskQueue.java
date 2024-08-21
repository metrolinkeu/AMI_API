package com.metrolink.ami_api.services.shared;

import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.CompletableFuture;

@Component
public class SharedTaskQueue {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor(); // Un único hilo para procesar las tareas
    private final LinkedBlockingQueue<CompletableFutureTask> requestQueue = new LinkedBlockingQueue<>(); // Cola compartida para las tareas

    public SharedTaskQueue() {
        executorService.submit(() -> {
            while (true) {
                try {
                    CompletableFutureTask task = requestQueue.take();
                    String result = task.getTask().processRequest(task.getJson());
                    System.out.println("Este es el Result" + result);
                    task.getFuture().complete(result);
                    Thread.sleep(10); // Pausa opcional
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    public void submitTask(CompletableFutureTask task) {
        requestQueue.offer(task);
    }
    
    // Clase auxiliar para encapsular la tarea y el JSON
    public static class CompletableFutureTask {
        private final CompletableFuture<String> future;
        private final String json;
        private final TaskProcessor task;

        public CompletableFutureTask(CompletableFuture<String> future, String json, TaskProcessor task) {
            this.future = future;
            this.json = json;
            this.task = task;
        }

        public CompletableFuture<String> getFuture() {
            return future;
        }

        public String getJson() {
            return json;
        }

        public TaskProcessor getTask() {
            return task;
        }
    }

    // Interfaz para las clases que procesarán las tareas
    public interface TaskProcessor {
        String processRequest(String json);
    }
}

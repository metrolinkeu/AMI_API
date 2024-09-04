package com.metrolink.ami_api.controllers.procesos.autoconfiguracion;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
public class AutoConfiguracionControllerTest2 {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testSimultaneousRequests() throws InterruptedException, ExecutionException {
        int numRequests = 2; // Número de solicitudes simultáneas
        List<CompletableFuture<MvcResult>> futures = new ArrayList<>(numRequests);
        // Crear solicitudes simultáneas
        IntStream.range(0, numRequests).forEach(i -> {
            futures.add(CompletableFuture.supplyAsync(() -> {
                try {
                    String jsonRequest = "{"
                            + "\"vcnoSerie\": \"1717\","
                            + "\"vcseriales\": {"
                            + "\"vcserie1\": \"27682\","
                            + "\"vcserie2\": \"70316\","
                            + "\"vcserie3\": \"61144\""
                            + "}"
                            + "}";

                    return mockMvc.perform(get("/api/autoconfiguracion/ObtenerConfig")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest))
                            .andExpect(status().isOk())
                            .andReturn();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }));
        });
        // Esperar a que todas las solicitudes se completen y verificar resultados
        for (CompletableFuture<MvcResult> future : futures) {
            MvcResult result = future.get(); // Esto bloquea hasta que cada solicitud se complete
            String responseContent;
            try {
                responseContent = result.getResponse().getContentAsString();
                // Aquí podrías añadir más aserciones según lo que esperes en la respuesta
                assertEquals(200, result.getResponse().getStatus());
                System.out.println("Response: " + responseContent);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}

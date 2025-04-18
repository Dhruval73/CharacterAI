package com.vaaniai.characterai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient.Builder webClientBuilder;

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";

    public String getResponse(String prompt) {
        WebClient webClient = webClientBuilder.build();

        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{ Map.of("text", prompt) })
                }
        );

        Mono<Map> responseMono = webClient.post()
                .uri(GEMINI_URL + apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class);

        Map response = responseMono.block();

        try {
            return ((Map)((Map)((java.util.List<?>) response.get("candidates")).get(0)).get("content"))
                    .get("parts").toString()
                    .replaceAll("^\\[\\{text=", "")
                    .replaceAll("}]$", "");
        } catch (Exception e) {
            return "Sorry, I couldn't generate a response.";
        }
    }
}

package com.example.isap.service;

import com.example.isap.model.Act;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

public class OpenAiSummaryService {
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(60);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public OpenAiSummaryService(String apiKey, String model) {
        this.apiKey = apiKey;
        this.model = model;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public Optional<String> summarizeAct(Act act, String sourceText) {
        if (apiKey == null || apiKey.isBlank()) {
            return Optional.of("Skonfiguruj klucz API OpenAI w pliku application.properties, aby generować streszczenia.");
        }
        if (sourceText == null || sourceText.isBlank()) {
            return Optional.of("Brak treści dokumentu. Pobierz plik z ISAP, aby wygenerować streszczenie.");
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .timeout(REQUEST_TIMEOUT)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(buildPayload(act, sourceText), StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return Optional.ofNullable(parseSummary(response.body()));
            }
            return Optional.of("Nie udało się wygenerować streszczenia: otrzymano kod " + response.statusCode());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.of("Przerwano oczekiwanie na odpowiedź API OpenAI.");
        } catch (IOException e) {
            return Optional.of("Błąd komunikacji z API OpenAI: " + e.getMessage());
        }
    }

    private String buildPayload(Act act, String sourceText) throws IOException {
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("model", model);
        ArrayNode messages = payload.putArray("messages");
        ObjectNode system = messages.addObject();
        system.put("role", "system");
        system.put("content", "Jesteś asystentem, który tworzy zwięzłe (1-4 zdania) streszczenia projektów ustaw z Monitora Polskiego.");
        ObjectNode user = messages.addObject();
        user.put("role", "user");
        user.put("content", buildPrompt(act, sourceText));
        payload.set("messages", messages);
        payload.put("max_tokens", 250);
        payload.put("temperature", 0.2);
        return objectMapper.writeValueAsString(payload);
    }

    private String buildPrompt(Act act, String sourceText) {
        StringBuilder builder = new StringBuilder();
        builder.append("Tytuł: ").append(act.getTitle()).append('\n');
        if (act.getPromulgationDate() != null) {
            builder.append("Data ogłoszenia: ").append(act.getPromulgationDate()).append('\n');
        }
        builder.append("Treść:")
                .append('\n')
                .append(sourceText)
                .append('\n')
                .append("Proszę przygotuj 1-4 zwięzłe zdania podsumowujące kluczowy cel dokumentu oraz potencjalne skutki.");
        return builder.toString();
    }

    private String parseSummary(String responseBody) throws IOException {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode choices = root.path("choices");
        if (!choices.isArray() || choices.isEmpty()) {
            return null;
        }
        JsonNode message = choices.get(0).path("message");
        return message.path("content").asText(null);
    }
}

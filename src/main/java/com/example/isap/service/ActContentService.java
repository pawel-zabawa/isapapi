package com.example.isap.service;

import com.example.isap.model.Act;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class ActContentService {
    private static final String TEXT_ENDPOINT_PATTERN = "https://api.sejm.gov.pl/eli/acts/%s/%d/%d/text";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public String fetchPlainText(Act act) {
        if (act == null) {
            return null;
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format(TEXT_ENDPOINT_PATTERN, act.getPublisherOrDefault(), act.getYear(), act.getPosition())))
                    .GET()
                    .header("Accept", "text/plain")
                    .timeout(Duration.ofSeconds(30))
                    .build();
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return new String(response.body(), StandardCharsets.UTF_8);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            return null;
        }
        return null;
    }
}

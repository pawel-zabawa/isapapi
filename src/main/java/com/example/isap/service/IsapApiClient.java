package com.example.isap.service;

import com.example.isap.model.Act;
import com.example.isap.model.Act.DocumentFile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class IsapApiClient {
    private static final String BASE_URL = "https://api.sejm.gov.pl/eli/acts";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public IsapApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public List<Act> fetchActs(String publisher, int year, int limit) throws IOException, InterruptedException {
        String url = String.format("%s/%s/%d?limit=%d", BASE_URL, publisher, year, limit);
        JsonNode root = executeRequest(url);
        if (root == null || !root.has("items")) {
            return List.of();
        }
        List<Act> acts = new ArrayList<>();
        for (JsonNode node : root.get("items")) {
            Optional<Act> act = enrichActDetails(node);
            act.ifPresent(acts::add);
        }
        return acts;
    }

    private Optional<Act> enrichActDetails(JsonNode listItem) throws IOException, InterruptedException {
        int year = listItem.path("year").asInt();
        int position = listItem.path("pos").asInt();
        String publisher = listItem.path("publisher").asText();
        if (year == 0 || position == 0 || publisher == null || publisher.isBlank()) {
            return Optional.empty();
        }
        String detailUrl = String.format("%s/%s/%d/%d", BASE_URL, publisher, year, position);
        JsonNode detail = executeRequest(detailUrl);
        if (detail == null || detail.isMissingNode()) {
            return Optional.empty();
        }
        String eli = detail.path("ELI").asText(null);
        String title = detail.path("title").asText(listItem.path("title").asText(null));
        String status = detail.path("status").asText(listItem.path("status").asText(""));
        String inForce = detail.path("inForce").asText(listItem.path("inForce").asText(""));
        String type = detail.path("type").asText(listItem.path("type").asText(""));
        LocalDate promulgationDate = parseDate(detail.path("promulgation").asText(null));
        List<String> keywords = new ArrayList<>();
        if (detail.has("keywords")) {
            detail.get("keywords").forEach(node -> keywords.add(node.asText()));
        }
        List<DocumentFile> documents = extractDocuments(detail);
        return Optional.of(new Act(eli, title, publisher, status, inForce, type, year, position, promulgationDate, keywords, documents));
    }

    private List<DocumentFile> extractDocuments(JsonNode detail) {
        String address = detail.path("address").asText(null);
        if (!detail.has("texts")) {
            return List.of();
        }
        Set<String> seen = new HashSet<>();
        List<DocumentFile> documents = new ArrayList<>();
        for (JsonNode textNode : detail.get("texts")) {
            String type = textNode.path("type").asText(null);
            String fileName = textNode.path("fileName").asText(null);
            if (type == null || fileName == null) {
                continue;
            }
            String key = type + "|" + fileName;
            if (seen.add(key)) {
                documents.add(new DocumentFile(address, type, fileName));
            }
        }
        return documents;
    }

    private JsonNode executeRequest(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return objectMapper.readTree(response.body());
        }
        throw new IOException("ISAP API returned status " + response.statusCode() + " for URL " + url);
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value.substring(0, Math.min(value.length(), 10)));
        } catch (Exception ex) {
            return null;
        }
    }

    public List<String> collectKeywords(List<Act> acts) {
        return acts.stream()
                .flatMap(act -> act.getKeywords().stream())
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}

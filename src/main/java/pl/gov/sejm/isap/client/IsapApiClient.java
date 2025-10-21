package pl.gov.sejm.isap.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.gov.sejm.isap.config.IsapApiProperties;
import pl.gov.sejm.isap.model.LegalActMetadata;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * High-level client for the ISAP REST API.
 */
public class IsapApiClient {
    private static final String DEFAULT_ACCEPT_HEADER = "application/json";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final IsapApiProperties properties;

    public IsapApiClient(IsapApiProperties properties) {
        this.properties = properties;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(properties.requestTimeout())
            .build();
        this.objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<LegalActMetadata> fetchLatestActs(int limit)
        throws IOException, InterruptedException {
        URI uri = properties.baseUrl().resolve("eli/acts/latest?limit=" + limit);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(uri)
            .timeout(properties.requestTimeout())
            .header("Accept", DEFAULT_ACCEPT_HEADER)
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new IsapApiException("Failed to fetch latest acts", response.statusCode(), response.body());
        }

        return objectMapper.readValue(response.body(), objectMapper.getTypeFactory()
            .constructCollectionType(List.class, LegalActMetadata.class));
    }
}

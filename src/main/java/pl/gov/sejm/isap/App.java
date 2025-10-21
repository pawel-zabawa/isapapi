package pl.gov.sejm.isap;

import pl.gov.sejm.isap.client.IsapApiClient;
import pl.gov.sejm.isap.config.IsapApiProperties;
import pl.gov.sejm.isap.model.LegalActMetadata;

import java.io.IOException;
import java.util.List;

/**
 * Entry point for the ISAP API integration sample application.
 */
public final class App {
    private App() {
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        IsapApiProperties properties = IsapApiProperties.builder().build();
        IsapApiClient client = new IsapApiClient(properties);

        List<LegalActMetadata> latestActs = client.fetchLatestActs(5);
        latestActs.forEach(act -> System.out.printf("%s (%s)\n", act.title(), act.publishedDate()));
    }
}

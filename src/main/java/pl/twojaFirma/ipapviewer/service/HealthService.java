package pl.twojaFirma.ipapviewer.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HealthService {

    private final Map<String, Instant> lastChecks = new ConcurrentHashMap<>();

    public Duration sinceLastCheck(String target) {
        Instant last = lastChecks.get(target);
        return last == null ? Duration.ofDays(1) : Duration.between(last, Instant.now());
    }

    public void markCheck(String target) {
        lastChecks.put(target, Instant.now());
    }
}

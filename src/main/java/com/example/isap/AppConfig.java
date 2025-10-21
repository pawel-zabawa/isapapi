package com.example.isap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Simple application configuration loader based on the {@code application.properties} file.
 */
public class AppConfig {
    private final Properties properties = new Properties();

    public AppConfig() {
        this(loadFromResource());
    }

    AppConfig(Properties properties) {
        if (properties != null) {
            this.properties.putAll(properties);
        }
    }

    public String getOpenAiApiKey() {
        return properties.getProperty("openai.apiKey", "").trim();
    }

    public String getOpenAiModel() {
        return properties.getProperty("openai.model", "gpt-4.1-mini");
    }

    public String getPublisher() {
        return properties.getProperty("isap.publisher", "MP");
    }

    public int getYearOrDefault() {
        String yearValue = properties.getProperty("isap.year");
        if (yearValue == null || yearValue.isBlank()) {
            return java.time.Year.now().getValue();
        }
        try {
            return Integer.parseInt(yearValue.trim());
        } catch (NumberFormatException ex) {
            return java.time.Year.now().getValue();
        }
    }

    public int getLimit() {
        String limitValue = properties.getProperty("isap.limit", "30");
        if (limitValue == null) {
            return 30;
        }
        String trimmed = limitValue.trim();
        if (trimmed.isEmpty()) {
            return 30;
        }
        try {
            int parsed = Integer.parseInt(trimmed);
            return parsed > 0 ? parsed : 30;
        } catch (NumberFormatException ex) {
            return 30;
        }
    }

    private static Properties loadFromResource() {
        Properties loaded = new Properties();
        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                loaded.load(input);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load application.properties", e);
        }
        return loaded;
    }
}

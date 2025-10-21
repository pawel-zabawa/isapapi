package com.example.isap;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppConfigTest {

    @Test
    void returnsTrimmedPositiveLimit() {
        Properties properties = new Properties();
        properties.setProperty("isap.limit", " 42 ");
        AppConfig config = new AppConfig(properties);

        assertEquals(42, config.getLimit());
    }

    @Test
    void returnsDefaultLimitWhenBlank() {
        Properties properties = new Properties();
        properties.setProperty("isap.limit", "   ");
        AppConfig config = new AppConfig(properties);

        assertEquals(30, config.getLimit());
    }

    @Test
    void returnsDefaultLimitWhenNonNumeric() {
        Properties properties = new Properties();
        properties.setProperty("isap.limit", "abc");
        AppConfig config = new AppConfig(properties);

        assertEquals(30, config.getLimit());
    }

    @Test
    void returnsDefaultLimitWhenNonPositive() {
        Properties properties = new Properties();
        properties.setProperty("isap.limit", "0");
        AppConfig config = new AppConfig(properties);

        assertEquals(30, config.getLimit());
    }
}

package pl.twojaFirma.ipapviewer.service;

import pl.twojaFirma.ipapviewer.model.Summary;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Stubbed OpenAI client. Generates pseudo summaries to keep the UI interactive during development.
 */
public class OpenAiClient {

    public Summary summarize(String actId, String modelVersion, String language, String content) {
        String text = "[" + modelVersion + "] Podsumowanie aktu " + actId + " (" + language + ")\n"
                + content.substring(0, Math.min(content.length(), 120))
                + "...\nWygenerowano: " + DateTimeFormatter.ISO_INSTANT.format(Instant.now());
        return new Summary(actId, modelVersion, language, text, Instant.now());
    }
}

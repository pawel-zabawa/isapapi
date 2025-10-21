package pl.twojaFirma.ipapviewer.model;

import java.time.Instant;

public record Summary(
        String actId,
        String modelVersion,
        String language,
        String text,
        Instant createdAt
) {
}

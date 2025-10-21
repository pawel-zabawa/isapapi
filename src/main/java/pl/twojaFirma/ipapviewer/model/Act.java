package pl.twojaFirma.ipapviewer.model;

import java.time.LocalDate;

public record Act(
        String id,
        String title,
        LocalDate date,
        String categoryId,
        String content
) {
}

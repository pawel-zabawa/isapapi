package pl.gov.sejm.isap.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * Minimal representation of legal act metadata exposed by the ISAP API.
 */
public record LegalActMetadata(
    @JsonProperty("identifier") String identifier,
    @JsonProperty("title") String title,
    @JsonProperty("publication_date") LocalDate publishedDate,
    @JsonProperty("journal_no") String journalNumber,
    @JsonProperty("url_pdf") String pdfUrl
) {
}

package pl.twojaFirma.ipapviewer.service;

import org.junit.jupiter.api.Test;
import pl.twojaFirma.ipapviewer.model.Summary;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class SummaryServiceTest {

    @Test
    void shouldCacheSummariesByActAndLocale() {
        OpenAiClient openAiClient = new OpenAiClient();
        SummaryService service = new SummaryService(openAiClient);

        Summary first = service.getSummary("act-1", "Lorem ipsum", Locale.forLanguageTag("pl-PL"));
        Summary second = service.getSummary("act-1", "Lorem ipsum", Locale.forLanguageTag("pl-PL"));

        assertThat(second).isSameAs(first);
    }
}

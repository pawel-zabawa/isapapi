package pl.twojaFirma.ipapviewer.service;

import com.github.benmanes.caffeine.cache.Cache;
import pl.twojaFirma.ipapviewer.config.AppConfig;
import pl.twojaFirma.ipapviewer.infra.CacheConfig;
import pl.twojaFirma.ipapviewer.model.Summary;

import java.util.Locale;
import java.util.Optional;

public class SummaryService {

    private final OpenAiClient openAiClient;
    private final Cache<String, Object> cache;
    private final AppConfig config = AppConfig.getInstance();

    public SummaryService(OpenAiClient openAiClient) {
        this.openAiClient = openAiClient;
        this.cache = CacheConfig.createSummaryCache();
    }

    public Summary getSummary(String actId, String content, Locale locale) {
        String key = cacheKey(actId, locale);
        Summary cached = (Summary) cache.getIfPresent(key);
        if (cached != null) {
            return cached;
        }
        Summary summary = openAiClient.summarize(actId, config.openAiModel(), locale.toLanguageTag(), content);
        cache.put(key, summary);
        return summary;
    }

    public Optional<Summary> peek(String actId, Locale locale) {
        return Optional.ofNullable((Summary) cache.getIfPresent(cacheKey(actId, locale)));
    }

    public void invalidate(String actId, Locale locale) {
        cache.invalidate(cacheKey(actId, locale));
    }

    public void clear() {
        cache.invalidateAll();
    }

    private String cacheKey(String actId, Locale locale) {
        return actId + ":" + config.openAiModel() + ":" + locale.toLanguageTag();
    }
}

package pl.twojaFirma.ipapviewer.infra;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import pl.twojaFirma.ipapviewer.config.AppConfig;

import java.time.Duration;

public final class CacheConfig {

    private static final AppConfig CONFIG = AppConfig.getInstance();

    private CacheConfig() {
    }

    public static Cache<String, Object> createCategoryCache() {
        return newCache(CONFIG.ipapCategoryCacheSpec());
    }

    public static Cache<String, Object> createActCache() {
        return newCache(CONFIG.ipapActsCacheSpec());
    }

    public static Cache<String, Object> createSummaryCache() {
        return newCache(CONFIG.summariesCacheSpec());
    }

    private static Cache<String, Object> newCache(String spec) {
        String[] tokens = spec.split(",");
        Caffeine<Object, Object> builder = Caffeine.newBuilder();
        for (String token : tokens) {
            String trimmed = token.trim();
            if (trimmed.startsWith("maximumSize")) {
                long size = Long.parseLong(trimmed.substring(trimmed.indexOf('=') + 1));
                builder = builder.maximumSize(size);
            }
            if (trimmed.startsWith("expireAfterWrite")) {
                String duration = trimmed.substring(trimmed.indexOf('=') + 1);
                builder = builder.expireAfterWrite(Duration.parse(duration));
            }
        }
        return builder.build();
    }
}

package pl.twojaFirma.ipapviewer.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;

/**
 * Application configuration wrapper providing typed access to values defined in application.conf.
 */
public final class AppConfig {

    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);
    private static final AppConfig INSTANCE = new AppConfig();

    private final Config config;

    private AppConfig() {
        Config defaults = ConfigFactory.parseResources("application.conf");
        this.config = ConfigFactory.systemProperties()
                .withFallback(ConfigFactory.systemEnvironment())
                .withFallback(defaults)
                .resolve();
        log.info("Configuration loaded. Active profiles: {}", config.getStringList("app.profiles"));
    }

    public static AppConfig getInstance() {
        return INSTANCE;
    }

    public String ipapBaseUrl() {
        return config.getString("ipap.baseUrl");
    }

    public Duration ipapTimeout() {
        return config.getDuration("ipap.timeout");
    }

    public String openAiBaseUrl() {
        return config.getString("openai.baseUrl");
    }

    public Duration openAiTimeout() {
        return config.getDuration("openai.timeout");
    }

    public String openAiModel() {
        return config.getString("openai.model");
    }

    public Optional<Path> keyStorePath() {
        if (config.hasPath("security.keyStore.path")) {
            return Optional.of(Path.of(config.getString("security.keyStore.path")));
        }
        return Optional.empty();
    }

    public String ipapCategoryCacheSpec() {
        return config.getString("cache.categories");
    }

    public String ipapActsCacheSpec() {
        return config.getString("cache.acts");
    }

    public String summariesCacheSpec() {
        return config.getString("cache.summaries");
    }
}

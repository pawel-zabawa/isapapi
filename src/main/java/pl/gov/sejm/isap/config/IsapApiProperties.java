package pl.gov.sejm.isap.config;

import java.net.URI;
import java.time.Duration;
import java.util.Objects;

/**
 * Application level configuration for talking with the ISAP API.
 */
public final class IsapApiProperties {
    private static final URI DEFAULT_BASE_URL = URI.create("https://api.sejm.gov.pl/sejm/ISAP/");
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(20);

    private final URI baseUrl;
    private final Duration requestTimeout;

    private IsapApiProperties(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.requestTimeout = builder.requestTimeout;
    }

    public URI baseUrl() {
        return baseUrl;
    }

    public Duration requestTimeout() {
        return requestTimeout;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private URI baseUrl = DEFAULT_BASE_URL;
        private Duration requestTimeout = DEFAULT_TIMEOUT;

        private Builder() {
        }

        public Builder baseUrl(URI baseUrl) {
            this.baseUrl = Objects.requireNonNull(baseUrl, "baseUrl");
            return this;
        }

        public Builder requestTimeout(Duration requestTimeout) {
            this.requestTimeout = Objects.requireNonNull(requestTimeout, "requestTimeout");
            return this;
        }

        public IsapApiProperties build() {
            return new IsapApiProperties(this);
        }
    }
}

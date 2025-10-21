package pl.twojaFirma.ipapviewer.infra;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;

import java.time.Duration;
import java.util.function.Supplier;

public final class HttpUtil {

    private static final Retry RETRY = RetryRegistry.of(RetryConfig.custom()
            .maxAttempts(5)
            .waitDuration(Duration.ofMillis(250))
            .retryExceptions(RuntimeException.class)
            .build()).retry("http");

    private HttpUtil() {
    }

    public static <T> T executeWithRetry(Supplier<T> supplier) {
        return Retry.decorateSupplier(RETRY, supplier).get();
    }
}

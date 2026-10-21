package dev.searchengine.common;

import java.net.URI;
import java.util.Locale;

public final class Urls {

    private Urls() {
    }

    /**
     * The Kafka message key for a URL. Keying by host means every URL of one site goes to the
     * same partition, so a single consumer owns that site (needed later for per-domain rate limits).
     */
    public static String host(String url) {
        String host = URI.create(url).getHost();
        if (host == null) {
            throw new IllegalArgumentException("URL has no host: " + url);
        }
        return host.toLowerCase(Locale.ROOT);
    }
}

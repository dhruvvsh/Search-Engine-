package dev.searchengine.fetcher;

import dev.searchengine.common.RawPage;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Downloads one URL. Knows nothing about Kafka. */
@Component
public class PageFetcher {

    static final String USER_AGENT = "SearchEngineLearningBot/0.1";

    /** Kafka's default max message size is ~1 MB, so we cap the HTML we forward. */
    private static final int MAX_HTML_CHARS = 500_000;

    private final HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    /** Returns empty when the response is not an HTML page (images, PDFs, ...). */
    public Optional<RawPage> fetch(String url, int depth) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("User-Agent", USER_AGENT)
                .header("Accept", "text/html")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String contentType = response.headers().firstValue("Content-Type").orElse("");
        if (!contentType.toLowerCase().startsWith("text/html")) {
            return Optional.empty();
        }

        String html = response.body();
        if (html.length() > MAX_HTML_CHARS) {
            html = html.substring(0, MAX_HTML_CHARS);
        }

        return Optional.of(new RawPage(
                response.uri().toString(), depth, response.statusCode(), contentType, html, Instant.now()));
    }
}

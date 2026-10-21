package dev.searchengine.common;

/**
 * Kafka topic names. The pipeline is:
 * url-frontier -> [fetcher] -> raw-pages -> [parser] -> parsed-docs -> [indexer]
 */
public final class Topics {

    /** URLs waiting to be fetched. Key = host, so one domain always lands on one partition. */
    public static final String URL_FRONTIER = "url-frontier";

    /** Fetched HTML, waiting to be parsed. Key = host. */
    public static final String RAW_PAGES = "raw-pages";

    /** Extracted title/text/links, waiting to be indexed. */
    public static final String PARSED_DOCS = "parsed-docs";

    private Topics() {
    }
}

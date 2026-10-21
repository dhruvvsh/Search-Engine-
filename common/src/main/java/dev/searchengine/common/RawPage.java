package dev.searchengine.common;

import java.time.Instant;

/**
 * A message on {@link Topics#RAW_PAGES}: the result of fetching a {@link CrawlTask}.
 *
 * @param url         the URL after following redirects
 * @param depth       copied from the CrawlTask
 * @param statusCode  HTTP status
 * @param contentType value of the Content-Type header
 * @param html        response body
 * @param fetchedAt   when the fetch completed
 */
public record RawPage(String url, int depth, int statusCode, String contentType, String html, Instant fetchedAt) {
}

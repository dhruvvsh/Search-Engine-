package dev.searchengine.common;

/**
 * A message on {@link Topics#URL_FRONTIER}: "please fetch this URL".
 *
 * @param url   absolute http(s) URL
 * @param depth how many links away from a seed URL this is (seed = 0)
 */
public record CrawlTask(String url, int depth) {
}

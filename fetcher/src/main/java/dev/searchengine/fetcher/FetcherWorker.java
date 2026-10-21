package dev.searchengine.fetcher;

import dev.searchengine.common.CrawlTask;
import dev.searchengine.common.RawPage;
import dev.searchengine.common.Topics;
import dev.searchengine.common.Urls;
import java.io.IOException;
import java.util.Optional;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

/**
 * The worker loop: read a CrawlTask from url-frontier, download it, write a RawPage to raw-pages.
 * Messages are plain JSON strings so you can read them in Kafka UI.
 */
@Component
public class FetcherWorker {

    private static final Logger log = LoggerFactory.getLogger(FetcherWorker.class);

    private final PageFetcher pageFetcher;
    private final KafkaTemplate<String, String> kafka;
    private final JsonMapper json;

    public FetcherWorker(PageFetcher pageFetcher, KafkaTemplate<String, String> kafka, JsonMapper json) {
        this.pageFetcher = pageFetcher;
        this.kafka = kafka;
        this.json = json;
    }

    @KafkaListener(topics = Topics.URL_FRONTIER)
    public void onCrawlTask(ConsumerRecord<String, String> record) throws IOException, InterruptedException {
        CrawlTask task = json.readValue(record.value(), CrawlTask.class);
        log.info("Fetching {} (partition={}, offset={})", task.url(), record.partition(), record.offset());

        Optional<RawPage> page = pageFetcher.fetch(task.url(), task.depth());
        if (page.isEmpty()) {
            log.info("Skipping non-HTML {}", task.url());
            return;
        }

        // .join() waits until Kafka confirms the write. Only then does this method return and the
        // consumer offset get committed, so a crash in between re-fetches the URL instead of losing it.
        kafka.send(Topics.RAW_PAGES, Urls.host(page.get().url()), json.writeValueAsString(page.get())).join();
        log.info("Fetched {} -> {} ({} chars)", task.url(), page.get().statusCode(), page.get().html().length());
    }
}

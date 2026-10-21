package dev.searchengine.fetcher;

import dev.searchengine.common.CrawlTask;
import dev.searchengine.common.Topics;
import dev.searchengine.common.Urls;
import java.util.List;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.json.JsonMapper;

/** Kick-starts a crawl by putting seed URLs on url-frontier. */
@RestController
public class SeedController {

    record SeedRequest(List<String> urls) {
    }

    private final KafkaTemplate<String, String> kafka;
    private final JsonMapper json;

    public SeedController(KafkaTemplate<String, String> kafka, JsonMapper json) {
        this.kafka = kafka;
        this.json = json;
    }

    @PostMapping("/seed")
    public String seed(@RequestBody SeedRequest request) {
        for (String url : request.urls()) {
            kafka.send(Topics.URL_FRONTIER, Urls.host(url), json.writeValueAsString(new CrawlTask(url, 0)));
        }
        kafka.flush();
        return "Queued " + request.urls().size() + " URL(s)";
    }
}

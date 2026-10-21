package dev.searchengine.fetcher;

import dev.searchengine.common.Topics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Spring's KafkaAdmin creates these topics on startup if they don't exist yet.
 * Partitions = the max number of consumers that can work on a topic in parallel.
 */
@Configuration
public class KafkaTopicConfig {

    private static final int PARTITIONS = 6;

    @Bean
    NewTopic urlFrontier() {
        return TopicBuilder.name(Topics.URL_FRONTIER).partitions(PARTITIONS).replicas(1).build();
    }

    @Bean
    NewTopic rawPages() {
        return TopicBuilder.name(Topics.RAW_PAGES).partitions(PARTITIONS).replicas(1).build();
    }

    @Bean
    NewTopic parsedDocs() {
        return TopicBuilder.name(Topics.PARSED_DOCS).partitions(PARTITIONS).replicas(1).build();
    }
}

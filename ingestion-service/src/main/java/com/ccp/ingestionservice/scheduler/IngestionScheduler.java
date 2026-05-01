package com.ccp.ingestionservice.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Component
public class IngestionScheduler {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${external.threat.api.url}")
    private String threatUrl;

    @Scheduled(fixedDelay = 30000) // every 30 seconds
    public void fetchAndPublish() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String rawJson = restTemplate.getForObject(Objects.requireNonNull(threatUrl), String.class);
            kafkaTemplate.send("raw-data", rawJson);
            System.out.println("Published to raw-data: " + rawJson);
        } catch (Exception e) {
            System.err.println("Error fetching threats: " + e.getMessage());
        }
    }
}

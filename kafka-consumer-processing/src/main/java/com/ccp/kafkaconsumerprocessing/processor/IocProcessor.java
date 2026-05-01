package com.ccp.kafkaconsumerprocessing.processor;

import com.ccp.kafkaconsumerprocessing.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Objects;

@Component
public class IocProcessor {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private KafkaTemplate<String, EnrichedIoc> kafkaTemplate;

    @Value("${ranking.service.url}")
    private String rankingUrl;

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @KafkaListener(topics = "iocs", groupId = "processor-group")
    public void process(IocRequest ioc) {
        if (!isValid(ioc)) return;

        RankRequest rankReq = new RankRequest(ioc.getValue(), ioc.getType());
        RankResponse response = restTemplate.postForObject(Objects.requireNonNull(rankingUrl), rankReq, RankResponse.class);
        double severity = (response != null) ? response.getScore() : 0.0;

        EnrichedIoc enriched = new EnrichedIoc(ioc.getType(), ioc.getValue(), severity, Instant.now());
        kafkaTemplate.send("enriched-iocs", enriched);
        System.out.println("Processed IOC: " + enriched.getValue() + " severity=" + severity);
    }

    private boolean isValid(IocRequest ioc) {
        if ("ip".equals(ioc.getType()))
            return ioc.getValue().matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
        if ("domain".equals(ioc.getType()))
            return ioc.getValue().matches(".*\\..*");
        return false;
    }
}

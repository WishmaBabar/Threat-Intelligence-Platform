package com.ccp.extractionservice.consumer;

import com.ccp.extractionservice.dto.IocRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RawDataConsumer {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${producer.service.url}")
    private String producerUrl;

    private static final Pattern IP_PATTERN = Pattern.compile("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b");
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("\\b([a-z0-9-]+\\.)+[a-z]{2,}\\b", Pattern.CASE_INSENSITIVE);

    @KafkaListener(topics = "raw-data", groupId = "extraction-group")
    public void consume(String rawJson) {
        List<IocRequest> iocs = new ArrayList<>();
        Matcher ipMatcher = IP_PATTERN.matcher(rawJson);
        while (ipMatcher.find()) iocs.add(new IocRequest("ip", ipMatcher.group()));
        Matcher domainMatcher = DOMAIN_PATTERN.matcher(rawJson);
        while (domainMatcher.find()) iocs.add(new IocRequest("domain", domainMatcher.group()));

        if (!iocs.isEmpty()) {
            restTemplate.postForObject(Objects.requireNonNull(producerUrl), iocs, String.class);
            System.out.println("Extracted and sent " + iocs.size() + " IOCs");
        }
    }
}

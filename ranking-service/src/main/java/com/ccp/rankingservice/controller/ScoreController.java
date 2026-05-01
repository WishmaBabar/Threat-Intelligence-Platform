package com.ccp.rankingservice.controller;

import com.ccp.rankingservice.dto.RankRequest;
import com.ccp.rankingservice.dto.RankResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class ScoreController {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${external.ranking.api.url}")
    private String rankingUrl;

    @GetMapping("/score")
    public Map<String, String> health() {
        return Map.of("service", "ranking-service", "status", "running");
    }

    @PostMapping("/score")
    public RankResponse score(@RequestBody RankRequest request) {
        return restTemplate.postForObject(Objects.requireNonNull(rankingUrl), request, RankResponse.class);
    }
}

package com.ccp.mockrankingapi.controller;

import com.ccp.mockrankingapi.model.RankRequest;
import com.ccp.mockrankingapi.model.RankResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class RankController {
    @GetMapping("/rank")
    public Map<String, String> health() {
        return Map.of("service", "mock-ranking-api", "status", "running");
    }

    @PostMapping("/rank")
    public RankResponse rank(@RequestBody RankRequest request) {
        double score = ThreadLocalRandom.current().nextDouble() * 10;
        return new RankResponse(score);
    }
}

package com.ccp.mockthreatapi.controller;

import com.ccp.mockthreatapi.model.Threat;
import com.ccp.mockthreatapi.service.ThreatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class ThreatController {
    
    @Autowired
    private ThreatService threatService;
    
    /**
     * Fetch threats from configured sources
     * Returns both AlienVault and AbuseIPDB threat data based on configuration
     */
    @GetMapping("/threats")
    public List<Threat> getThreats() {
        return threatService.fetchThreats();
    }
    
    /**
     * Health and status check endpoint
     * Shows which threat sources are configured and active
     */
    @GetMapping("/status")
    public Map<String, String> getStatus() {
        Map<String, String> status = new HashMap<>();
        status.put("service", "mock-threat-api");
        status.put("status", "running");
        status.put("threat_sources", threatService.getSourceMode());
        return status;
    }
}

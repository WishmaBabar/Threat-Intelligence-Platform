package com.ccp.mockthreatapi.controller;

import com.ccp.mockthreatapi.model.Threat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class ThreatController {
    @GetMapping("/threats")
    public List<Threat> getThreats() {
        return List.of(
                new Threat("1", "AlienVault", "Suspicious IP 192.168.1.100 and evil.com detected"),
                new Threat("2", "AbuseIPDB", "Domain malware.xyz and 10.0.0.5 are malicious")
        );
    }
}

package com.ccp.mockthreatapi.service;

import com.ccp.mockthreatapi.model.Threat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ThreatService {
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${threat.source.mode:mock}")
    private String sourceMode; // "mock", "real", or "hybrid"
    
    @Value("${abuseipdb.api.key:}")
    private String abuseIPDBKey;
    
    @Value("${alienvault.api.key:}")
    private String alienVaultKey;
    
    /**
     * Fetch threats from configured sources
     * Mode: "mock" = simulated data only
     *       "real" = real API calls only (requires keys)
     *       "hybrid" = real APIs with mock fallback
     */
    public List<Threat> fetchThreats() {
        List<Threat> threats = new ArrayList<>();
        
        switch (sourceMode.toLowerCase()) {
            case "real":
                // Fetch from real APIs only
                threats.addAll(fetchFromAbuseIPDB());
                threats.addAll(fetchFromAlienVault());
                break;
                
            case "hybrid":
                // Try real APIs, fallback to mock
                threats.addAll(fetchFromAbuseIPDB());
                threats.addAll(fetchFromAlienVault());
                if (threats.isEmpty()) {
                    threats.addAll(getMockThreats());
                }
                break;
                
            case "mock":
            default:
                // Use mock data only
                threats.addAll(getMockThreats());
                break;
        }
        
        return threats;
    }
    
    /**
     * Fetch from AbuseIPDB API
     * API Endpoint: https://api.abuseipdb.com/api/v2/check
     */
    private List<Threat> fetchFromAbuseIPDB() {
        List<Threat> threats = new ArrayList<>();
        
        if (abuseIPDBKey == null || abuseIPDBKey.trim().isEmpty()) {
            System.out.println("[INFO] AbuseIPDB API key not configured, skipping.");
            return threats;
        }
        
        String[] suspiciousIPs = {
            "192.168.1.100",
            "10.0.0.5",
            "203.0.113.45",
            "198.51.100.200"
        };
        String url = "https://api.abuseipdb.com/api/v2/check";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Key", abuseIPDBKey);
        headers.set("Accept", "application/json");
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        
        for (String ip : suspiciousIPs) {
            try {
                String uri = UriComponentsBuilder.fromHttpUrl(url)
                        .queryParam("ipAddress", ip)
                        .queryParam("maxAgeInDays", 90)
                        .toUriString();

                ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, Map.class);
                Map<String, Object> body = response.getBody();
                if (body != null && body.containsKey("data")) {
                    Map<String, Object> data = (Map<String, Object>) body.get("data");
                    Object score = data.getOrDefault("abuseConfidenceScore", 0);
                    Object country = data.getOrDefault("countryCode", "unknown");
                    Object usage = data.getOrDefault("usageType", "unknown");
                    String threatData = String.format("IP %s AbuseIPDB score=%s country=%s usage=%s", ip, score, country, usage);
                    threats.add(new Threat(UUID.randomUUID().toString(), "AbuseIPDB", threatData));
                    System.out.println("[AbuseIPDB] " + threatData);
                } else {
                    String threatData = String.format("IP %s - AbuseIPDB returned no data", ip);
                    threats.add(new Threat(UUID.randomUUID().toString(), "AbuseIPDB", threatData));
                    System.out.println("[AbuseIPDB] No data for " + ip);
                }
            } catch (Exception e) {
                System.err.println("[AbuseIPDB Error] Failed to fetch IP: " + ip + " - " + e.getMessage());
            }
        }
        return threats;
    }
    
    /**
     * Fetch from AlienVault (OTX) API
     * API Endpoint: https://otx.alienvault.com/api/v1/
     */
    private List<Threat> fetchFromAlienVault() {
        List<Threat> threats = new ArrayList<>();
        
        if (alienVaultKey == null || alienVaultKey.trim().isEmpty()) {
            System.out.println("[INFO] AlienVault API key not configured, skipping.");
            return threats;
        }
        
        String[] suspiciousDomains = {
            "evil.com",
            "malware.xyz",
            "phishing-site.net",
            "botnet-c2.com"
        };
        String url = "https://otx.alienvault.com/api/v1/indicators/domain/{domain}/general";
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-OTX-API-KEY", alienVaultKey);
        headers.set("Accept", "application/json");
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        
        for (String domain : suspiciousDomains) {
            try {
                ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Map.class, domain);
                Map<String, Object> body = response.getBody();
                Object reputation = body != null ? body.getOrDefault("reputation", "unknown") : "unknown";
                Map<String, Object> pulseInfo = body != null && body.get("pulse_info") instanceof Map ? (Map<String, Object>) body.get("pulse_info") : null;
                Object pulseCount = pulseInfo != null ? pulseInfo.getOrDefault("count", 0) : 0;
                String threatData = String.format("Domain %s AlienVault reputation=%s pulses=%s", domain, reputation, pulseCount);
                threats.add(new Threat(UUID.randomUUID().toString(), "AlienVault", threatData));
                System.out.println("[AlienVault] " + threatData);
            } catch (Exception e) {
                System.err.println("[AlienVault Error] Failed to fetch domain: " + domain + " - " + e.getMessage());
            }
        }
        return threats;
    }
    
    /**
     * Mock threats - used when real APIs are not available or in demo mode
     */
    private List<Threat> getMockThreats() {
        return List.of(
            new Threat("1", "AlienVault", "Suspicious IP 192.168.1.100 and evil.com detected"),
            new Threat("2", "AbuseIPDB", "Domain malware.xyz and 10.0.0.5 are malicious")
        );
    }
    
    /**
     * Get current source mode (useful for monitoring)
     */
    public String getSourceMode() {
        String mode = sourceMode.toLowerCase();
        String status = "Mode: " + mode.toUpperCase();
        
        if (!mode.equals("mock")) {
            if (abuseIPDBKey != null && !abuseIPDBKey.trim().isEmpty()) {
                status += " | AbuseIPDB: CONFIGURED";
            } else {
                status += " | AbuseIPDB: NOT_CONFIGURED";
            }
            
            if (alienVaultKey != null && !alienVaultKey.trim().isEmpty()) {
                status += " | AlienVault: CONFIGURED";
            } else {
                status += " | AlienVault: NOT_CONFIGURED";
            }
        }
        
        return status;
    }
}

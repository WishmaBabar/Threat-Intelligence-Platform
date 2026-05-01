package com.ccp.kafkaconsumerprocessing.dto;

import java.time.Instant;

public class EnrichedIoc {
    private String type;
    private String value;
    private double severity;
    private Instant createdAt;
    public EnrichedIoc() {}
    public EnrichedIoc(String type, String value, double severity, Instant createdAt) {
        this.type = type;
        this.value = value;
        this.severity = severity;
        this.createdAt = createdAt;
    }
    // getters and setters...
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public double getSeverity() { return severity; }
    public void setSeverity(double severity) { this.severity = severity; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

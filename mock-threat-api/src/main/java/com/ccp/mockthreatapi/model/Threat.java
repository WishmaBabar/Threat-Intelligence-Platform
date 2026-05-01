package com.ccp.mockthreatapi.model;

public class Threat {
    private String id;
    private String source;
    private String raw;

    public Threat() {}
    public Threat(String id, String source, String raw) {
        this.id = id;
        this.source = source;
        this.raw = raw;
    }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getRaw() { return raw; }
    public void setRaw(String raw) { this.raw = raw; }
}

package com.ccp.kafkaconsumerprocessing.dto;
public class RankRequest {
    private String ioc;
    private String type;
    public RankRequest() {}
    public RankRequest(String ioc, String type) {
        this.ioc = ioc;
        this.type = type;
    }
    public String getIoc() { return ioc; }
    public void setIoc(String ioc) { this.ioc = ioc; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}

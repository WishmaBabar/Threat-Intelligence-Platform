package com.ccp.mockrankingapi.model;
public class RankResponse {
    private double score;
    public RankResponse() {}
    public RankResponse(double score) { this.score = score; }
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
}

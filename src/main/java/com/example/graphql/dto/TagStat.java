package com.example.graphql.dto;

public class TagStat {
    private String tag;
    private int count;
    private double percentage;

    public TagStat() {
    }

    public TagStat(String tag, int count, double percentage) {
        this.tag = tag;
        this.count = count;
        this.percentage = percentage;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
} 
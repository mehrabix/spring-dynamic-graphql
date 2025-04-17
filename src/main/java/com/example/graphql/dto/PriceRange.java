package com.example.graphql.dto;

public class PriceRange {
    private double min;
    private double max;
    private int count;
    private double percentage;

    public PriceRange() {
    }

    public PriceRange(double min, double max) {
        this.min = min;
        this.max = max;
        this.count = 0;
        this.percentage = 0;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
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

    public boolean inRange(double price) {
        return price >= min && price <= max;
    }
} 
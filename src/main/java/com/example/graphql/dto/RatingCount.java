package com.example.graphql.dto;

public class RatingCount {
    private float rating;
    private int count;
    private double percentage;

    public RatingCount() {
    }

    public RatingCount(float rating, int count, double percentage) {
        this.rating = rating;
        this.count = count;
        this.percentage = percentage;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
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
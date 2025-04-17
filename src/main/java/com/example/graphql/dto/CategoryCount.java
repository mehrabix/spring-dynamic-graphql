package com.example.graphql.dto;

public class CategoryCount {
    private String category;
    private int count;
    private double percentage;

    public CategoryCount() {
    }

    public CategoryCount(String category, int count, double percentage) {
        this.category = category;
        this.count = count;
        this.percentage = percentage;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
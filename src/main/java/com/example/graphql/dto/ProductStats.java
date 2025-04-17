package com.example.graphql.dto;

import java.util.List;
import java.util.Map;

public class ProductStats {
    private int count;
    private double avgPrice;
    private double minPrice;
    private double maxPrice;
    private int totalProducts;
    private int inStockCount;
    private int outOfStockCount;
    private PriceDistribution priceDistribution;
    private List<CategoryCount> categoryDistribution;
    private List<RatingCount> ratingDistribution;
    private List<TagStat> tagStats;

    public ProductStats() {
    }
    
    public ProductStats(int count, double avgPrice, double minPrice, double maxPrice, 
                        int inStockCount, int lowStockCount, Map<String, Long> categoryDistribution) {
        this.count = count;
        this.avgPrice = avgPrice;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.totalProducts = count;
        this.inStockCount = inStockCount;
        this.outOfStockCount = count - inStockCount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(double avgPrice) {
        this.avgPrice = avgPrice;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public int getInStockCount() {
        return inStockCount;
    }

    public void setInStockCount(int inStockCount) {
        this.inStockCount = inStockCount;
    }

    public int getOutOfStockCount() {
        return outOfStockCount;
    }

    public void setOutOfStockCount(int outOfStockCount) {
        this.outOfStockCount = outOfStockCount;
    }

    public PriceDistribution getPriceDistribution() {
        return priceDistribution;
    }

    public void setPriceDistribution(PriceDistribution priceDistribution) {
        this.priceDistribution = priceDistribution;
    }

    public List<CategoryCount> getCategoryDistribution() {
        return categoryDistribution;
    }

    public void setCategoryDistribution(List<CategoryCount> categoryDistribution) {
        this.categoryDistribution = categoryDistribution;
    }

    public List<RatingCount> getRatingDistribution() {
        return ratingDistribution;
    }

    public void setRatingDistribution(List<RatingCount> ratingDistribution) {
        this.ratingDistribution = ratingDistribution;
    }

    public List<TagStat> getTagStats() {
        return tagStats;
    }

    public void setTagStats(List<TagStat> tagStats) {
        this.tagStats = tagStats;
    }
} 
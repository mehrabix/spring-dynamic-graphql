package com.example.graphql.dto;

import java.util.List;

/**
 * DTO for filtering products with various criteria
 */
public class ProductFilter {
    private String nameContains;
    private Double minPrice;
    private Double maxPrice;
    private List<String> categories;
    private Boolean inStock;
    private Double minRating;
    private List<String> hasTags;
    private Boolean hasPriceChanged;
    private String createdAfter;
    private String createdBefore;
    private Integer minStockQuantity;
    private Integer minPopularity;
    
    public ProductFilter() {
    }
    
    public String getNameContains() {
        return nameContains;
    }
    
    public void setNameContains(String nameContains) {
        this.nameContains = nameContains;
    }
    
    public Double getMinPrice() {
        return minPrice;
    }
    
    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }
    
    public Double getMaxPrice() {
        return maxPrice;
    }
    
    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }
    
    public List<String> getCategories() {
        return categories;
    }
    
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
    
    public Boolean getInStock() {
        return inStock;
    }
    
    public void setInStock(Boolean inStock) {
        this.inStock = inStock;
    }
    
    public Double getMinRating() {
        return minRating;
    }
    
    public void setMinRating(Double minRating) {
        this.minRating = minRating;
    }
    
    public List<String> getHasTags() {
        return hasTags;
    }
    
    public void setHasTags(List<String> hasTags) {
        this.hasTags = hasTags;
    }
    
    public Boolean getHasPriceChanged() {
        return hasPriceChanged;
    }
    
    public void setHasPriceChanged(Boolean hasPriceChanged) {
        this.hasPriceChanged = hasPriceChanged;
    }
    
    public String getCreatedAfter() {
        return createdAfter;
    }
    
    public void setCreatedAfter(String createdAfter) {
        this.createdAfter = createdAfter;
    }
    
    public String getCreatedBefore() {
        return createdBefore;
    }
    
    public void setCreatedBefore(String createdBefore) {
        this.createdBefore = createdBefore;
    }
    
    public Integer getMinStockQuantity() {
        return minStockQuantity;
    }
    
    public void setMinStockQuantity(Integer minStockQuantity) {
        this.minStockQuantity = minStockQuantity;
    }
    
    public Integer getMinPopularity() {
        return minPopularity;
    }
    
    public void setMinPopularity(Integer minPopularity) {
        this.minPopularity = minPopularity;
    }
} 
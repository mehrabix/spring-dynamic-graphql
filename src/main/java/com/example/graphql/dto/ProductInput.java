package com.example.graphql.dto;

import java.io.Serializable;
import java.util.List;

public class ProductInput implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private Double price;
    private String category;
    private Boolean inStock;
    private Float rating;
    private List<String> tags;
    
    public ProductInput() {
    }
    
    public ProductInput(String name, String description, Double price, String category, Boolean inStock,
                       Float rating, List<String> tags) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.inStock = inStock;
        this.rating = rating;
        this.tags = tags;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public Boolean getInStock() {
        return inStock;
    }
    
    public void setInStock(Boolean inStock) {
        this.inStock = inStock;
    }
    
    public Float getRating() {
        return rating;
    }
    
    public void setRating(Float rating) {
        this.rating = rating;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    @Override
    public String toString() {
        return "ProductInput{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", inStock=" + inStock +
                ", rating=" + rating +
                ", tags=" + tags +
                '}';
    }
} 
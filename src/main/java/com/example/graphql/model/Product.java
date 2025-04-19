package com.example.graphql.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Transient;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Product implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    private Double price;
    private String category;
    private Boolean inStock;
    private Float rating;
    private Integer stockQuantity;
    private Integer popularity;
    
    // Store previous price for tracking changes
    @Transient
    private Double previousPrice;
    
    // Track operation type (not stored in database)
    @Transient
    private String operation;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_tags", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_related", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "related_product_id")
    private List<Long> relatedProductIds = new ArrayList<>();
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_frequently_bought", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "bought_with_id")
    private List<Long> frequentlyBoughtWithIds = new ArrayList<>();
    
    // For dynamic attribute storage
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_attributes", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "attribute_value")
    private Map<String, String> customAttributes = new HashMap<>();
    
    private String createdAt;
    private String updatedAt;
    
    // Default constructor required by JPA
    public Product() {
    }
    
    // Custom constructor
    public Product(Long id, String name, String description, Double price, String category, Boolean inStock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.inStock = inStock;
        this.stockQuantity = 10; // Default value
        this.popularity = 0;     // Default value
    }
    
    // Full constructor with all fields
    public Product(Long id, String name, String description, Double price, String category, 
                  Boolean inStock, Float rating, List<String> tags, Integer stockQuantity, Integer popularity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.inStock = inStock;
        this.rating = rating;
        this.tags = tags;
        this.stockQuantity = stockQuantity;
        this.popularity = popularity;
    }
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now().format(FORMATTER);
        this.updatedAt = this.createdAt;
        if (this.stockQuantity == null) {
            this.stockQuantity = 10;
        }
        if (this.popularity == null) {
            this.popularity = 0;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now().format(FORMATTER);
        // Save previous price for tracking changes
        if (this.price != null && this.previousPrice == null) {
            this.previousPrice = this.price;
        }
    }
    
    // Method to check if price has changed
    public boolean hasPriceChanged() {
        return previousPrice != null && !previousPrice.equals(price);
    }
    
    // Method to get price change percentage
    public Float getPriceChangePercentage() {
        if (previousPrice == null || previousPrice == 0) {
            return 0.0f;
        }
        return (float) ((price - previousPrice) / previousPrice * 100);
    }
    
    // Method to add a custom attribute
    public void addCustomAttribute(String key, String value) {
        this.customAttributes.put(key, value);
    }
    
    // Method to get a custom attribute
    public String getCustomAttribute(String key) {
        return this.customAttributes.get(key);
    }
    
    // Method to add a related product
    public void addRelatedProduct(Long productId) {
        if (!this.relatedProductIds.contains(productId)) {
            this.relatedProductIds.add(productId);
        }
    }
    
    // Method to add a frequently bought together product
    public void addFrequentlyBoughtWith(Long productId) {
        if (!this.frequentlyBoughtWithIds.contains(productId)) {
            this.frequentlyBoughtWithIds.add(productId);
        }
    }
    
    // Method to add a tag
    public void addTag(String tag) {
        if (!this.tags.contains(tag)) {
            this.tags.add(tag);
        }
    }
    
    // Method to remove a tag
    public boolean removeTag(String tag) {
        return this.tags.remove(tag);
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
        this.previousPrice = this.price; // Store previous price
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
    
    public boolean isInStock() {
        return inStock != null && inStock;
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
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Integer getStockQuantity() {
        return stockQuantity;
    }
    
    public Integer getInventoryCount() {
        return stockQuantity;
    }
    
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
        // Update inStock status based on stock quantity
        this.inStock = stockQuantity != null && stockQuantity > 0;
    }
    
    public Integer getPopularity() {
        return popularity;
    }
    
    public void setPopularity(Integer popularity) {
        this.popularity = popularity;
    }
    
    public Double getPreviousPrice() {
        return previousPrice;
    }
    
    public List<Long> getRelatedProductIds() {
        return relatedProductIds;
    }
    
    public void setRelatedProductIds(List<Long> relatedProductIds) {
        this.relatedProductIds = relatedProductIds;
    }
    
    public List<Long> getFrequentlyBoughtWithIds() {
        return frequentlyBoughtWithIds;
    }
    
    public void setFrequentlyBoughtWithIds(List<Long> frequentlyBoughtWithIds) {
        this.frequentlyBoughtWithIds = frequentlyBoughtWithIds;
    }
    
    public Map<String, String> getCustomAttributes() {
        return customAttributes;
    }
    
    public void setCustomAttributes(Map<String, String> customAttributes) {
        this.customAttributes = customAttributes;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public void setOperation(String operation) {
        this.operation = operation;
    }
    
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", inStock=" + inStock +
                ", rating=" + rating +
                ", stockQuantity=" + stockQuantity +
                ", popularity=" + popularity +
                ", tags=" + tags +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
} 
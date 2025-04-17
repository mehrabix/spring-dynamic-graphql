package com.example.graphql.dto;

import java.util.List;

public class DynamicProduct {
    private String id;
    private List<ProductAttribute> attributes;

    public DynamicProduct() {
    }

    public DynamicProduct(String id, List<ProductAttribute> attributes) {
        this.id = id;
        this.attributes = attributes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ProductAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<ProductAttribute> attributes) {
        this.attributes = attributes;
    }
} 
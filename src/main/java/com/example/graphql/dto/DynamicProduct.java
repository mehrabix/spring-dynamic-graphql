package com.example.graphql.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DynamicProduct {
    private String id;
    private List<ProductAttribute> attributes;

    public DynamicProduct() {
    }

    public DynamicProduct(String id, List<ProductAttribute> attributes) {
        this.id = id;
        this.attributes = attributes;
    }
    
    public DynamicProduct(Map<String, Object> attributeMap) {
        if (attributeMap.containsKey("id")) {
            this.id = attributeMap.get("id").toString();
        }
        
        this.attributes = new ArrayList<>();
        for (Map.Entry<String, Object> entry : attributeMap.entrySet()) {
            if (!entry.getKey().equals("id")) {
                this.attributes.add(new ProductAttribute(entry.getKey(), entry.getValue()));
            }
        }
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
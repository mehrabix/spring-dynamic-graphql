package com.example.graphql.repository;

import com.example.graphql.dto.ProductFilter;
import com.example.graphql.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductSpecificationTest {

    @Test
    void testSpecificationCreation() {
        // Given
        ProductFilter filter = new ProductFilter();
        filter.setNameContains("phone");
        filter.setMinPrice(100.0);
        filter.setMaxPrice(500.0);
        filter.setCategories(Arrays.asList("Electronics", "Mobile Phones"));
        filter.setInStock(true);
        filter.setMinRating(4.0);
        filter.setHasTags(Arrays.asList("wireless", "bluetooth"));
        
        // When
        Specification<Product> spec = ProductSpecification.getSpecification(filter);
        
        // Then
        assertNotNull(spec);
        // We can't easily test the behavior of the Specification without an actual database
        // But at least we can verify that it was created successfully and didn't throw any exceptions
    }
    
    @Test
    void testEmptyFilter() {
        // Given
        ProductFilter filter = new ProductFilter();
        
        // When
        Specification<Product> spec = ProductSpecification.getSpecification(filter);
        
        // Then
        assertNotNull(spec);
    }
    
    @Test
    void testNullableFields() {
        // Given
        ProductFilter filter = new ProductFilter();
        filter.setNameContains(null);
        filter.setMinPrice(null);
        filter.setMaxPrice(null);
        filter.setCategories(null);
        filter.setInStock(null);
        filter.setMinRating(null);
        filter.setHasTags(null);
        
        // When
        Specification<Product> spec = ProductSpecification.getSpecification(filter);
        
        // Then
        assertNotNull(spec);
        // Verify that no NullPointerException is thrown
    }
    
    @Test
    void testEmptyCollections() {
        // Given
        ProductFilter filter = new ProductFilter();
        filter.setCategories(List.of());
        filter.setHasTags(List.of());
        
        // When
        Specification<Product> spec = ProductSpecification.getSpecification(filter);
        
        // Then
        assertNotNull(spec);
        // Verify that empty collections are handled correctly
    }
} 
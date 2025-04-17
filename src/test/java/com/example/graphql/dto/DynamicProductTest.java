package com.example.graphql.dto;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for DynamicProduct DTO
 */
public class DynamicProductTest {

    @Test
    public void testConstructorAndGetters() {
        // Create a DynamicProduct instance
        String id = "PROD-123";
        List<ProductAttribute> attributes = Arrays.asList(
            new ProductAttribute("name", "Test Product"),
            new ProductAttribute("price", 99.99)
        );
        
        DynamicProduct product = new DynamicProduct(id, attributes);
        
        // Test getters
        assertEquals(id, product.getId());
        assertEquals(attributes, product.getAttributes());
        assertEquals(2, product.getAttributes().size());
        assertEquals("name", product.getAttributes().get(0).getName());
        assertEquals("Test Product", product.getAttributes().get(0).getValue());
        assertEquals("price", product.getAttributes().get(1).getName());
        assertEquals(99.99, product.getAttributes().get(1).getValue());
    }

    @Test
    public void testSetters() {
        // Create an empty DynamicProduct
        DynamicProduct product = new DynamicProduct();
        
        // Set values
        String id = "PROD-456";
        List<ProductAttribute> attributes = Arrays.asList(
            new ProductAttribute("category", "Electronics"),
            new ProductAttribute("inStock", true)
        );
        
        product.setId(id);
        product.setAttributes(attributes);
        
        // Verify values were set correctly
        assertEquals(id, product.getId());
        assertEquals(attributes, product.getAttributes());
        assertEquals(2, product.getAttributes().size());
        assertEquals("category", product.getAttributes().get(0).getName());
        assertEquals("Electronics", product.getAttributes().get(0).getValue());
        assertEquals("inStock", product.getAttributes().get(1).getName());
        assertEquals(true, product.getAttributes().get(1).getValue());
    }

    @Test
    public void testEmptyConstructor() {
        // Create a product with empty constructor
        DynamicProduct product = new DynamicProduct();
        
        // Verify initial values
        assertNull(product.getId());
        assertNull(product.getAttributes());
    }
} 
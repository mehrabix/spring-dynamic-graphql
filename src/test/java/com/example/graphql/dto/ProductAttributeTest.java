package com.example.graphql.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for ProductAttribute DTO
 */
public class ProductAttributeTest {

    @Test
    public void testConstructorAndGetters() {
        // Create a ProductAttribute instance
        String name = "price";
        String value = "99.99";
        
        ProductAttribute attribute = new ProductAttribute(name, value);
        
        // Test getters
        assertEquals(name, attribute.getName());
        assertEquals(value, attribute.getValue());
    }

    @Test
    public void testSetters() {
        // Create an empty ProductAttribute
        ProductAttribute attribute = new ProductAttribute();
        
        // Set values
        String name = "category";
        String value = "Electronics";
        
        attribute.setName(name);
        attribute.setValue(value);
        
        // Verify values were set correctly
        assertEquals(name, attribute.getName());
        assertEquals(value, attribute.getValue());
    }

    @Test
    public void testEmptyConstructor() {
        // Create an attribute with empty constructor
        ProductAttribute attribute = new ProductAttribute();
        
        // Verify initial values
        assertNull(attribute.getName());
        assertNull(attribute.getValue());
    }
} 
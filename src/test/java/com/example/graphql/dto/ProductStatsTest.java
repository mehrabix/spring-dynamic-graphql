package com.example.graphql.dto;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for ProductStats DTO
 */
public class ProductStatsTest {

    @Test
    public void testConstructor() {
        // Create a ProductStats instance with the actual constructor parameters
        ProductStats stats = new ProductStats(10, 100.0, 50.0, 150.0, 8, 2, Map.of("Electronics", 10L));
        
        // Verify that all fields are set correctly
        assertEquals(10, stats.getCount());
        assertEquals(100.0, stats.getAvgPrice());
        assertEquals(50.0, stats.getMinPrice());
        assertEquals(150.0, stats.getMaxPrice());
        assertEquals(10, stats.getTotalProducts());
        assertEquals(8, stats.getInStockCount());
        assertEquals(2, stats.getOutOfStockCount());
    }

    @Test
    public void testGettersAndSetters() {
        // Create an empty ProductStats
        ProductStats stats = new ProductStats();
        
        // Set values using setters
        stats.setCount(15);
        stats.setAvgPrice(120.0);
        stats.setMinPrice(60.0);
        stats.setMaxPrice(200.0);
        stats.setTotalProducts(15);
        stats.setInStockCount(12);
        stats.setOutOfStockCount(3);
        
        // Verify values using getters
        assertEquals(15, stats.getCount());
        assertEquals(120.0, stats.getAvgPrice());
        assertEquals(60.0, stats.getMinPrice());
        assertEquals(200.0, stats.getMaxPrice());
        assertEquals(15, stats.getTotalProducts());
        assertEquals(12, stats.getInStockCount());
        assertEquals(3, stats.getOutOfStockCount());
    }

    @Test
    public void testEmptyConstructor() {
        // Create a ProductStats with empty constructor
        ProductStats stats = new ProductStats();
        
        // No values should be set yet
        assertEquals(0, stats.getCount());
        assertEquals(0.0, stats.getAvgPrice());
        assertEquals(0.0, stats.getMinPrice());
        assertEquals(0.0, stats.getMaxPrice());
        assertEquals(0, stats.getTotalProducts());
        assertEquals(0, stats.getInStockCount());
        assertEquals(0, stats.getOutOfStockCount());
    }
} 
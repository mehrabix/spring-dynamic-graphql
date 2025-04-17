package com.example.graphql.dto;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ProductFilterTest {

    @Test
    void testProductFilterGettersAndSetters() {
        // Create a new ProductFilter instance
        ProductFilter filter = new ProductFilter();
        
        // Test string fields
        filter.setNameContains("phone");
        assertEquals("phone", filter.getNameContains());
        
        filter.setCreatedAfter("2023-01-01");
        assertEquals("2023-01-01", filter.getCreatedAfter());
        
        filter.setCreatedBefore("2023-12-31");
        assertEquals("2023-12-31", filter.getCreatedBefore());
        
        // Test numeric fields
        filter.setMinPrice(100.0);
        assertEquals(100.0, filter.getMinPrice());
        
        filter.setMaxPrice(500.0);
        assertEquals(500.0, filter.getMaxPrice());
        
        filter.setMinRating(4.5);
        assertEquals(4.5, filter.getMinRating());
        
        filter.setMinStockQuantity(5);
        assertEquals(5, filter.getMinStockQuantity());
        
        filter.setMinPopularity(50);
        assertEquals(50, filter.getMinPopularity());
        
        // Test boolean fields
        filter.setInStock(true);
        assertTrue(filter.getInStock());
        
        filter.setHasPriceChanged(false);
        assertFalse(filter.getHasPriceChanged());
        
        // Test list fields
        List<String> categories = Arrays.asList("Electronics", "Mobile Phones");
        filter.setCategories(categories);
        assertEquals(categories, filter.getCategories());
        
        List<String> tags = Arrays.asList("wireless", "bluetooth");
        filter.setHasTags(tags);
        assertEquals(tags, filter.getHasTags());
    }
    
    @Test
    void testProductFilterEmptyConstructor() {
        ProductFilter filter = new ProductFilter();
        
        assertNull(filter.getNameContains());
        assertNull(filter.getMinPrice());
        assertNull(filter.getMaxPrice());
        assertNull(filter.getCategories());
        assertNull(filter.getInStock());
        assertNull(filter.getMinRating());
        assertNull(filter.getHasTags());
        assertNull(filter.getHasPriceChanged());
        assertNull(filter.getCreatedAfter());
        assertNull(filter.getCreatedBefore());
        assertNull(filter.getMinStockQuantity());
        assertNull(filter.getMinPopularity());
    }
} 
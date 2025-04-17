package com.example.graphql.service;

import com.example.graphql.dto.ProductFilter;
import com.example.graphql.model.Product;
import com.example.graphql.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DynamicQueryServiceTest {

    @Mock
    private ProductRepository productRepository;
    
    private DynamicQueryService dynamicQueryService;
    private Method matchesFilterMethod;
    
    @BeforeEach
    void setUp() throws NoSuchMethodException {
        dynamicQueryService = new DynamicQueryService(productRepository);
        
        // Using reflection to test private method
        matchesFilterMethod = DynamicQueryService.class.getDeclaredMethod("matchesFilter", Product.class, ProductFilter.class);
        matchesFilterMethod.setAccessible(true);
    }
    
    @Test
    void testMatchesFilterWithNameContains() throws Exception {
        // Given
        Product product = createProduct("Smartphone X", 499.99, "Electronics", true);
        
        ProductFilter filter = new ProductFilter();
        filter.setNameContains("phone");
        
        // When
        boolean result = (boolean) matchesFilterMethod.invoke(dynamicQueryService, product, filter);
        
        // Then
        assertTrue(result);
        
        // Test with non-matching name
        filter.setNameContains("tablet");
        result = (boolean) matchesFilterMethod.invoke(dynamicQueryService, product, filter);
        assertFalse(result);
    }
    
    @Test
    void testMatchesFilterWithPriceRange() throws Exception {
        // Given
        Product product = createProduct("Smartphone X", 499.99, "Electronics", true);
        
        ProductFilter filter = new ProductFilter();
        filter.setMinPrice(400.0);
        filter.setMaxPrice(600.0);
        
        // When
        boolean result = (boolean) matchesFilterMethod.invoke(dynamicQueryService, product, filter);
        
        // Then
        assertTrue(result);
        
        // Test with price below range
        filter.setMinPrice(500.0);
        result = (boolean) matchesFilterMethod.invoke(dynamicQueryService, product, filter);
        assertFalse(result);
        
        // Test with price above range
        filter.setMinPrice(400.0);
        filter.setMaxPrice(400.0);
        result = (boolean) matchesFilterMethod.invoke(dynamicQueryService, product, filter);
        assertFalse(result);
    }
    
    @Test
    void testMatchesFilterWithCategory() throws Exception {
        // Given
        Product product = createProduct("Smartphone X", 499.99, "Electronics", true);
        
        ProductFilter filter = new ProductFilter();
        filter.setCategories(Arrays.asList("Electronics", "Mobile Phones"));
        
        // When
        boolean result = (boolean) matchesFilterMethod.invoke(dynamicQueryService, product, filter);
        
        // Then
        assertTrue(result);
        
        // Test with non-matching category
        filter.setCategories(Arrays.asList("Books", "Toys"));
        result = (boolean) matchesFilterMethod.invoke(dynamicQueryService, product, filter);
        assertFalse(result);
    }
    
    @Test
    void testMatchesFilterWithInStock() throws Exception {
        // Given
        Product product = createProduct("Smartphone X", 499.99, "Electronics", true);
        
        ProductFilter filter = new ProductFilter();
        filter.setInStock(true);
        
        // When
        boolean result = (boolean) matchesFilterMethod.invoke(dynamicQueryService, product, filter);
        
        // Then
        assertTrue(result);
        
        // Test with non-matching inStock
        filter.setInStock(false);
        result = (boolean) matchesFilterMethod.invoke(dynamicQueryService, product, filter);
        assertFalse(result);
    }
    
    @Test
    void testMatchesFilterWithRating() throws Exception {
        // Given
        Product product = createProduct("Smartphone X", 499.99, "Electronics", true);
        product.setRating(4.5f);
        
        ProductFilter filter = new ProductFilter();
        filter.setMinRating(4.0);
        
        // When
        boolean result = (boolean) matchesFilterMethod.invoke(dynamicQueryService, product, filter);
        
        // Then
        assertTrue(result);
        
        // Test with higher minimum rating
        filter.setMinRating(4.6);
        result = (boolean) matchesFilterMethod.invoke(dynamicQueryService, product, filter);
        assertFalse(result);
    }
    
    @Test
    void testMatchesFilterWithTags() throws Exception {
        // Given
        Product product = createProduct("Smartphone X", 499.99, "Electronics", true);
        product.setTags(Arrays.asList("wireless", "bluetooth", "smartphone"));
        
        ProductFilter filter = new ProductFilter();
        filter.setHasTags(Arrays.asList("bluetooth", "touchscreen"));
        
        // When
        boolean result = (boolean) matchesFilterMethod.invoke(dynamicQueryService, product, filter);
        
        // Then
        assertTrue(result);
        
        // Test with non-matching tags
        filter.setHasTags(Arrays.asList("waterproof", "rugged"));
        result = (boolean) matchesFilterMethod.invoke(dynamicQueryService, product, filter);
        assertFalse(result);
    }
    
    @Test
    void testMatchesFilterWithStockQuantity() throws Exception {
        // Given
        Product product = createProduct("Smartphone X", 499.99, "Electronics", true);
        product.setStockQuantity(10);
        
        ProductFilter filter = new ProductFilter();
        filter.setMinStockQuantity(5);
        
        // When
        boolean result = (boolean) matchesFilterMethod.invoke(dynamicQueryService, product, filter);
        
        // Then
        assertTrue(result);
        
        // Test with higher minimum stock
        filter.setMinStockQuantity(15);
        result = (boolean) matchesFilterMethod.invoke(dynamicQueryService, product, filter);
        assertFalse(result);
    }
    
    @Test
    void testMatchesFilterWithPopularity() throws Exception {
        // Given
        Product product = createProduct("Smartphone X", 499.99, "Electronics", true);
        product.setPopularity(75);
        
        ProductFilter filter = new ProductFilter();
        filter.setMinPopularity(50);
        
        // When
        boolean result = (boolean) matchesFilterMethod.invoke(dynamicQueryService, product, filter);
        
        // Then
        assertTrue(result);
        
        // Test with higher minimum popularity
        filter.setMinPopularity(80);
        result = (boolean) matchesFilterMethod.invoke(dynamicQueryService, product, filter);
        assertFalse(result);
    }
    
    @Test
    void testMatchesFilterWithMultipleCriteria() throws Exception {
        // Given
        Product product = createProduct("Smartphone X", 499.99, "Electronics", true);
        product.setRating(4.5f);
        product.setStockQuantity(10);
        product.setTags(Arrays.asList("wireless", "bluetooth", "smartphone"));
        
        ProductFilter filter = new ProductFilter();
        filter.setNameContains("phone");
        filter.setMinPrice(400.0);
        filter.setMaxPrice(600.0);
        filter.setCategories(Arrays.asList("Electronics", "Mobile Phones"));
        filter.setInStock(true);
        filter.setMinRating(4.0);
        filter.setHasTags(Arrays.asList("bluetooth"));
        filter.setMinStockQuantity(5);
        
        // When
        boolean result = (boolean) matchesFilterMethod.invoke(dynamicQueryService, product, filter);
        
        // Then
        assertTrue(result);
        
        // Test with one non-matching criterion
        filter.setMinPrice(600.0);
        result = (boolean) matchesFilterMethod.invoke(dynamicQueryService, product, filter);
        assertFalse(result);
    }
    
    private Product createProduct(String name, double price, String category, boolean inStock) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setCategory(category);
        product.setInStock(inStock);
        return product;
    }
} 
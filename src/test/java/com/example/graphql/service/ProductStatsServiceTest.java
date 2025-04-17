package com.example.graphql.service;

import com.example.graphql.dto.ProductFilter;
import com.example.graphql.dto.ProductStats;
import com.example.graphql.model.Product;
import com.example.graphql.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for the ProductStatsService
 */
public class ProductStatsServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private DynamicQueryService dynamicQueryService;

    @InjectMocks
    private ProductStatsService productStatsService;

    private List<Product> testProducts;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Create test products
        testProducts = new ArrayList<>();
        
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Test Product 1");
        product1.setPrice(100.0);
        product1.setInStock(true);
        product1.setRating(4.5f);
        product1.setStockQuantity(10);
        product1.setCategory("Electronics");
        
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Test Product 2");
        product2.setPrice(200.0);
        product2.setInStock(true);
        product2.setRating(4.0f);
        product2.setStockQuantity(5);
        product2.setCategory("Electronics");
        
        Product product3 = new Product();
        product3.setId(3L);
        product3.setName("Test Product 3");
        product3.setPrice(300.0);
        product3.setInStock(false);
        product3.setRating(3.5f);
        product3.setStockQuantity(0);
        product3.setCategory("Clothing");
        
        testProducts.addAll(Arrays.asList(product1, product2, product3));
    }

    @Test
    public void testGetOverallStats() {
        // Configure mock repository
        when(productRepository.findAll()).thenReturn(testProducts);
        
        // Call the method
        ProductStats stats = productStatsService.getOverallStats();
        
        // Verify repository was called
        verify(productRepository).findAll();
        
        // Verify the statistics
        assertEquals(3, stats.getCount());
        assertEquals(200.0, stats.getAvgPrice(), 0.01);
        assertEquals(100.0, stats.getMinPrice(), 0.01);
        assertEquals(300.0, stats.getMaxPrice(), 0.01);
        assertEquals(2, stats.getInStockCount());
        assertEquals(1, stats.getOutOfStockCount());
    }
    
    @Test
    public void testGetStatsByCategory() {
        // Configure mock repository
        when(productRepository.findByCategory("Electronics")).thenReturn(
            testProducts.stream()
                .filter(p -> "Electronics".equals(p.getCategory()))
                .toList()
        );
        
        // Call the method
        ProductStats stats = productStatsService.getStatsByCategory("Electronics");
        
        // Verify repository was called
        verify(productRepository).findByCategory("Electronics");
        
        // Verify the statistics
        assertEquals(2, stats.getCount());
        assertEquals(150.0, stats.getAvgPrice(), 0.01);
        assertEquals(100.0, stats.getMinPrice(), 0.01);
        assertEquals(200.0, stats.getMaxPrice(), 0.01);
        assertEquals(2, stats.getInStockCount());
        assertEquals(0, stats.getOutOfStockCount());
    }
    
    @Test
    public void testGetStatsByFilter() {
        // Create a filter
        ProductFilter filter = new ProductFilter();
        filter.setInStock(true);
        
        // Configure mocks
        when(productRepository.findAll()).thenReturn(testProducts);
        
        // Only in-stock products should pass the filter
        when(dynamicQueryService.applyFilter(any(), eq(filter))).thenReturn(
            testProducts.stream()
                .filter(Product::isInStock)
                .toList()
        );
        
        // Call the method
        ProductStats stats = productStatsService.getStatsByFilter(filter);
        
        // Verify repository and service were called
        verify(productRepository).findAll();
        verify(dynamicQueryService).applyFilter(testProducts, filter);
        
        // Verify the statistics
        assertEquals(2, stats.getCount());
        assertEquals(150.0, stats.getAvgPrice(), 0.01);
        assertEquals(100.0, stats.getMinPrice(), 0.01);
        assertEquals(200.0, stats.getMaxPrice(), 0.01);
        assertEquals(2, stats.getInStockCount());
        assertEquals(0, stats.getOutOfStockCount());
    }
    
    @Test
    public void testEmptyProductList() {
        // Configure mock repository to return empty list
        when(productRepository.findAll()).thenReturn(new ArrayList<>());
        
        // Call the method
        ProductStats stats = productStatsService.getOverallStats();
        
        // Verify repository was called
        verify(productRepository).findAll();
        
        // Verify the statistics for empty list
        assertEquals(0, stats.getCount());
        assertEquals(0.0, stats.getAvgPrice(), 0.01);
        assertEquals(0.0, stats.getMinPrice(), 0.01);
        assertEquals(0.0, stats.getMaxPrice(), 0.01);
        assertEquals(0, stats.getInStockCount());
        assertEquals(0, stats.getOutOfStockCount());
    }
} 
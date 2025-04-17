package com.example.graphql.resolver;

import com.example.graphql.dto.DynamicProduct;
import com.example.graphql.dto.ProductFilter;
import com.example.graphql.dto.ProductStats;
import com.example.graphql.model.Product;
import com.example.graphql.service.DynamicQueryService;
import com.example.graphql.service.ProductService;
import com.example.graphql.service.ProductStatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests for the StatsResolver class
 */
public class StatsResolverTest {

    @Mock
    private ProductStatsService productStatsService;

    @Mock
    private DynamicQueryService dynamicQueryService;
    
    @Mock
    private ProductService productService;

    @InjectMocks
    private StatsResolver statsResolver;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProductStats() {
        // Create a mock ProductStats object
        ProductStats expectedStats = new ProductStats();
        expectedStats.setCount(10);
        expectedStats.setAvgPrice(120.0);
        expectedStats.setMinPrice(50.0);
        expectedStats.setMaxPrice(200.0);
        expectedStats.setInStockCount(8);
        expectedStats.setOutOfStockCount(2);
        
        // Configure mock behavior
        when(productStatsService.getOverallStats()).thenReturn(expectedStats);
        
        // Call the method to test
        ProductStats result = statsResolver.productStats();
        
        // Verify the service was called
        verify(productStatsService).getOverallStats();
        
        // Verify the result
        assertNotNull(result);
        assertEquals(expectedStats.getCount(), result.getCount());
        assertEquals(expectedStats.getAvgPrice(), result.getAvgPrice());
        assertEquals(expectedStats.getMinPrice(), result.getMinPrice());
        assertEquals(expectedStats.getMaxPrice(), result.getMaxPrice());
        assertEquals(expectedStats.getInStockCount(), result.getInStockCount());
        assertEquals(expectedStats.getOutOfStockCount(), result.getOutOfStockCount());
    }
    
    @Test
    public void testProductStatsByCategory() {
        // Create a mock ProductStats object
        ProductStats expectedStats = new ProductStats();
        expectedStats.setCount(5);
        expectedStats.setAvgPrice(100.0);
        expectedStats.setMinPrice(80.0);
        expectedStats.setMaxPrice(120.0);
        expectedStats.setInStockCount(4);
        expectedStats.setOutOfStockCount(1);
        
        String category = "Electronics";
        
        // Configure mock behavior
        when(productStatsService.getStatsByCategory(category)).thenReturn(expectedStats);
        
        // Call the method to test
        ProductStats result = statsResolver.productStatsByCategory(category);
        
        // Verify the service was called with the correct category
        verify(productStatsService).getStatsByCategory(category);
        
        // Verify the result
        assertNotNull(result);
        assertEquals(expectedStats.getCount(), result.getCount());
        assertEquals(expectedStats.getAvgPrice(), result.getAvgPrice());
    }

    @Test
    public void testProductStatsByFilter() {
        // Create a mock ProductStats object
        ProductStats expectedStats = new ProductStats();
        expectedStats.setCount(7);
        expectedStats.setAvgPrice(150.0);
        expectedStats.setMinPrice(100.0);
        expectedStats.setMaxPrice(200.0);
        expectedStats.setInStockCount(5);
        expectedStats.setOutOfStockCount(2);
        
        // Create a test filter
        ProductFilter filter = new ProductFilter();
        filter.setMinPrice(50.0);
        filter.setInStock(true);
        
        // Configure mock behavior
        when(productStatsService.getStatsByFilter(any(ProductFilter.class))).thenReturn(expectedStats);
        
        // Call the method to test
        ProductStats result = statsResolver.productStatsByFilter(filter);
        
        // Verify the service was called with the correct filter
        verify(productStatsService).getStatsByFilter(filter);
        
        // Verify the result
        assertNotNull(result);
        assertEquals(expectedStats.getCount(), result.getCount());
        assertEquals(expectedStats.getAvgPrice(), result.getAvgPrice());
        assertEquals(expectedStats.getMinPrice(), result.getMinPrice());
        assertEquals(expectedStats.getMaxPrice(), result.getMaxPrice());
        assertEquals(expectedStats.getInStockCount(), result.getInStockCount());
        assertEquals(expectedStats.getOutOfStockCount(), result.getOutOfStockCount());
    }

    @Test
    public void testDynamicProductQuery() {
        // Create test data
        List<String> attributes = Arrays.asList("id", "name", "price");
        ProductFilter filter = new ProductFilter();
        filter.setMinRating(4.0);
        
        // Create expected result
        List<DynamicProduct> expectedProducts = Arrays.asList(
            new DynamicProduct(Map.of("id", 1L, "name", "Product 1", "price", 100.0)),
            new DynamicProduct(Map.of("id", 2L, "name", "Product 2", "price", 200.0))
        );
        
        // Configure mock behavior
        when(dynamicQueryService.dynamicProductQuery(eq(attributes), any(ProductFilter.class)))
            .thenReturn(expectedProducts);
        
        // Call the method to test
        List<DynamicProduct> result = statsResolver.dynamicProductQuery(attributes, filter);
        
        // Verify the service was called with the correct parameters
        verify(dynamicQueryService).dynamicProductQuery(attributes, filter);
        
        // Verify the result
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedProducts, result);
    }
    
    @Test
    public void testRelatedProducts() {
        // Create test data
        Long productId = 1L;
        Integer maxResults = 3;
        
        // Create mock products
        Product product1 = new Product();
        product1.setId(2L);
        product1.setName("Related Product 1");
        
        Product product2 = new Product();
        product2.setId(3L);
        product2.setName("Related Product 2");
        
        List<Product> expectedProducts = Arrays.asList(product1, product2);
        
        // Configure mock behavior
        when(dynamicQueryService.findRelatedProducts(eq(productId), eq(maxResults)))
            .thenReturn(expectedProducts);
        
        // Call the method to test
        List<Product> result = statsResolver.relatedProducts(productId, maxResults);
        
        // Verify the service was called with the correct parameters
        verify(dynamicQueryService).findRelatedProducts(productId, maxResults);
        
        // Verify the result
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedProducts, result);
    }
    
    @Test
    public void testFrequentlyBoughtTogether() {
        // Create test data
        Long productId = 1L;
        Integer maxResults = 2;
        
        // Create mock products
        Product product1 = new Product();
        product1.setId(4L);
        product1.setName("Bought Together Product 1");
        
        List<Product> expectedProducts = List.of(product1);
        
        // Configure mock behavior
        when(dynamicQueryService.findFrequentlyBoughtTogether(eq(productId), eq(maxResults)))
            .thenReturn(expectedProducts);
        
        // Call the method to test
        List<Product> result = statsResolver.frequentlyBoughtTogether(productId, maxResults);
        
        // Verify the service was called with the correct parameters
        verify(dynamicQueryService).findFrequentlyBoughtTogether(productId, maxResults);
        
        // Verify the result
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedProducts, result);
    }
    
    @Test
    public void testNullMaxResults() {
        // Create test data
        Long productId = 1L;
        
        // Create mock products
        List<Product> expectedProducts = Collections.emptyList();
        
        // Configure mock behavior
        when(dynamicQueryService.findRelatedProducts(eq(productId), eq(5)))
            .thenReturn(expectedProducts);
        
        // Call the method to test with null maxResults (should default to 5)
        List<Product> result = statsResolver.relatedProducts(productId, null);
        
        // Verify the service was called with the default value
        verify(dynamicQueryService).findRelatedProducts(productId, 5);
        
        // Verify the result
        assertNotNull(result);
        assertEquals(0, result.size());
    }
} 
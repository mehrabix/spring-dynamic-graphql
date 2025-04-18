package com.example.graphql.service;

import com.example.graphql.config.WebSocketSubscriptionConfig;
import com.example.graphql.dto.ProductPriceChange;
import com.example.graphql.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductSubscriptionServiceTest {

    @Mock
    private WebSocketSubscriptionConfig.ProductSubscriptionPublisher publisher;

    @InjectMocks
    private ProductSubscriptionService subscriptionService;
    
    @Captor
    private ArgumentCaptor<ProductPriceChange> priceChangeCaptor;
    
    private Product originalProduct;
    private Product updatedProduct;
    private Product lowStockProduct;
    private Product newProduct;

    @BeforeEach
    void setUp() {
        // Setup original product
        originalProduct = new Product();
        originalProduct.setId(1L);
        originalProduct.setName("Original Product");
        originalProduct.setDescription("Original Description");
        originalProduct.setPrice(89.99);
        originalProduct.setCategory("Electronics");
        originalProduct.setInStock(true);
        originalProduct.setStockQuantity(20);
        
        // Setup updated product with changed price
        updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName("Updated Product");
        updatedProduct.setDescription("Updated Description");
        updatedProduct.setPrice(99.99);
        updatedProduct.setCategory("Electronics");
        updatedProduct.setInStock(true);
        updatedProduct.setStockQuantity(18);
        
        // Setup low stock product
        lowStockProduct = new Product();
        lowStockProduct.setId(2L);
        lowStockProduct.setName("Low Stock Product");
        lowStockProduct.setPrice(49.99);
        lowStockProduct.setCategory("Accessories");
        lowStockProduct.setInStock(true);
        lowStockProduct.setStockQuantity(3);
        
        // Setup new product
        newProduct = new Product();
        newProduct.setId(3L);
        newProduct.setName("New Product");
        newProduct.setPrice(129.99);
        newProduct.setCategory("Gadgets");
        newProduct.setInStock(true);
        newProduct.setStockQuantity(30);
    }

    @Test
    void testNotifyProductUpdated() {
        // Call the method under test
        subscriptionService.notifyProductUpdated(updatedProduct);
        
        // Verify the publisher was called
        verify(publisher).publishProductUpdate(updatedProduct);
    }
    
    @Test
    void testNotifyPriceChanged() {
        // Call the method under test - create a price change with the old and new price
        double oldPrice = 89.99;
        double newPrice = 99.99;
        subscriptionService.notifyPriceChanged(updatedProduct, oldPrice, newPrice);
        
        // Verify the publisher was called
        verify(publisher).publishPriceChange(priceChangeCaptor.capture());
        
        // Verify the price change object
        ProductPriceChange priceChange = priceChangeCaptor.getValue();
        assertEquals(updatedProduct, priceChange.getProduct());
        assertEquals(oldPrice, priceChange.getOldPrice(), 0.001);
        assertEquals(newPrice, priceChange.getNewPrice(), 0.001);
    }
    
    @Test
    void testNotifyLowStock() {
        // Call the method under test
        subscriptionService.notifyLowStock(lowStockProduct);
        
        // Verify the publisher was called
        verify(publisher).publishLowStockAlert(lowStockProduct);
    }
    
    @Test
    void testHandleProductUpdate_WithPriceChange() {
        // Call the method under test
        subscriptionService.handleProductUpdate(originalProduct, updatedProduct);
        
        // Verify that both update and price change notifications were sent
        verify(publisher).publishProductUpdate(updatedProduct);
        verify(publisher).publishPriceChange(priceChangeCaptor.capture());
        
        // Verify the price change object
        ProductPriceChange priceChange = priceChangeCaptor.getValue();
        assertEquals(updatedProduct, priceChange.getProduct());
        assertEquals(89.99, priceChange.getOldPrice(), 0.001);
        assertEquals(99.99, priceChange.getNewPrice(), 0.001);
    }
    
    @Test
    void testHandleProductUpdate_WithoutPriceChange() {
        // Create an updated product with the same price
        Product samePrice = new Product();
        samePrice.setId(1L);
        samePrice.setName("Updated Name Only");
        samePrice.setPrice(89.99); // Same price as original
        samePrice.setStockQuantity(15); // Make sure to set stock quantity to avoid NPE
        
        // Call the method under test
        subscriptionService.handleProductUpdate(originalProduct, samePrice);
        
        // Verify that only update notification was sent (no price change)
        verify(publisher).publishProductUpdate(samePrice);
        
        // Price change should not be called
        verify(publisher, never()).publishPriceChange(any());
    }
    
    @Test
    void testHandleProductUpdate_WithLowStock() {
        // Create an updated product with low stock
        Product lowStock = new Product();
        lowStock.setId(1L);
        lowStock.setName("Low Stock Updated");
        lowStock.setPrice(89.99);
        lowStock.setStockQuantity(3); // Low stock
        
        // Call the method under test
        subscriptionService.handleProductUpdate(originalProduct, lowStock);
        
        // Verify that update notification was sent
        verify(publisher).publishProductUpdate(lowStock);
        
        // Verify low stock notification was sent
        verify(publisher).publishLowStockAlert(lowStock);
    }
    
    @Test
    void testHandleProductCreated() {
        // Call the method under test
        subscriptionService.handleProductCreated(newProduct);
        
        // Verify that update notification was sent
        verify(publisher).publishProductUpdate(newProduct);
    }
    
    @Test
    void testHandleProductCreated_WithLowStock() {
        // Create a new product with low stock
        Product newLowStock = new Product();
        newLowStock.setId(4L);
        newLowStock.setName("New Low Stock Product");
        newLowStock.setPrice(19.99);
        newLowStock.setStockQuantity(2); // Low stock
        
        // Call the method under test
        subscriptionService.handleProductCreated(newLowStock);
        
        // Verify that update notification was sent
        verify(publisher).publishProductUpdate(newLowStock);
        
        // Verify low stock notification was sent
        verify(publisher).publishLowStockAlert(newLowStock);
    }
} 
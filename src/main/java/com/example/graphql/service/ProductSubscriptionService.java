package com.example.graphql.service;

import com.example.graphql.config.WebSocketSubscriptionConfig.ProductSubscriptionPublisher;
import com.example.graphql.dto.ProductPriceChange;
import com.example.graphql.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service responsible for handling product subscription events.
 * This service acts as a bridge between product operations and GraphQL subscriptions.
 */
@Service
public class ProductSubscriptionService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductSubscriptionService.class);
    
    private final ProductSubscriptionPublisher publisher;
    
    @Autowired
    public ProductSubscriptionService(ProductSubscriptionPublisher publisher) {
        this.publisher = publisher;
        logger.info("ProductSubscriptionService initialized");
    }
    
    /**
     * Notify subscribers about a product update
     * @param product The updated product
     */
    public void notifyProductUpdated(Product product) {
        logger.debug("Notifying subscribers about product update: {}", product.getId());
        publisher.publishProductUpdate(product);
    }
    
    /**
     * Notify subscribers about a price change if the difference is significant
     * @param product The product with updated price
     * @param oldPrice The previous price
     * @param newPrice The new price
     */
    public void notifyPriceChanged(Product product, Double oldPrice, Double newPrice) {
        logger.debug("Product price changed from {} to {} for product: {}", 
                     oldPrice, newPrice, product.getId());
        
        ProductPriceChange priceChange = new ProductPriceChange(product, oldPrice, newPrice);
        publisher.publishPriceChange(priceChange);
    }
    
    /**
     * Notify subscribers about low stock levels for a product
     * @param product The product with low stock
     */
    public void notifyLowStock(Product product) {
        // We publish regardless of threshold - the threshold filtering 
        // is handled in the subscription publisher
        logger.debug("Low stock notification for product: {} (quantity: {})", 
                    product.getId(), product.getStockQuantity());
        publisher.publishLowStockAlert(product);
    }
    
    /**
     * Check and send appropriate notifications after a product update
     * @param oldProduct The product before update
     * @param newProduct The product after update
     */
    public void handleProductUpdate(Product oldProduct, Product newProduct) {
        // Always notify about the general product update
        notifyProductUpdated(newProduct);
        
        // Check if price has changed
        if (!oldProduct.getPrice().equals(newProduct.getPrice())) {
            notifyPriceChanged(newProduct, oldProduct.getPrice(), newProduct.getPrice());
        }
        
        // Check if stock is low
        if (newProduct.getStockQuantity() <= 10) { // Default threshold, actual filtering happens in publisher
            notifyLowStock(newProduct);
        }
    }
    
    /**
     * Handle notifications for a newly created product
     * @param product The newly created product
     */
    public void handleProductCreated(Product product) {
        // Notify about new product
        notifyProductUpdated(product);
        
        // Check if initial stock is low
        if (product.getStockQuantity() <= 10) {
            notifyLowStock(product);
        }
    }
} 
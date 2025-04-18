package com.example.graphql.resolver;

import com.example.graphql.config.WebSocketSubscriptionConfig.ProductSubscriptionPublisher;
import com.example.graphql.dto.ProductPriceChange;
import com.example.graphql.model.Product;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;

/**
 * Resolver for GraphQL subscriptions.
 * This class maps GraphQL subscription operations to reactive streams.
 */
@Controller
public class SubscriptionResolver {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionResolver.class);
    
    private final ProductSubscriptionPublisher publisher;
    
    @Autowired
    public SubscriptionResolver(ProductSubscriptionPublisher publisher) {
        this.publisher = publisher;
        logger.info("SubscriptionResolver initialized");
    }
    
    /**
     * Subscribe to all product updates.
     * 
     * @return A Publisher that emits Product objects whenever a product is updated
     */
    @SubscriptionMapping
    public Publisher<Product> productUpdated() {
        logger.info("New subscription for product updates");
        return publisher.getProductUpdatePublisher();
    }
    
    /**
     * Subscribe to price change notifications with a minimum difference threshold.
     * 
     * @param minPriceDifference The minimum price difference to trigger a notification
     * @return A Publisher that emits ProductPriceChange objects when product prices change
     */
    @SubscriptionMapping
    public Publisher<ProductPriceChange> productPriceChanged(@Argument Double minPriceDifference) {
        logger.info("New subscription for price changes with threshold: {}", minPriceDifference);
        return publisher.getPriceChangePublisher(minPriceDifference);
    }
    
    /**
     * Subscribe to low stock alerts with a configurable threshold.
     * 
     * @param threshold The stock quantity threshold for alerts (default is 5)
     * @return A Publisher that emits Product objects when stock is low
     */
    @SubscriptionMapping
    public Publisher<Product> lowStockAlert(@Argument Integer threshold) {
        // If threshold is null, use the default value of 5
        int actualThreshold = (threshold != null) ? threshold : 5;
        logger.info("New subscription for low stock alerts with threshold: {}", actualThreshold);
        return publisher.getLowStockPublisher(actualThreshold);
    }
} 
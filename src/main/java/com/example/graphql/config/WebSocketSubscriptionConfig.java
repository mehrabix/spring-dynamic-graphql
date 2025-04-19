package com.example.graphql.config;

import com.example.graphql.model.Product;
import com.example.graphql.dto.ProductPriceChange;
import com.example.graphql.service.ProductService;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.stereotype.Component;

import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.FluxSink;

import java.util.function.Predicate;

/**
 * Configuration class for GraphQL WebSocket Subscriptions.
 * This class sets up reactive streams for real-time product updates.
 */
@Configuration
public class WebSocketSubscriptionConfig {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketSubscriptionConfig.class);

    @Component
    public static class ProductSubscriptionPublisher {
        private final FluxProcessor<Product, Product> productUpdateProcessor;
        private final FluxSink<Product> productUpdateSink;
        
        private final FluxProcessor<ProductPriceChange, ProductPriceChange> priceChangeProcessor;
        private final FluxSink<ProductPriceChange> priceChangeSink;
        
        private final FluxProcessor<Product, Product> lowStockProcessor;
        private final FluxSink<Product> lowStockSink;

        public ProductSubscriptionPublisher() {
            this.productUpdateProcessor = DirectProcessor.<Product>create().serialize();
            this.productUpdateSink = productUpdateProcessor.sink();
            
            this.priceChangeProcessor = DirectProcessor.<ProductPriceChange>create().serialize();
            this.priceChangeSink = priceChangeProcessor.sink();
            
            this.lowStockProcessor = DirectProcessor.<Product>create().serialize();
            this.lowStockSink = lowStockProcessor.sink();
            
            logger.info("ProductSubscriptionPublisher initialized");
        }

        /**
         * Publishes a product update event that will be sent to subscribers
         * 
         * @param product The updated product
         */
        public void publishProductUpdate(Product product) {
            logger.debug("Publishing product update for product ID: {}", product.getId());
            productUpdateSink.next(product);
        }
        
        /**
         * Publishes a price change event that will be sent to subscribers
         * 
         * @param priceChange Contains product, old price, and new price information
         */
        public void publishPriceChange(ProductPriceChange priceChange) {
            logger.debug("Publishing price change for product ID: {}", priceChange.getProduct().getId());
            priceChangeSink.next(priceChange);
        }
        
        /**
         * Publishes a low stock alert that will be sent to subscribers
         * 
         * @param product The product with low stock
         */
        public void publishLowStockAlert(Product product) {
            logger.debug("Publishing low stock alert for product ID: {}", product.getId());
            lowStockSink.next(product);
        }

        /**
         * Returns a filtered publisher for product updates
         */
        public Publisher<Product> getProductUpdatePublisher() {
            return productUpdateProcessor;
        }
        
        /**
         * Returns a filtered publisher for price changes with a minimum difference threshold
         * 
         * @param minPriceDifference The minimum price difference to trigger notification
         */
        public Publisher<ProductPriceChange> getPriceChangePublisher(Double minPriceDifference) {
            // If minPriceDifference is null, don't filter based on price difference
            if (minPriceDifference == null) {
                return priceChangeProcessor;
            }
            
            return Flux.from(priceChangeProcessor)
                .filter(priceChange -> {
                    double absDifference = Math.abs(priceChange.getNewPrice() - priceChange.getOldPrice());
                    return absDifference >= minPriceDifference;
                });
        }
        
        /**
         * Returns a filtered publisher for low stock alerts
         * 
         * @param threshold The stock quantity threshold for alerts
         */
        public Publisher<Product> getLowStockPublisher(Integer threshold) {
            return Flux.from(lowStockProcessor)
                .filter(product -> product.getStockQuantity() <= threshold);
        }
    }

    @Bean
    public RuntimeWiringConfigurer subscriptionWiringConfigurer(ProductSubscriptionPublisher publisher) {
        return wiringBuilder -> wiringBuilder
            .type("Subscription", builder -> builder
                // Subscription for any product updates
                .dataFetcher("productUpdated", environment -> {
                    logger.info("Subscription request received for productUpdated");
                    return publisher.getProductUpdatePublisher();
                })
                // Subscription for price changes with configurable threshold
                .dataFetcher("productPriceChanged", environment -> {
                    Double minPriceDifference = environment.getArgument("minPriceDifference");
                    logger.info("Subscription request received for productPriceChanged with minPriceDifference: {}", minPriceDifference);
                    return publisher.getPriceChangePublisher(minPriceDifference);
                })
                // Subscription for low stock alerts with configurable threshold
                .dataFetcher("lowStockAlert", environment -> {
                    Integer threshold = environment.getArgument("threshold");
                    logger.info("Subscription request received for lowStockAlert with threshold: {}", threshold);
                    return publisher.getLowStockPublisher(threshold);
                })
            );
    }
} 
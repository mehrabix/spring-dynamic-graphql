package com.example.graphql.service;

import com.example.graphql.dto.ProductFilter;
import com.example.graphql.dto.ProductStats;
import com.example.graphql.model.Product;
import com.example.graphql.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductStatsService {

    private final ProductRepository productRepository;
    private final DynamicQueryService dynamicQueryService;

    @Autowired
    public ProductStatsService(ProductRepository productRepository, DynamicQueryService dynamicQueryService) {
        this.productRepository = productRepository;
        this.dynamicQueryService = dynamicQueryService;
    }

    /**
     * Get overall statistics for all products
     */
    public ProductStats getOverallStats() {
        List<Product> products = productRepository.findAll();
        return calculateStats(products);
    }

    /**
     * Get statistics for products in a specific category
     */
    public ProductStats getStatsByCategory(String category) {
        List<Product> products = productRepository.findByCategory(category);
        return calculateStats(products);
    }

    /**
     * Get statistics for products matching a filter
     */
    public ProductStats getStatsByFilter(ProductFilter filter) {
        List<Product> products = dynamicQueryService.applyFilter(productRepository.findAll(), filter);
        return calculateStats(products);
    }

    /**
     * Calculate statistics from a list of products
     */
    private ProductStats calculateStats(List<Product> products) {
        if (products.isEmpty()) {
            return new ProductStats(0, 0.0, 0.0, 0.0, 0, 0, Map.of());
        }

        DoubleSummaryStatistics priceStats = products.stream()
                .mapToDouble(Product::getPrice)
                .summaryStatistics();

        Map<String, Long> categoryDistribution = products.stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.counting()
                ));

        int inStockCount = (int) products.stream()
                .filter(Product::isInStock)
                .count();

        int lowStockCount = (int) products.stream()
                .filter(p -> p.isInStock() && p.getInventoryCount() < 10)
                .count();

        return new ProductStats(
                products.size(),
                priceStats.getAverage(),
                priceStats.getMin(),
                priceStats.getMax(),
                inStockCount,
                lowStockCount,
                categoryDistribution
        );
    }
} 
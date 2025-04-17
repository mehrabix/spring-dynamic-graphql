package com.example.graphql.resolver;

import com.example.graphql.dto.DynamicProduct;
import com.example.graphql.dto.ProductFilter;
import com.example.graphql.dto.ProductStats;
import com.example.graphql.model.Product;
import com.example.graphql.service.DynamicQueryService;
import com.example.graphql.service.ProductService;
import com.example.graphql.service.ProductStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class StatsResolver {

    private final ProductStatsService productStatsService;
    private final DynamicQueryService dynamicQueryService;
    private final ProductService productService;

    @Autowired
    public StatsResolver(
            ProductStatsService productStatsService,
            DynamicQueryService dynamicQueryService,
            ProductService productService) {
        this.productStatsService = productStatsService;
        this.dynamicQueryService = dynamicQueryService;
        this.productService = productService;
    }

    /**
     * Get overall product statistics
     */
    @QueryMapping
    public ProductStats productStats() {
        return productStatsService.getOverallStats();
    }

    /**
     * Get product statistics for a specific category
     */
    @QueryMapping
    public ProductStats productStatsByCategory(@Argument String category) {
        return productStatsService.getStatsByCategory(category);
    }

    /**
     * Get product statistics based on a filter
     */
    @QueryMapping
    public ProductStats productStatsByFilter(@Argument ProductFilter filter) {
        return productStatsService.getStatsByFilter(filter);
    }

    /**
     * Dynamic query for fetching only specific product attributes
     */
    @QueryMapping
    public List<DynamicProduct> dynamicProductQuery(@Argument List<String> attributes, @Argument ProductFilter filter) {
        return dynamicQueryService.dynamicProductQuery(attributes, filter);
    }

    /**
     * Find related products based on algorithm
     */
    @QueryMapping
    public List<Product> relatedProducts(@Argument Long id, @Argument Integer maxResults) {
        return dynamicQueryService.findRelatedProducts(id, maxResults != null ? maxResults : 5);
    }

    /**
     * Find frequently bought together products
     */
    @QueryMapping
    public List<Product> frequentlyBoughtTogether(@Argument Long id, @Argument Integer maxResults) {
        return dynamicQueryService.findFrequentlyBoughtTogether(id, maxResults != null ? maxResults : 5);
    }
} 
package com.example.graphql.service;

import com.example.graphql.dto.DynamicProduct;
import com.example.graphql.dto.ProductAttribute;
import com.example.graphql.dto.ProductFilter;
import com.example.graphql.model.Product;
import com.example.graphql.repository.ProductRepository;
import com.example.graphql.repository.ProductSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DynamicQueryService {

    private final ProductRepository productRepository;

    @Autowired
    public DynamicQueryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Execute a dynamic query for products based on selected attributes and filters
     */
    public List<Map<String, Object>> executeQuery(List<String> attributes, ProductFilter filter) {
        List<Product> products = getFilteredProducts(filter);
        return extractAttributes(products, attributes);
    }

    /**
     * Apply filters to a list of products
     */
    public List<Product> applyFilter(List<Product> products, ProductFilter filter) {
        if (filter == null) {
            return products;
        }

        return products.stream()
                .filter(product -> matchesFilter(product, filter))
                .collect(Collectors.toList());
    }

    /**
     * Get filtered products from repository
     */
    public List<Product> getFilteredProducts(ProductFilter filter) {
        List<Product> allProducts = productRepository.findAll();
        return applyFilter(allProducts, filter);
    }

    /**
     * Find related products based on category, tags, and other attributes
     */
    public List<Product> findRelatedProducts(Long productId, Integer maxResults) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return Collections.emptyList();
        }

        Product product = productOpt.get();
        List<Product> allProducts = productRepository.findAll();
        
        // Remove the current product from candidates
        List<Product> candidates = allProducts.stream()
                .filter(p -> !p.getId().equals(productId))
                .collect(Collectors.toList());

        // Score each product based on similarity
        Map<Product, Integer> productScores = new HashMap<>();
        for (Product candidate : candidates) {
            int score = calculateSimilarityScore(product, candidate);
            productScores.put(candidate, score);
        }

        // Sort by score (descending) and take top results
        return productScores.entrySet().stream()
                .sorted(Map.Entry.<Product, Integer>comparingByValue().reversed())
                .limit(maxResults != null ? maxResults : 5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Find products frequently bought together with a given product
     */
    public List<Product> findFrequentlyBoughtTogether(Long productId, Integer maxResults) {
        // In a real application, this would query from order history or a recommendation system
        // For this example, we'll simulate with random related products
        return findRelatedProducts(productId, maxResults);
    }

    /**
     * Extract specified attributes from products
     */
    private List<Map<String, Object>> extractAttributes(List<Product> products, List<String> attributes) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Product product : products) {
            Map<String, Object> productMap = new HashMap<>();
            for (String attribute : attributes) {
                try {
                    Field field = getField(Product.class, attribute);
                    if (field != null) {
                        field.setAccessible(true);
                        productMap.put(attribute, field.get(product));
                    }
                } catch (Exception e) {
                    // Skip attributes that can't be accessed
                }
            }
            result.add(productMap);
        }
        
        return result;
    }

    /**
     * Get a field from a class or its superclasses
     */
    private Field getField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                return getField(superClass, fieldName);
            }
        }
        return null;
    }

    /**
     * Check if a product matches the given filter
     */
    private boolean matchesFilter(Product product, ProductFilter filter) {
        if (filter.getNameContains() != null && !filter.getNameContains().isEmpty() && 
            (product.getName() == null || !product.getName().toLowerCase().contains(filter.getNameContains().toLowerCase()))) {
            return false;
        }
        
        if (filter.getMinPrice() != null && product.getPrice() < filter.getMinPrice()) {
            return false;
        }
        
        if (filter.getMaxPrice() != null && product.getPrice() > filter.getMaxPrice()) {
            return false;
        }
        
        if (filter.getCategories() != null && !filter.getCategories().isEmpty() && 
            (product.getCategory() == null || !filter.getCategories().contains(product.getCategory()))) {
            return false;
        }
        
        if (filter.getMinRating() != null && (product.getRating() == null || product.getRating() < filter.getMinRating())) {
            return false;
        }
        
        if (filter.getInStock() != null && !filter.getInStock().equals(product.isInStock())) {
            return false;
        }
        
        if (filter.getHasTags() != null && !filter.getHasTags().isEmpty()) {
            if (product.getTags() == null || product.getTags().isEmpty()) {
                return false;
            }
            
            boolean hasAnyTag = false;
            for (String tag : filter.getHasTags()) {
                if (product.getTags().contains(tag)) {
                    hasAnyTag = true;
                    break;
                }
            }
            
            if (!hasAnyTag) {
                return false;
            }
        }
        
        if (filter.getMinStockQuantity() != null && 
            (product.getStockQuantity() == null || product.getStockQuantity() < filter.getMinStockQuantity())) {
            return false;
        }
        
        if (filter.getMinPopularity() != null && 
            (product.getPopularity() == null || product.getPopularity() < filter.getMinPopularity())) {
            return false;
        }
        
        // Handling date-based filters would require date parsing
        // This is a simplified version that assumes createdAfter and createdBefore are ISO date strings
        if (filter.getCreatedAfter() != null && product.getCreatedAt() != null) {
            try {
                if (product.getCreatedAt().compareTo(filter.getCreatedAfter()) < 0) {
                    return false;
                }
            } catch (Exception e) {
                // Skip date comparison if parsing fails
            }
        }
        
        if (filter.getCreatedBefore() != null && product.getCreatedAt() != null) {
            try {
                if (product.getCreatedAt().compareTo(filter.getCreatedBefore()) > 0) {
                    return false;
                }
            } catch (Exception e) {
                // Skip date comparison if parsing fails
            }
        }
        
        // hasPriceChanged filter would require access to price history, which is not included in this example
        
        return true;
    }

    /**
     * Calculate similarity score between two products
     */
    private int calculateSimilarityScore(Product product1, Product product2) {
        int score = 0;
        
        // Same category gets high score
        if (product1.getCategory() != null && product1.getCategory().equals(product2.getCategory())) {
            score += 5;
        }
        
        // Similar price range (within 20%)
        if (Math.abs(product1.getPrice() - product2.getPrice()) / product1.getPrice() < 0.2) {
            score += 2;
        }
        
        // Common tags
        if (product1.getTags() != null && product2.getTags() != null) {
            Set<String> tags1 = new HashSet<>(product1.getTags());
            for (String tag : product2.getTags()) {
                if (tags1.contains(tag)) {
                    score += 1;
                }
            }
        }
        
        return score;
    }

    /**
     * Dynamic query that returns only the requested attributes
     */
    public List<DynamicProduct> dynamicProductQuery(List<String> attributes, ProductFilter filter) {
        // Validate attributes
        List<String> validAttributes = validateAttributes(attributes);
        
        // Get products based on filter
        List<Product> products;
        if (filter != null) {
            Specification<Product> spec = ProductSpecification.getSpecification(filter);
            products = productRepository.findAll(spec);
        } else {
            products = productRepository.findAll();
        }
        
        // Transform to dynamic products with only requested attributes
        return products.stream()
                .map(product -> createDynamicProduct(product, validAttributes))
                .collect(Collectors.toList());
    }
    
    /**
     * Helper to score product similarity
     */
    private int scoreProductSimilarity(Product base, Product compare) {
        int score = 0;
        
        // Score for same category
        if (base.getCategory() != null && base.getCategory().equals(compare.getCategory())) {
            score += 5;
        }
        
        // Score for matching tags
        if (base.getTags() != null && compare.getTags() != null) {
            for (String tag : base.getTags()) {
                if (compare.getTags().contains(tag)) {
                    score += 2;
                }
            }
        }
        
        // Score for similar price range (within 20%)
        if (base.getPrice() != null && compare.getPrice() != null) {
            double priceRatio = base.getPrice() / compare.getPrice();
            if (priceRatio >= 0.8 && priceRatio <= 1.2) {
                score += 3;
            }
        }
        
        // Score for similar rating (within 0.5)
        if (base.getRating() != null && compare.getRating() != null) {
            float ratingDiff = Math.abs(base.getRating() - compare.getRating());
            if (ratingDiff <= 0.5) {
                score += 2;
            }
        }
        
        return score;
    }
    
    /**
     * Creates a dynamic product with only specified attributes
     */
    private DynamicProduct createDynamicProduct(Product product, List<String> attributes) {
        DynamicProduct dynamicProduct = new DynamicProduct();
        dynamicProduct.setId(product.getId().toString());
        
        List<ProductAttribute> productAttributes = new ArrayList<>();
        
        for (String attribute : attributes) {
            Object value = getAttributeValue(product, attribute);
            if (value != null) {
                productAttributes.add(new ProductAttribute(attribute, value.toString()));
            } else if (product.getCustomAttributes().containsKey(attribute)) {
                productAttributes.add(new ProductAttribute(
                    attribute, 
                    product.getCustomAttributes().get(attribute)
                ));
            }
        }
        
        dynamicProduct.setAttributes(productAttributes);
        return dynamicProduct;
    }
    
    /**
     * Gets a specific attribute value using reflection
     */
    private Object getAttributeValue(Product product, String attributeName) {
        try {
            // First check if it's a custom attribute
            if (product.getCustomAttributes().containsKey(attributeName)) {
                return product.getCustomAttributes().get(attributeName);
            }
            
            // Then try to get it from standard fields
            Field field = ReflectionUtils.findField(Product.class, attributeName);
            if (field != null) {
                field.setAccessible(true);
                return field.get(product);
            }
            
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Validates attributes against allowed fields
     */
    private List<String> validateAttributes(List<String> attributes) {
        List<String> validAttributes = new ArrayList<>();
        
        // Standard product attributes
        List<String> standardAttributes = List.of(
            "id", "name", "description", "price", "category", 
            "inStock", "rating", "stockQuantity", "popularity", 
            "tags", "createdAt", "updatedAt"
        );
        
        for (String attribute : attributes) {
            if (standardAttributes.contains(attribute)) {
                validAttributes.add(attribute);
            }
            // We could also validate custom attributes here
        }
        
        // Ensure id is always included
        if (!validAttributes.contains("id")) {
            validAttributes.add("id");
        }
        
        return validAttributes;
    }
} 
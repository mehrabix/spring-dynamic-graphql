package com.example.graphql.controller;

import com.example.graphql.dto.PageInput;
import com.example.graphql.dto.ProductFilter;
import com.example.graphql.dto.ProductInput;
import com.example.graphql.dto.ProductPage;
import com.example.graphql.dto.ProductSort;
import com.example.graphql.model.Product;
import com.example.graphql.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProductController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;
    
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    // Basic queries
    @QueryMapping
    public List<Product> allProducts() {
        try {
            logger.info("Fetching all products");
            List<Product> products = productService.getAllProducts();
            logger.info("Found {} products", products.size());
            return products;
        } catch (Exception e) {
            logger.error("Error fetching all products", e);
            throw e;
        }
    }
    
    @QueryMapping
    public Product productById(@Argument String id) {
        try {
            logger.info("Fetching product with ID: {}", id);
            Long productId = Long.parseLong(id);
            return productService.getProductById(productId).orElse(null);
        } catch (Exception e) {
            logger.error("Error fetching product with ID: {}", id, e);
            throw e;
        }
    }
    
    @QueryMapping
    public List<Product> productsByCategory(@Argument String category) {
        try {
            logger.info("Fetching products with category: {}", category);
            return productService.getProductsByCategory(category);
        } catch (Exception e) {
            logger.error("Error fetching products by category: {}", category, e);
            throw e;
        }
    }
    
    // Advanced query with filtering, sorting and pagination
    @QueryMapping
    public ProductPage productsWithFilter(
            @Argument ProductFilter filter,
            @Argument ProductSort sort,
            @Argument PageInput page) {
        try {
            logger.info("Executing advanced product query with filters");
            return productService.getProductsWithFilter(filter, sort, page);
        } catch (Exception e) {
            logger.error("Error executing advanced product query", e);
            throw e;
        }
    }
    
    // Basic mutations
    @MutationMapping
    public Product addProduct(@Argument ProductInput product) {
        try {
            logger.info("Adding new product: {}", product.getName());
            Product newProduct = mapInputToProduct(product);
            return productService.addProduct(newProduct);
        } catch (Exception e) {
            logger.error("Error adding product", e);
            throw e;
        }
    }
    
    @MutationMapping
    public Product updateProduct(@Argument String id, @Argument ProductInput product) {
        try {
            logger.info("Updating product with ID: {}", id);
            Long productId = Long.parseLong(id);
            Product updatedProduct = mapInputToProduct(product);
            updatedProduct.setId(productId);
            
            return productService.updateProduct(productId, updatedProduct).orElse(null);
        } catch (Exception e) {
            logger.error("Error updating product with ID: {}", id, e);
            throw e;
        }
    }
    
    @MutationMapping
    public Boolean deleteProduct(@Argument String id) {
        try {
            logger.info("Deleting product with ID: {}", id);
            Long productId = Long.parseLong(id);
            return productService.deleteProduct(productId);
        } catch (Exception e) {
            logger.error("Error deleting product with ID: {}", id, e);
            throw e;
        }
    }
    
    // Bulk operations
    @MutationMapping
    public List<Product> bulkAddProducts(@Argument List<ProductInput> products) {
        try {
            logger.info("Adding {} products in bulk", products.size());
            List<Product> productEntities = products.stream()
                .map(this::mapInputToProduct)
                .collect(Collectors.toList());
            
            return productService.bulkAddProducts(productEntities);
        } catch (Exception e) {
            logger.error("Error adding products in bulk", e);
            throw e;
        }
    }
    
    @MutationMapping
    public Integer bulkDeleteProducts(@Argument List<String> ids) {
        try {
            logger.info("Deleting {} products in bulk", ids.size());
            List<Long> productIds = ids.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
            
            return productService.bulkDeleteProducts(productIds);
        } catch (Exception e) {
            logger.error("Error deleting products in bulk", e);
            throw e;
        }
    }
    
    // Helper methods
    private Product mapInputToProduct(ProductInput input) {
        Product product = new Product();
        product.setName(input.getName());
        product.setDescription(input.getDescription());
        product.setPrice(input.getPrice());
        product.setCategory(input.getCategory());
        product.setInStock(input.getInStock());
        product.setRating(input.getRating());
        product.setTags(input.getTags());
        return product;
    }
} 
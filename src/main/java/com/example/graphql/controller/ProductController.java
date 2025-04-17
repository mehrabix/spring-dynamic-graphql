package com.example.graphql.controller;

import com.example.graphql.dto.ProductInput;
import com.example.graphql.model.Product;
import com.example.graphql.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ProductController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;
    
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
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
    
    @MutationMapping
    public Product addProduct(@Argument ProductInput product) {
        try {
            logger.info("Adding new product: {}", product.getName());
            Product newProduct = new Product(
                null,
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getInStock()
            );
            
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
            Product updatedProduct = new Product(
                productId,
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getInStock()
            );
            
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
} 
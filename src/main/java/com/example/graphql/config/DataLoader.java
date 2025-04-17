package com.example.graphql.config;

import com.example.graphql.model.Product;
import com.example.graphql.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataLoader implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Autowired
    public DataLoader(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        // Clear existing data
        productRepository.deleteAll();

        // Add sample products
        Product product1 = new Product(null, "Laptop", "High-performance laptop", 1299.99, "Electronics", true);
        Product product2 = new Product(null, "Smartphone", "Latest model with advanced features", 899.99, "Electronics", true);
        Product product3 = new Product(null, "Headphones", "Noise-cancelling wireless headphones", 249.99, "Audio", true);
        Product product4 = new Product(null, "Coffee Maker", "Automatic coffee machine", 99.99, "Kitchen", false);
        Product product5 = new Product(null, "Tennis Racket", "Professional-grade tennis racket", 189.99, "Sports", true);

        productRepository.saveAll(Arrays.asList(product1, product2, product3, product4, product5));
        
        System.out.println("Sample data loaded successfully!");
    }
} 
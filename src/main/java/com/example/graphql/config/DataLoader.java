package com.example.graphql.config;

import com.example.graphql.model.Product;
import com.example.graphql.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        // Define categories for organization
        List<String> categories = Arrays.asList(
            "Electronics", "Computers", "Smart Home", "Audio", 
            "Mobile Phones", "Wearables", "Gaming", "Office", "Kitchen"
        );
        
        // Create sample products
        List<Product> products = new ArrayList<>();
        
        // Electronics
        Product laptop = new Product(null, "High-Performance Laptop", "15-inch laptop with the latest processor, 16GB RAM and 512GB SSD", 1299.99, "Electronics", true);
        laptop.setRating(4.7f);
        laptop.setTags(Arrays.asList("computer", "laptop", "portable"));
        products.add(laptop);
        
        Product smartphone = new Product(null, "Flagship Smartphone", "Latest model with advanced camera system and all-day battery life", 899.99, "Mobile Phones", true);
        smartphone.setRating(4.8f);
        smartphone.setTags(Arrays.asList("phone", "mobile", "camera", "android"));
        products.add(smartphone);
        
        Product tablet = new Product(null, "Pro Tablet", "12-inch tablet for creative professionals with stylus support", 799.99, "Electronics", true);
        tablet.setRating(4.5f);
        tablet.setTags(Arrays.asList("tablet", "stylus", "portable", "graphics"));
        products.add(tablet);
        
        // Audio
        Product headphones = new Product(null, "Noise-Cancelling Headphones", "Over-ear wireless headphones with premium sound quality", 249.99, "Audio", true);
        headphones.setRating(4.6f);
        headphones.setTags(Arrays.asList("audio", "wireless", "bluetooth", "noise-cancelling"));
        products.add(headphones);
        
        Product earbuds = new Product(null, "Wireless Earbuds", "True wireless earbuds with long battery life and water resistance", 159.99, "Audio", true);
        earbuds.setRating(4.4f);
        earbuds.setTags(Arrays.asList("audio", "wireless", "earbuds", "water-resistant"));
        products.add(earbuds);
        
        Product speaker = new Product(null, "Smart Speaker", "Voice-controlled speaker with room-filling sound", 129.99, "Smart Home", true);
        speaker.setRating(4.2f);
        speaker.setTags(Arrays.asList("audio", "smart", "voice-control", "bluetooth"));
        products.add(speaker);
        
        // Kitchen
        Product coffeemaker = new Product(null, "Premium Coffee Maker", "Programmable coffee machine with built-in grinder", 149.99, "Kitchen", false);
        coffeemaker.setRating(4.3f);
        coffeemaker.setTags(Arrays.asList("kitchen", "coffee", "brewing", "appliance"));
        products.add(coffeemaker);
        
        Product blender = new Product(null, "High-Speed Blender", "Professional-grade blender for smoothies and food prep", 199.99, "Kitchen", true);
        blender.setRating(4.5f);
        blender.setTags(Arrays.asList("kitchen", "blender", "smoothie", "food-processor"));
        products.add(blender);
        
        // Gaming
        Product gamingConsole = new Product(null, "Gaming Console", "Next-gen gaming console with 4K capabilities", 499.99, "Gaming", false);
        gamingConsole.setRating(4.9f);
        gamingConsole.setTags(Arrays.asList("gaming", "console", "4k", "entertainment"));
        products.add(gamingConsole);
        
        Product gamingHeadset = new Product(null, "Gaming Headset", "Immersive surround sound headset for competitive gaming", 149.99, "Gaming", true);
        gamingHeadset.setRating(4.4f);
        gamingHeadset.setTags(Arrays.asList("gaming", "audio", "headset", "microphone"));
        products.add(gamingHeadset);
        
        Product gamingKeyboard = new Product(null, "Mechanical Gaming Keyboard", "Customizable RGB mechanical keyboard with programmable keys", 129.99, "Gaming", true);
        gamingKeyboard.setRating(4.6f);
        gamingKeyboard.setTags(Arrays.asList("gaming", "keyboard", "mechanical", "rgb"));
        products.add(gamingKeyboard);
        
        // Wearables
        Product smartwatch = new Product(null, "Fitness Smartwatch", "Advanced fitness tracking with heart rate monitoring and GPS", 199.99, "Wearables", true);
        smartwatch.setRating(4.3f);
        smartwatch.setTags(Arrays.asList("wearable", "fitness", "smartwatch", "gps"));
        products.add(smartwatch);
        
        // Office
        Product printer = new Product(null, "Wireless Laser Printer", "Fast, reliable printer with wireless connectivity", 249.99, "Office", true);
        printer.setRating(4.0f);
        printer.setTags(Arrays.asList("office", "printer", "wireless", "laser"));
        products.add(printer);
        
        Product webcam = new Product(null, "HD Webcam", "High-definition webcam for video conferencing", 79.99, "Office", true);
        webcam.setRating(4.2f);
        webcam.setTags(Arrays.asList("office", "webcam", "video", "conference"));
        products.add(webcam);
        
        // Save all products
        productRepository.saveAll(products);
        
        System.out.println("Sample data loaded successfully! Added " + products.size() + " products.");
    }
} 
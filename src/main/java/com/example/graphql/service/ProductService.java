package com.example.graphql.service;

import com.example.graphql.dto.PageInfo;
import com.example.graphql.dto.PageInput;
import com.example.graphql.dto.ProductFilter;
import com.example.graphql.dto.ProductPage;
import com.example.graphql.dto.ProductSort;
import com.example.graphql.model.Product;
import com.example.graphql.repository.ProductRepository;
import com.example.graphql.repository.ProductSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ProductSubscriptionService subscriptionService;
    
    @Autowired
    public ProductService(ProductRepository productRepository, ProductSubscriptionService subscriptionService) {
        this.productRepository = productRepository;
        this.subscriptionService = subscriptionService;
    }
    
    // Basic operations
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    public Product addProduct(Product product) {
        Product savedProduct = productRepository.save(product);
        
        // Notify subscribers about the new product
        subscriptionService.handleProductCreated(savedProduct);
        
        return savedProduct;
    }
    
    public Optional<Product> updateProduct(Long id, Product productDetails) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    // Keep a copy of the original product for comparison
                    Product originalProduct = new Product();
                    originalProduct.setId(existingProduct.getId());
                    originalProduct.setName(existingProduct.getName());
                    originalProduct.setDescription(existingProduct.getDescription());
                    originalProduct.setPrice(existingProduct.getPrice());
                    originalProduct.setCategory(existingProduct.getCategory());
                    originalProduct.setInStock(existingProduct.getInStock());
                    originalProduct.setRating(existingProduct.getRating());
                    originalProduct.setTags(existingProduct.getTags());
                    originalProduct.setStockQuantity(existingProduct.getStockQuantity());
                    
                    // Update the product
                    existingProduct.setName(productDetails.getName());
                    existingProduct.setDescription(productDetails.getDescription());
                    existingProduct.setPrice(productDetails.getPrice());
                    existingProduct.setCategory(productDetails.getCategory());
                    existingProduct.setInStock(productDetails.getInStock());
                    
                    if (productDetails.getRating() != null) {
                        existingProduct.setRating(productDetails.getRating());
                    }
                    
                    if (productDetails.getTags() != null) {
                        existingProduct.setTags(productDetails.getTags());
                    }
                    
                    if (productDetails.getStockQuantity() != null) {
                        existingProduct.setStockQuantity(productDetails.getStockQuantity());
                    }
                    
                    Product updatedProduct = productRepository.save(existingProduct);
                    
                    // Notify subscribers about the update
                    subscriptionService.handleProductUpdate(originalProduct, updatedProduct);
                    
                    return updatedProduct;
                });
    }
    
    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            // Get the product before deleting
            Optional<Product> productOpt = productRepository.findById(id);
            productRepository.deleteById(id);
            
            // Notify subscribers if product was found
            productOpt.ifPresent(product -> {
                // Create a copy with a "deleted" marker for proper notification
                Product updatedProduct = new Product();
                updatedProduct.setId(product.getId());
                updatedProduct.setName(product.getName());
                updatedProduct.setDescription(product.getDescription());
                updatedProduct.setPrice(product.getPrice());
                updatedProduct.setCategory(product.getCategory());
                updatedProduct.setInStock(false); // Mark as out of stock since it's deleted
                updatedProduct.setRating(product.getRating());
                updatedProduct.setTags(product.getTags());
                updatedProduct.setStockQuantity(0); // Set stock to 0 since it's deleted
                
                // Use handleProductUpdate to ensure all subscribers are notified
                subscriptionService.handleProductUpdate(product, updatedProduct);
                
                // Additional notification for low stock (zero stock is definitely low)
                subscriptionService.notifyLowStock(updatedProduct);
            });
            
            return true;
        }
        return false;
    }
    
    // Advanced operations
    @Transactional
    public List<Product> bulkAddProducts(List<Product> products) {
        List<Product> savedProducts = productRepository.saveAll(products);
        
        // Notify subscribers about each new product
        for (Product product : savedProducts) {
            subscriptionService.handleProductCreated(product);
        }
        
        return savedProducts;
    }
    
    @Transactional
    public int bulkDeleteProducts(List<Long> ids) {
        int count = 0;
        for (Long id : ids) {
            if (productRepository.existsById(id)) {
                // Get the product before deleting
                Optional<Product> productOpt = productRepository.findById(id);
                productRepository.deleteById(id);
                
                // Notify subscribers if product was found
                productOpt.ifPresent(product -> {
                    // Create a copy with a "deleted" marker for proper notification
                    Product updatedProduct = new Product();
                    updatedProduct.setId(product.getId());
                    updatedProduct.setName(product.getName());
                    updatedProduct.setDescription(product.getDescription());
                    updatedProduct.setPrice(product.getPrice());
                    updatedProduct.setCategory(product.getCategory());
                    updatedProduct.setInStock(false); // Mark as out of stock since it's deleted
                    updatedProduct.setRating(product.getRating());
                    updatedProduct.setTags(product.getTags());
                    updatedProduct.setStockQuantity(0); // Set stock to 0 since it's deleted
                    
                    // Use handleProductUpdate to ensure all subscribers are notified
                    subscriptionService.handleProductUpdate(product, updatedProduct);
                    
                    // Additional notification for low stock (zero stock is definitely low)
                    subscriptionService.notifyLowStock(updatedProduct);
                });
                
                count++;
            }
        }
        return count;
    }
    
    // Advanced query with filtering, sorting and pagination
    public ProductPage getProductsWithFilter(ProductFilter filter, ProductSort sort, PageInput pageInput) {
        // Create specification from filter
        Specification<Product> spec = null;
        if (filter != null) {
            spec = ProductSpecification.getSpecification(filter);
        }
        
        // Create pageable from page input and sort
        Pageable pageable = createPageable(pageInput, sort);
        
        // Execute query
        Page<Product> productPage;
        if (spec != null) {
            productPage = productRepository.findAll(spec, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }
        
        // Convert to DTO
        return convertToProductPage(productPage);
    }
    
    // Helper methods
    private Pageable createPageable(PageInput pageInput, ProductSort sort) {
        int page = 0;
        int size = 10;
        
        if (pageInput != null) {
            page = pageInput.getPage();
            size = pageInput.getSize();
        }
        
        if (sort != null && sort.getField() != null) {
            String sortField = getSortField(sort.getField());
            Sort.Direction direction = sort.getDirection() == ProductSort.SortDirection.ASC ? 
                Sort.Direction.ASC : Sort.Direction.DESC;
            
            return PageRequest.of(page, size, direction, sortField);
        }
        
        return PageRequest.of(page, size);
    }
    
    private String getSortField(ProductSort.ProductSortField field) {
        switch (field) {
            case NAME:
                return "name";
            case PRICE:
                return "price";
            case RATING:
                return "rating";
            case CREATED_AT:
                return "createdAt";
            case ID:
            default:
                return "id";
        }
    }
    
    private ProductPage convertToProductPage(Page<Product> page) {
        PageInfo pageInfo = new PageInfo(
            (int) page.getTotalElements(),
            page.getTotalPages(),
            page.getNumber(),
            page.getSize()
        );
        
        return new ProductPage(page.getContent(), pageInfo);
    }
    
    /**
     * Check stock level for a product and trigger low stock alert if needed
     * @param productId ID of the product to check
     */
    public void checkAndNotifyLowStock(Long productId) {
        productRepository.findById(productId).ifPresent(product -> {
            if (product.getStockQuantity() <= 10) {
                subscriptionService.notifyLowStock(product);
            }
        });
    }
} 
package com.example.graphql.repository;

import com.example.graphql.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    
    // Basic queries
    List<Product> findByCategory(String category);
    
    List<Product> findByInStock(boolean inStock);
    
    // More complex queries
    List<Product> findByCategoryAndInStock(String category, boolean inStock);
    
    List<Product> findByPriceBetween(double minPrice, double maxPrice);
    
    List<Product> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT p FROM Product p WHERE p.rating >= :minRating")
    List<Product> findByMinRating(@Param("minRating") float minRating);
    
    @Query("SELECT p FROM Product p JOIN p.tags t WHERE t IN :tags GROUP BY p HAVING COUNT(DISTINCT t) = :tagCount")
    List<Product> findByAllTags(@Param("tags") List<String> tags, @Param("tagCount") long tagCount);
} 
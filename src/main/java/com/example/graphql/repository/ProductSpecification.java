package com.example.graphql.repository;

import com.example.graphql.dto.ProductFilter;
import com.example.graphql.model.Product;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> getSpecification(ProductFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Filter by name containing
            if (filter.getNameContains() != null && !filter.getNameContains().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + filter.getNameContains().toLowerCase() + "%"
                ));
            }
            
            // Filter by minimum price
            if (filter.getMinPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("price"), filter.getMinPrice()
                ));
            }
            
            // Filter by maximum price
            if (filter.getMaxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("price"), filter.getMaxPrice()
                ));
            }
            
            // Filter by categories
            if (filter.getCategories() != null && !filter.getCategories().isEmpty()) {
                predicates.add(root.get("category").in(filter.getCategories()));
            }
            
            // Filter by in stock
            if (filter.getInStock() != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("inStock"), filter.getInStock()
                ));
            }
            
            // Filter by minimum rating
            if (filter.getMinRating() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("rating"), filter.getMinRating()
                ));
            }
            
            // Filter by tags
            if (filter.getHasTags() != null && !filter.getHasTags().isEmpty()) {
                // Custom handling for tags since it's a collection
                query.distinct(true);  // Avoid duplicates
                Join<Product, String> tagJoin = root.join("tags");
                predicates.add(tagJoin.in(filter.getHasTags()));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
} 
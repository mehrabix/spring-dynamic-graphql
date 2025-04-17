package com.example.graphql.dto;

public class ProductSort {
    private ProductSortField field;
    private SortDirection direction;

    public ProductSortField getField() {
        return field;
    }

    public void setField(ProductSortField field) {
        this.field = field;
    }

    public SortDirection getDirection() {
        return direction;
    }

    public void setDirection(SortDirection direction) {
        this.direction = direction;
    }

    public enum ProductSortField {
        ID, NAME, PRICE, RATING, CREATED_AT
    }

    public enum SortDirection {
        ASC, DESC
    }
} 
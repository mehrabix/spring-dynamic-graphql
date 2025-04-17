package com.example.graphql.dto;

import com.example.graphql.model.Product;

public class ProductPriceChange {
    private Product product;
    private double oldPrice;
    private double newPrice;
    private double percentChange;

    public ProductPriceChange() {
    }

    public ProductPriceChange(Product product, double oldPrice, double newPrice) {
        this.product = product;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.percentChange = oldPrice != 0 ? ((newPrice - oldPrice) / oldPrice) * 100 : 0;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public double getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public double getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(double newPrice) {
        this.newPrice = newPrice;
    }

    public double getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(double percentChange) {
        this.percentChange = percentChange;
    }
} 
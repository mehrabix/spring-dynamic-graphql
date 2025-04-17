package com.example.graphql.dto;

import java.util.List;

/**
 * DTO for representing a single period in a sales report
 */
public class SalesReportPeriod {
    private String period;
    private int totalSales;
    private double totalRevenue;
    private double averageOrderValue;
    private List<TopSellingProduct> topSellingProducts;

    public SalesReportPeriod() {
    }

    public SalesReportPeriod(String period, int totalSales, double totalRevenue, 
                           double averageOrderValue, List<TopSellingProduct> topSellingProducts) {
        this.period = period;
        this.totalSales = totalSales;
        this.totalRevenue = totalRevenue;
        this.averageOrderValue = averageOrderValue;
        this.topSellingProducts = topSellingProducts;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public int getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(int totalSales) {
        this.totalSales = totalSales;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public double getAverageOrderValue() {
        return averageOrderValue;
    }

    public void setAverageOrderValue(double averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }

    public List<TopSellingProduct> getTopSellingProducts() {
        return topSellingProducts;
    }

    public void setTopSellingProducts(List<TopSellingProduct> topSellingProducts) {
        this.topSellingProducts = topSellingProducts;
    }

    /**
     * Inner class representing a top-selling product
     */
    public static class TopSellingProduct {
        private String productId;
        private String productName;
        private int unitsSold;
        private double revenue;

        public TopSellingProduct() {
        }

        public TopSellingProduct(String productId, String productName, int unitsSold, double revenue) {
            this.productId = productId;
            this.productName = productName;
            this.unitsSold = unitsSold;
            this.revenue = revenue;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public int getUnitsSold() {
            return unitsSold;
        }

        public void setUnitsSold(int unitsSold) {
            this.unitsSold = unitsSold;
        }

        public double getRevenue() {
            return revenue;
        }

        public void setRevenue(double revenue) {
            this.revenue = revenue;
        }
    }
} 
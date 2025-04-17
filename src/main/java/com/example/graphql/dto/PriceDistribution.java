package com.example.graphql.dto;

public class PriceDistribution {
    private PriceRange lowRange;
    private PriceRange midRange;
    private PriceRange highRange;

    public PriceDistribution() {
        this.lowRange = new PriceRange(0, 99.99);
        this.midRange = new PriceRange(100, 499.99);
        this.highRange = new PriceRange(500, Double.MAX_VALUE);
    }

    public PriceRange getLowRange() {
        return lowRange;
    }

    public void setLowRange(PriceRange lowRange) {
        this.lowRange = lowRange;
    }

    public PriceRange getMidRange() {
        return midRange;
    }

    public void setMidRange(PriceRange midRange) {
        this.midRange = midRange;
    }

    public PriceRange getHighRange() {
        return highRange;
    }

    public void setHighRange(PriceRange highRange) {
        this.highRange = highRange;
    }
} 
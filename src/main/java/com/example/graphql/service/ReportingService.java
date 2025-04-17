package com.example.graphql.service;

import com.example.graphql.dto.ProductFilter;
import com.example.graphql.dto.SalesReportPeriod;
import com.example.graphql.dto.TimeframeType;
import com.example.graphql.model.Product;
import com.example.graphql.repository.ProductRepository;
import com.example.graphql.repository.ProductSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Service for generating various types of reports
 */
@Service
public class ReportingService {

    private final ProductRepository productRepository;
    private final DynamicQueryService dynamicQueryService;
    private final Random random = new Random(); // Used for demo data generation

    @Autowired
    public ReportingService(ProductRepository productRepository, DynamicQueryService dynamicQueryService) {
        this.productRepository = productRepository;
        this.dynamicQueryService = dynamicQueryService;
    }

    /**
     * Generate a sales report by timeframe
     * 
     * Note: For demo purposes, this generates simulated sales data based on products
     * In a real application, this would pull from actual sales records
     */
    public List<SalesReportPeriod> generateSalesReportByTimeframe(
            TimeframeType timeframe, 
            String startDateStr, 
            String endDateStr, 
            ProductFilter filter) {
        
        // Get filtered products
        List<Product> filteredProducts = getFilteredProducts(filter);
        
        // Parse start and end dates
        LocalDate startDate = parseDate(startDateStr);
        LocalDate endDate = parseDate(endDateStr);
        
        // Generate periods based on timeframe
        List<PeriodBoundary> periods = generatePeriods(timeframe, startDate, endDate);
        
        // Generate report for each period
        return periods.stream()
                .map(period -> generateSalesReportForPeriod(period, filteredProducts))
                .collect(Collectors.toList());
    }
    
    /**
     * Generate a single period of sales report
     */
    private SalesReportPeriod generateSalesReportForPeriod(PeriodBoundary period, List<Product> products) {
        // For demo purposes, we'll simulate sales data based on product properties
        // In a real app, this would query a sales/orders table
        
        // Simulate total sales (number of orders)
        int totalSales = random.nextInt(products.size() * 5) + products.size();
        
        // Calculate product sales (simulate units sold per product)
        Map<Product, Integer> productSales = new HashMap<>();
        double totalRevenue = 0.0;
        
        for (Product product : products) {
            // Simulate units sold - more popular products sell more
            int unitsSold = calculateSimulatedUnitsSold(product, period);
            productSales.put(product, unitsSold);
            
            // Calculate revenue for this product
            double productRevenue = unitsSold * product.getPrice();
            totalRevenue += productRevenue;
        }
        
        // Calculate average order value
        double averageOrderValue = totalSales > 0 ? totalRevenue / totalSales : 0;
        
        // Get top selling products
        List<SalesReportPeriod.TopSellingProduct> topProducts = productSales.entrySet().stream()
                .sorted(Map.Entry.<Product, Integer>comparingByValue().reversed())
                .limit(5) // Top 5 products
                .map(entry -> {
                    Product product = entry.getKey();
                    int unitsSold = entry.getValue();
                    double revenue = unitsSold * product.getPrice();
                    return new SalesReportPeriod.TopSellingProduct(
                            product.getId().toString(),
                            product.getName(),
                            unitsSold,
                            revenue
                    );
                })
                .collect(Collectors.toList());
        
        return new SalesReportPeriod(
                period.getLabel(),
                totalSales,
                totalRevenue,
                averageOrderValue,
                topProducts
        );
    }
    
    /**
     * Simulate units sold for a product in a specific period
     * This is just for demo purposes - in a real app this would come from actual sales data
     */
    private int calculateSimulatedUnitsSold(Product product, PeriodBoundary period) {
        // Base units sold on product popularity and stock
        int baseUnits = product.getPopularity() != null ? product.getPopularity() / 10 : 1;
        
        // Add some randomness
        baseUnits = baseUnits * (random.nextInt(5) + 1);
        
        // Products that are in stock sell more
        if (product.isInStock()) {
            baseUnits *= 2;
        }
        
        // Higher rated products sell more
        if (product.getRating() != null && product.getRating() >= 4.0) {
            baseUnits *= 1.5;
        }
        
        return Math.max(1, baseUnits);
    }
    
    /**
     * Get filtered products for reporting
     */
    private List<Product> getFilteredProducts(ProductFilter filter) {
        if (filter == null) {
            return productRepository.findAll();
        }
        
        Specification<Product> spec = ProductSpecification.getSpecification(filter);
        return productRepository.findAll(spec);
    }
    
    /**
     * Parse date string to LocalDate
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return LocalDate.now();
        }
        
        try {
            // Try to parse as ISO date time
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME).toLocalDate();
        } catch (Exception e) {
            try {
                // Try to parse as ISO date
                return LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
            } catch (Exception e2) {
                // Default to today
                return LocalDate.now();
            }
        }
    }
    
    /**
     * Generate period boundaries based on timeframe
     */
    private List<PeriodBoundary> generatePeriods(TimeframeType timeframe, LocalDate startDate, LocalDate endDate) {
        List<PeriodBoundary> periods = new ArrayList<>();
        
        switch (timeframe) {
            case DAILY:
                long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
                for (int i = 0; i < days; i++) {
                    LocalDate date = startDate.plusDays(i);
                    periods.add(new PeriodBoundary(
                            date.format(DateTimeFormatter.ISO_DATE),
                            date,
                            date
                    ));
                }
                break;
                
            case WEEKLY:
                // Start from first day of week
                LocalDate weekStart = startDate;
                while (!weekStart.isAfter(endDate)) {
                    LocalDate weekEnd = weekStart.plusDays(6);
                    if (weekEnd.isAfter(endDate)) {
                        weekEnd = endDate;
                    }
                    
                    periods.add(new PeriodBoundary(
                            "Week " + weekStart.format(DateTimeFormatter.ISO_DATE) + " to " + weekEnd.format(DateTimeFormatter.ISO_DATE),
                            weekStart,
                            weekEnd
                    ));
                    
                    weekStart = weekStart.plusDays(7);
                }
                break;
                
            case MONTHLY:
                // Group by month
                LocalDate monthStart = startDate.withDayOfMonth(1);
                while (!monthStart.isAfter(endDate)) {
                    LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
                    if (monthEnd.isAfter(endDate)) {
                        monthEnd = endDate;
                    }
                    
                    periods.add(new PeriodBoundary(
                            monthStart.getMonth() + " " + monthStart.getYear(),
                            monthStart,
                            monthEnd
                    ));
                    
                    monthStart = monthStart.plusMonths(1);
                }
                break;
                
            case QUARTERLY:
                // Group by quarter
                int startQuarter = (startDate.getMonthValue() - 1) / 3 + 1;
                LocalDate quarterStart = LocalDate.of(startDate.getYear(), (startQuarter - 1) * 3 + 1, 1);
                
                while (!quarterStart.isAfter(endDate)) {
                    LocalDate quarterEnd = quarterStart.plusMonths(3).minusDays(1);
                    if (quarterEnd.isAfter(endDate)) {
                        quarterEnd = endDate;
                    }
                    
                    int quarter = (quarterStart.getMonthValue() - 1) / 3 + 1;
                    periods.add(new PeriodBoundary(
                            "Q" + quarter + " " + quarterStart.getYear(),
                            quarterStart,
                            quarterEnd
                    ));
                    
                    quarterStart = quarterStart.plusMonths(3);
                }
                break;
                
            case YEARLY:
                // Group by year
                LocalDate yearStart = LocalDate.of(startDate.getYear(), 1, 1);
                while (!yearStart.isAfter(endDate)) {
                    LocalDate yearEnd = yearStart.plusYears(1).minusDays(1);
                    if (yearEnd.isAfter(endDate)) {
                        yearEnd = endDate;
                    }
                    
                    periods.add(new PeriodBoundary(
                            String.valueOf(yearStart.getYear()),
                            yearStart,
                            yearEnd
                    ));
                    
                    yearStart = yearStart.plusYears(1);
                }
                break;
                
            case CUSTOM:
            default:
                // Just one period for the entire range
                periods.add(new PeriodBoundary(
                        startDate.format(DateTimeFormatter.ISO_DATE) + " to " + endDate.format(DateTimeFormatter.ISO_DATE),
                        startDate,
                        endDate
                ));
                break;
        }
        
        return periods;
    }
    
    /**
     * Helper class to represent a period boundary
     */
    private static class PeriodBoundary {
        private final String label;
        private final LocalDate startDate;
        private final LocalDate endDate;
        
        public PeriodBoundary(String label, LocalDate startDate, LocalDate endDate) {
            this.label = label;
            this.startDate = startDate;
            this.endDate = endDate;
        }
        
        public String getLabel() {
            return label;
        }
        
        public LocalDate getStartDate() {
            return startDate;
        }
        
        public LocalDate getEndDate() {
            return endDate;
        }
    }
} 
package com.example.graphql.resolver;

import com.example.graphql.dto.ProductFilter;
import com.example.graphql.dto.SalesReportPeriod;
import com.example.graphql.dto.TimeframeType;
import com.example.graphql.service.ReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL resolver for reporting queries
 */
@Controller
public class ReportingResolver {

    private final ReportingService reportingService;
    
    @Autowired
    public ReportingResolver(ReportingService reportingService) {
        this.reportingService = reportingService;
    }
    
    /**
     * Generate a sales report by timeframe
     */
    @QueryMapping
    public List<SalesReportPeriod> salesReportByTimeframe(
            @Argument TimeframeType timeframe,
            @Argument String startDate,
            @Argument String endDate,
            @Argument ProductFilter filter) {
        
        return reportingService.generateSalesReportByTimeframe(
                timeframe != null ? timeframe : TimeframeType.MONTHLY,
                startDate,
                endDate,
                filter
        );
    }
} 
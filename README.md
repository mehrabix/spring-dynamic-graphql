# Spring Boot GraphQL Dynamic Data Flow

This project demonstrates a dynamic data flow application using Spring Boot, GraphQL, and H2 database with advanced filtering, sorting, and pagination capabilities.

## Technologies Used

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA with Specifications
- Spring GraphQL
- H2 Database (in-memory)

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application:

```bash
mvn spring-boot:run
```

The application will start on port 8080 by default.

## Features

- GraphQL API for CRUD operations on Products
- Advanced filtering, sorting, and pagination
- In-memory H2 database with comprehensive demo data
- GraphiQL interface for testing GraphQL queries
- Dynamic querying capabilities
- Statistical analysis of product data

## Accessing the Application

- H2 Console: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:graphqldb`
  - Username: `sa`
  - Password: (empty)
- GraphiQL Interface: http://localhost:8080/graphiql

## Advanced Product Filtering System

The application implements a sophisticated product filtering system through GraphQL that allows for complex querying of product data. The ProductFilter type provides multiple ways to filter products based on various criteria.

### ProductFilter Fields

| Field             | Type            | Description                                                       |
|-------------------|-----------------|-------------------------------------------------------------------|
| nameContains      | String          | Filters products whose names contain the specified string (case-insensitive) |
| minPrice          | Float           | Minimum product price (inclusive)                                |
| maxPrice          | Float           | Maximum product price (inclusive)                                |
| categories        | [String]        | List of categories to include (OR condition)                     |
| inStock           | Boolean         | Filter by whether product is in stock                           |
| minRating         | Float           | Minimum product rating (inclusive)                               |
| hasTags           | [String]        | Products that have at least one of the specified tags            |
| hasPriceChanged   | Boolean         | Products that have had a price change                           |
| createdAfter      | String          | Products created after this date (ISO format)                   |
| createdBefore     | String          | Products created before this date (ISO format)                  |
| minStockQuantity  | Int             | Minimum stock quantity (inclusive)                              |
| minPopularity     | Int             | Minimum popularity rating (inclusive)                           |

### How Filtering Works

The filtering system is implemented using Spring Data JPA Specifications. When a GraphQL query includes a ProductFilter:

1. The filter is converted to a JPA Specification in `ProductSpecification.java`
2. The specification is dynamically built based on which filter fields are provided
3. The specification is passed to the Spring Data repository for efficient SQL generation
4. For non-database fields like `hasPriceChanged`, additional Java filtering is applied

### Filter Implementation Details

- **Text Filtering**: `nameContains` uses SQL `LIKE` operator with wildcards for partial matching
- **Range Filtering**: `minPrice/maxPrice` implement inclusive bounds with `>=` and `<=` operators
- **Collection Filtering**: `categories` and `hasTags` filter using SQL `IN` operator
- **Boolean Filtering**: `inStock` uses direct equality matching
- **Date Filtering**: `createdAfter/createdBefore` use SQL date comparison operations
- **Numeric Filtering**: `minStockQuantity` and `minPopularity` use `>=` comparison

## Sample GraphQL Queries

### Basic Queries

#### Get All Products

Retrieve all products with their basic details:

```graphql
query {
  allProducts {
    id
    name
    description
    price
    category
    inStock
    rating
    tags
    createdAt
    updatedAt
  }
}
```

#### Get Product by ID

Fetch a specific product using its ID:

```graphql
query {
  productById(id: "1") {
    id
    name
    description
    price
    rating
    tags
  }
}
```

#### Get Products by Category

Retrieve all products in a specific category:

```graphql
query {
  productsByCategory(category: "Electronics") {
    id
    name
    price
    category
  }
}
```

### Filtering Examples

#### Text Filtering with nameContains

Find products containing specific text in their name:

```graphql
query {
  productsWithFilter(
    filter: {
      nameContains: "pro"
    }
  ) {
    content {
      name
      price
      category
    }
    pageInfo {
      totalElements
    }
  }
}
```

#### Price Range Filtering

Find products within a specific price range:

```graphql
query {
  productsWithFilter(
    filter: {
      minPrice: 100
      maxPrice: 500
    }
  ) {
    content {
      name
      price
      category
    }
  }
}
```

#### Category Filtering

Find products in specific categories:

```graphql
query {
  productsWithFilter(
    filter: {
      categories: ["Electronics", "Mobile Phones", "Computers"]
    }
  ) {
    content {
      name
      category
      price
    }
  }
}
```

#### In-Stock Filtering

Find only products that are in stock:

```graphql
query {
  productsWithFilter(
    filter: {
      inStock: true
    }
  ) {
    content {
      name
      stockQuantity
      price
    }
  }
}
```

#### Rating Filtering

Find products with a minimum rating:

```graphql
query {
  productsWithFilter(
    filter: {
      minRating: 4.5
    }
  ) {
    content {
      name
      rating
      price
    }
  }
}
```

#### Tag Filtering

Find products with specific tags:

```graphql
query {
  productsWithFilter(
    filter: {
      hasTags: ["wireless", "bluetooth", "premium"]
    }
  ) {
    content {
      name
      tags
      price
    }
  }
}
```

#### Price Change Filtering

Find products that have had a price change:

```graphql
query {
  productsWithFilter(
    filter: {
      hasPriceChanged: true
    }
  ) {
    content {
      name
      price
    }
  }
}
```

#### Date Filtering

Find products created within a specific date range:

```graphql
query {
  productsWithFilter(
    filter: {
      createdAfter: "2023-01-01T00:00:00Z"
      createdBefore: "2023-12-31T23:59:59Z"
    }
  ) {
    content {
      name
      createdAt
    }
  }
}
```

#### Stock Quantity Filtering

Find products with a minimum stock quantity:

```graphql
query {
  productsWithFilter(
    filter: {
      minStockQuantity: 10
    }
  ) {
    content {
      name
      stockQuantity
    }
  }
}
```

#### Popularity Filtering

Find products with a minimum popularity rating:

```graphql
query {
  productsWithFilter(
    filter: {
      minPopularity: 70
    }
  ) {
    content {
      name
      popularity
    }
  }
}
```

### Combining Multiple Filters

You can combine any number of filter criteria for complex queries:

```graphql
query {
  productsWithFilter(
    filter: {
      categories: ["Electronics", "Computers"]
      minPrice: 500
      maxPrice: 2000
      minRating: 4.0
      hasTags: ["laptop", "gaming"]
      minStockQuantity: 3
      inStock: true
      minPopularity: 50
      nameContains: "pro"
    }
  ) {
    content {
      id
      name
      price
      category
      rating
      tags
      stockQuantity
      popularity
    }
    pageInfo {
      totalElements
    }
  }
}
```

### Sorting Options

#### Sort by Price (Ascending)

```graphql
query {
  productsWithFilter(
    filter: {
      categories: ["Electronics"]
    }
    sort: {
      field: PRICE
      direction: ASC
    }
  ) {
    content {
      name
      price
    }
  }
}
```

#### Sort by Price (Descending)

```graphql
query {
  productsWithFilter(
    filter: {
      categories: ["Electronics"]
    }
    sort: {
      field: PRICE
      direction: DESC
    }
  ) {
    content {
      name
      price
    }
  }
}
```

#### Sort by Name

```graphql
query {
  productsWithFilter(
    sort: {
      field: NAME
      direction: ASC
    }
  ) {
    content {
      name
      price
    }
  }
}
```

#### Sort by Rating

```graphql
query {
  productsWithFilter(
    sort: {
      field: RATING
      direction: DESC
    }
  ) {
    content {
      name
      rating
    }
  }
}
```

#### Sort by Creation Date

```graphql
query {
  productsWithFilter(
    sort: {
      field: CREATED_AT
      direction: DESC
    }
  ) {
    content {
      name
      createdAt
    }
  }
}
```

#### Sort by Popularity

```graphql
query {
  productsWithFilter(
    sort: {
      field: POPULARITY
      direction: DESC
    }
  ) {
    content {
      name
      popularity
    }
  }
}
```

#### Sort by Stock Quantity

```graphql
query {
  productsWithFilter(
    sort: {
      field: STOCK_QUANTITY
      direction: DESC
    }
  ) {
    content {
      name
      stockQuantity
    }
  }
}
```

### Pagination Examples

#### Basic Pagination

```graphql
query {
  productsWithFilter(
    page: {
      page: 0
      size: 5
    }
  ) {
    content {
      name
      price
    }
    pageInfo {
      totalElements
      totalPages
      currentPage
      size
      hasNext
      hasPrevious
    }
  }
}
```

#### Pagination with Filtering and Sorting

```graphql
query {
  productsWithFilter(
    filter: {
      minPrice: 100
      categories: ["Electronics"]
    }
    sort: {
      field: PRICE
      direction: ASC
    }
    page: {
      page: 0
      size: 10
    }
  ) {
    content {
      name
      price
      category
    }
    pageInfo {
      totalElements
      totalPages
      currentPage
      size
      hasNext
      hasPrevious
    }
  }
}
```

#### Second Page of Results

```graphql
query {
  productsWithFilter(
    filter: {
      categories: ["Electronics"]
    }
    page: {
      page: 1
      size: 5
    }
  ) {
    content {
      name
      price
    }
    pageInfo {
      currentPage
      hasNext
      hasPrevious
    }
  }
}
```

#### Custom Page Size

```graphql
query {
  productsWithFilter(
    page: {
      page: 0
      size: 20
    }
  ) {
    content {
      name
    }
    pageInfo {
      size
      totalElements
      totalPages
    }
  }
}
```

## Product Statistics

### Basic Statistics

Get basic statistics for all products:

```graphql
query {
  productStats {
    count
    avgPrice
    minPrice
    maxPrice
    inStockCount
    outOfStockCount
  }
}
```

### Statistics with Category Distribution

Get statistics with category distribution:

```graphql
query {
  productStats {
    count
    avgPrice
    minPrice
    maxPrice
    inStockCount
    outOfStockCount
    categoryDistribution {
      category
      count
      percentage
    }
  }
}
```

### Filtered Statistics

Get statistics for products matching specific filters:

```graphql
query {
  productStatsByFilter(
    filter: {
      categories: ["Electronics", "Mobile Phones"]
      minPrice: 100
    }
  ) {
    count
    avgPrice
    minPrice
    maxPrice
    inStockCount
    outOfStockCount
    categoryDistribution {
      category
      count
      percentage
    }
  }
}
```

### Price Range Distribution

Get statistics on price range distribution:

```graphql
query {
  productStatsByFilter(
    filter: {
      categories: ["Electronics"]
    }
  ) {
    count
    avgPrice
    priceRangeDistribution {
      range
      count
      percentage
    }
  }
}
```

### Rating Distribution

Get statistics on rating distribution:

```graphql
query {
  productStatsByFilter {
    count
    ratingDistribution {
      rating
      count
      percentage
    }
  }
}
```

## Dynamic Product Queries

### Basic Dynamic Query

Request specific attributes from products:

```graphql
query {
  dynamicProductQuery(
    attributes: ["name", "price", "category"]
  ) {
    id
    attributes {
      name
      value
    }
  }
}
```

### Dynamic Query with Filtering

Request specific attributes with filtering:

```graphql
query {
  dynamicProductQuery(
    attributes: ["name", "price", "category", "rating"]
    filter: {
      minRating: 4.5
      categories: ["Electronics"]
    }
  ) {
    id
    attributes {
      name
      value
    }
  }
}
```

### Computed and Nested Fields

Request computed or nested fields:

```graphql
query {
  dynamicProductQuery(
    attributes: ["name", "category", "stockStatus", "priceWithTax", "relatedProductCount"]
    filter: {
      minRating: 4.0
    }
  ) {
    id
    attributes {
      name
      value
    }
  }
}
```

### All Available Product Attributes

Request all available attributes:

```graphql
query {
  availableProductAttributes
}
```

## GraphQL Mutations

### Create Operations

#### Add a New Product

Basic product creation:

```graphql
mutation {
  addProduct(product: {
    name: "Gaming Mouse"
    description: "High-precision gaming mouse with customizable buttons"
    price: 79.99
    category: "Gaming"
    inStock: true
    rating: 4.2
    tags: ["gaming", "mouse", "rgb"]
    stockQuantity: 25
    popularity: 85
  }) {
    id
    name
    tags
  }
}
```

#### Add a Product with All Fields

Complete product creation with all available fields:

```graphql
mutation {
  addProduct(product: {
    name: "Professional Camera"
    description: "High-end DSLR camera for professional photography"
    price: 1299.99
    category: "Photography"
    inStock: true
    rating: 4.8
    tags: ["camera", "professional", "dslr", "high-resolution"]
    stockQuantity: 12
    popularity: 90
    customAttributes: {
      "sensorType": "Full-frame CMOS"
      "resolution": "45.7 MP"
      "iso": "100-25600"
      "shutterSpeed": "1/8000s"
      "weight": "780g"
    }
  }) {
    id
    name
    description
    price
    category
    customAttributes
  }
}
```

#### Bulk Add Products

Add multiple products at once:

```graphql
mutation {
  bulkAddProducts(products: [
    {
      name: "Wireless Mouse"
      description: "Comfortable wireless mouse with long battery life"
      price: 49.99
      category: "Computers"
      inStock: true
      rating: 4.0
      tags: ["mouse", "wireless", "computer"]
      stockQuantity: 30
      popularity: 75
    },
    {
      name: "Mechanical Keyboard"
      description: "Tactile mechanical keyboard with customizable switches"
      price: 129.99
      category: "Computers"
      inStock: true
      rating: 4.3
      tags: ["keyboard", "mechanical", "typing"]
      stockQuantity: 20
      popularity: 80
    },
    {
      name: "Ultra-wide Monitor"
      description: "34-inch curved ultra-wide monitor for immersive experience"
      price: 449.99
      category: "Computers"
      inStock: true
      rating: 4.6
      tags: ["monitor", "ultra-wide", "curved", "gaming"]
      stockQuantity: 15
      popularity: 85
    }
  ]) {
    id
    name
    category
  }
}
```

### Update Operations

#### Update a Product

Basic product update:

```graphql
mutation {
  updateProduct(
    id: "1",
    product: {
      name: "Premium Laptop"
      description: "Updated description with new features"
      price: 1499.99
      category: "Electronics"
      inStock: true
      tags: ["laptop", "premium", "powerful"]
    }
  ) {
    id
    name
    price
    description
    tags
  }
}
```

#### Update Specific Product Fields

Update only specific fields of a product:

```graphql
mutation {
  updateProduct(
    id: "2",
    product: {
      price: 599.99
      inStock: false
      stockQuantity: 0
    }
  ) {
    id
    name
    price
    inStock
    stockQuantity
    updatedAt
  }
}
```

#### Update Product Tags

Add or remove tags from a product:

```graphql
mutation {
  updateProductTags(
    id: "3",
    addTags: ["bestseller", "popular"],
    removeTags: ["outdated"]
  ) {
    id
    name
    tags
  }
}
```

#### Update Product Custom Attributes

Update custom attributes for a product:

```graphql
mutation {
  updateProductAttributes(
    id: "4",
    attributes: {
      "color": "Midnight Black"
      "material": "Aircraft-grade aluminum"
      "warranty": "2 years"
    }
  ) {
    id
    name
    customAttributes
  }
}
```

### Delete Operations

#### Delete a Single Product

```graphql
mutation {
  deleteProduct(id: "3")
}
```

#### Bulk Delete Products

Delete multiple products at once:

```graphql
mutation {
  bulkDeleteProducts(ids: ["5", "6", "7"])
}
```

#### Delete Products by Category

Delete all products in a specific category:

```graphql
mutation {
  deleteProductsByCategory(category: "Outdated")
}
```

### Product Relationship Operations

#### Add Related Products

Create relationships between products:

```graphql
mutation {
  addRelatedProducts(
    productId: "1",
    relatedProductIds: ["2", "3", "4"]
  ) {
    id
    name
    relatedProducts {
      id
      name
    }
  }
}
```

#### Add Frequently Bought Together Products

Specify products that are frequently bought together:

```graphql
mutation {
  addFrequentlyBoughtTogether(
    productId: "1",
    frequentlyBoughtWithIds: ["5", "8"]
  ) {
    id
    name
    frequentlyBoughtWith {
      id
      name
    }
  }
}
```

## Technical Implementation Details

### Filter System Architecture

The product filtering system is built with a layered architecture:

1. **GraphQL Layer**: Defines the filter input type in `schema.graphqls`
2. **DTO Layer**: The `ProductFilter` class maps GraphQL inputs to Java objects
3. **Specification Layer**: `ProductSpecification` converts filter criteria to JPA specifications
4. **Repository Layer**: Uses Spring Data JPA to execute the specifications
5. **Service Layer**: Orchestrates the filtering process in `ProductService` and `DynamicQueryService`

### Key Components

#### 1. ProductFilter DTO

The `ProductFilter` class in `com.example.graphql.dto` defines the filter structure:

```java
public class ProductFilter {
    private String nameContains;
    private Double minPrice;
    private Double maxPrice;
    private List<String> categories;
    private Boolean inStock;
    private Double minRating;
    private List<String> hasTags;
    private Boolean hasPriceChanged;
    private String createdAfter;
    private String createdBefore;
    private Integer minStockQuantity;
    private Integer minPopularity;
    
    // Getters and setters
}
```

#### 2. ProductSpecification

The `ProductSpecification` class handles the conversion of filter criteria to JPA specifications:

```java
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
        
        // Filter by price range
        if (filter.getMinPrice() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                root.get("price"), filter.getMinPrice()
            ));
        }
        
        if (filter.getMaxPrice() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(
                root.get("price"), filter.getMaxPrice()
            ));
        }
        
        // Filter by categories
        if (filter.getCategories() != null && !filter.getCategories().isEmpty()) {
            predicates.add(root.get("category").in(filter.getCategories()));
        }
        
        // Filter by in-stock status
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
        
        // Filter by minimum stock quantity
        if (filter.getMinStockQuantity() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                root.get("stockQuantity"), filter.getMinStockQuantity()
            ));
        }
        
        // Filter by minimum popularity
        if (filter.getMinPopularity() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                root.get("popularity"), filter.getMinPopularity()
            ));
        }
        
        // Filter by creation date range
        if (filter.getCreatedAfter() != null) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(filter.getCreatedAfter(), 
                    DateTimeFormatter.ISO_DATE_TIME);
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("createdAt"), dateTime
                ));
            } catch (Exception e) {
                // Handle date parsing error
            }
        }
        
        if (filter.getCreatedBefore() != null) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(filter.getCreatedBefore(), 
                    DateTimeFormatter.ISO_DATE_TIME);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("createdAt"), dateTime
                ));
            } catch (Exception e) {
                // Handle date parsing error
            }
        }
        
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
}
```

#### 3. Dynamic Query Processing

For more complex filtering that can't be efficiently done at the database level, the `DynamicQueryService` applies additional filtering in memory:

```java
private boolean matchesFilter(Product product, ProductFilter filter) {
    // Check name contains filter
    if (filter.getNameContains() != null && !filter.getNameContains().isEmpty()) {
        if (!product.getName().toLowerCase().contains(filter.getNameContains().toLowerCase())) {
            return false;
        }
    }
    
    // Check price range filter
    if (filter.getMinPrice() != null && product.getPrice() < filter.getMinPrice()) {
        return false;
    }
    
    if (filter.getMaxPrice() != null && product.getPrice() > filter.getMaxPrice()) {
        return false;
    }
    
    // Check categories filter
    if (filter.getCategories() != null && !filter.getCategories().isEmpty()) {
        if (!filter.getCategories().contains(product.getCategory())) {
            return false;
        }
    }
    
    // Check in-stock filter
    if (filter.getInStock() != null && product.isInStock() != filter.getInStock()) {
        return false;
    }
    
    // Check minimum rating filter
    if (filter.getMinRating() != null && product.getRating() < filter.getMinRating()) {
        return false;
    }
    
    // Check tags filter
    if (filter.getHasTags() != null && !filter.getHasTags().isEmpty()) {
        if (product.getTags() == null || product.getTags().isEmpty()) {
            return false;
        }
        
        boolean hasMatchingTag = false;
        for (String tag : filter.getHasTags()) {
            if (product.getTags().contains(tag)) {
                hasMatchingTag = true;
                break;
            }
        }
        
        if (!hasMatchingTag) {
            return false;
        }
    }
    
    // Check price changed filter
    if (filter.getHasPriceChanged() != null && 
        filter.getHasPriceChanged() && 
        !product.hasPriceChanged()) {
        return false;
    }
    
    // Check minimum stock quantity
    if (filter.getMinStockQuantity() != null && 
        product.getStockQuantity() < filter.getMinStockQuantity()) {
        return false;
    }
    
    // Check minimum popularity
    if (filter.getMinPopularity() != null && 
        product.getPopularity() < filter.getMinPopularity()) {
        return false;
    }
    
    return true;
}
```

### Advanced Features

#### 1. Multi-field Searching

The filtering system supports searching across multiple fields simultaneously using different criteria. Each filter condition is added as a separate predicate to the JPA specification.

#### 2. Customizable Querying

The `dynamicProductQuery` endpoint allows clients to specify exactly which attributes they want returned, helping minimize payload size and optimize performance.

#### 3. Statistical Analysis

The filtering system integrates with the statistics system, allowing for analytics on filtered subsets of products through the `productStatsByFilter` endpoint.

### Extensibility

The filtering system is designed to be extensible:

1. **Adding New Filters**: Simply add new fields to the `ProductFilter` class and update the GraphQL schema
2. **Enhancing Filter Logic**: Extend the `getSpecification` method in `ProductSpecification` to handle new criteria
3. **Custom Filter Operations**: For complex filtering not supported by JPA, add custom logic in the `matchesFilter` method

### Performance Considerations

- Database-level filtering is preferred where possible for better performance
- For complex filters like `hasPriceChanged`, in-memory filtering is used
- Pagination is implemented to limit result set size for large queries
- Proper indexing of filtered columns is important for production deployments

## Project Structure

- `src/main/java/com/example/graphql/`
  - `GraphQLDemoApplication.java` - Main application class
  - `controller/` - GraphQL controllers for handling queries and mutations
  - `model/` - JPA entity classes
  - `repository/` - Spring Data JPA repositories and specifications
  - `service/` - Business logic services
  - `dto/` - Data transfer objects for GraphQL input types
  - `config/` - Configuration classes including data loader and GraphQL scalar configuration
  - `resolver/` - GraphQL resolvers for queries and mutations

- `src/main/resources/`
  - `application.properties` - Application configuration
  - `graphql/schema.graphqls` - GraphQL schema definition
  - `data.sql` - Initial data script for H2 database

## Testing

The application includes comprehensive test coverage:

- Unit tests for DTO classes
- Integration tests for repositories and specifications
- End-to-end tests for GraphQL queries and mutations

## Error Handling

The application implements robust error handling:

- GraphQL-specific error responses
- Validation of input data
- Proper exception handling throughout the application

## Security Considerations

When deploying to production, consider:

- Adding authentication and authorization
- Implementing rate limiting
- Securing GraphQL against common vulnerabilities
- Protecting sensitive data

## Performance Optimization

The application is optimized for performance:

- Efficient database query generation
- Pagination to handle large result sets
- Proper use of caching
- Query depth and complexity analysis 

## Advanced Reporting Features

The system can be extended with the following features to create a powerful reporting platform:

### Time-Based Aggregations

Analyze data across different time periods with built-in time aggregation functions:

```graphql
query {
  salesReportByTimeframe(
    timeframe: MONTHLY,
    startDate: "2023-01-01",
    endDate: "2023-12-31",
    filter: {
      categories: ["Electronics"]
    }
  ) {
    period
    totalSales
    totalRevenue
    averageOrderValue
    topSellingProducts {
      productId
      productName
      unitsSold
      revenue
    }
  }
}
```

### Comparative Analytics

Compare metrics across different time periods or categories:

```graphql
query {
  comparativeReport(
    dimension: CATEGORY,
    metric: REVENUE,
    periods: [
      {name: "Q1", startDate: "2023-01-01", endDate: "2023-03-31"},
      {name: "Q2", startDate: "2023-04-01", endDate: "2023-06-30"}
    ]
  ) {
    dimensionValue
    metricValues {
      periodName
      value
      percentageChange
    }
  }
}
```

### Data Export Capabilities

Export report data in various formats:

```graphql
mutation {
  exportReport(
    reportType: PRODUCT_PERFORMANCE,
    format: CSV,
    filter: {
      minPopularity: 70,
      categories: ["Electronics"]
    },
    dateRange: {
      startDate: "2023-01-01",
      endDate: "2023-12-31"
    }
  ) {
    downloadUrl
    expiresAt
    recordCount
  }
}
```

### Custom Metric Definitions

Define custom metrics for specialized reporting needs:

```graphql
mutation {
  createCustomMetric(
    name: "ProfitMargin",
    description: "Calculated profit margin percentage",
    formula: "(revenue - cost) / revenue * 100",
    applicableEntities: ["Product", "Category"]
  ) {
    id
    name
    formula
  }
}

query {
  productReport(
    metrics: ["Revenue", "UnitsSold", "ProfitMargin"],
    filter: {
      categories: ["Electronics"]
    }
  ) {
    products {
      name
      metrics {
        name
        value
        formattedValue
      }
    }
  }
}
```

### Scheduled Reports

Set up automated report generation and delivery:

```graphql
mutation {
  scheduleReport(
    name: "Weekly Sales Summary",
    reportType: SALES_SUMMARY,
    schedule: {
      frequency: WEEKLY,
      dayOfWeek: MONDAY,
      time: "09:00"
    },
    recipients: ["reporting@example.com"],
    format: PDF,
    filter: {
      categories: ["Electronics", "Home Appliances"]
    }
  ) {
    id
    name
    schedule {
      frequency
      nextExecutionTime
    }
  }
}
```

### Data Visualization Endpoints

Generate visualization-ready data structures:

```graphql
query {
  visualizationData(
    type: TIME_SERIES,
    metrics: ["Revenue", "UnitsSold"],
    dimensions: ["Category"],
    timeframe: DAILY,
    startDate: "2023-01-01",
    endDate: "2023-01-31",
    filter: {
      minPrice: 100
    }
  ) {
    labels
    datasets {
      label
      data
      color
    }
  }
}
```

### Trend Analysis

Analyze trends and patterns in your data:

```graphql
query {
  trendAnalysis(
    metric: SALES,
    timeframe: MONTHLY,
    startDate: "2022-01-01",
    endDate: "2023-12-31",
    filter: {
      categories: ["Electronics"]
    }
  ) {
    trend {
      direction
      percentageChange
      significance
    }
    seasonality {
      exists
      pattern
      peakPeriods
    }
    forecast {
      periods {
        period
        predictedValue
        confidenceInterval {
          lower
          upper
        }
      }
    }
  }
}
```

### Geographic Distribution

Analyze data across geographic regions:

```graphql
query {
  geographicDistribution(
    metric: REVENUE,
    geoLevel: COUNTRY,
    period: {
      startDate: "2023-01-01",
      endDate: "2023-12-31"
    },
    filter: {
      categories: ["Electronics"]
    }
  ) {
    regions {
      code
      name
      value
      percentageOfTotal
    }
    topRegions {
      name
      value
    }
    bottomRegions {
      name
      value
    }
  }
}
```

### Customer Segmentation Analysis

Segment customers based on their behavior and attributes:

```graphql
query {
  customerSegmentAnalysis(
    segmentBy: ["purchaseFrequency", "averageOrderValue", "preferredCategory"],
    period: {
      startDate: "2023-01-01",
      endDate: "2023-12-31"
    }
  ) {
    segments {
      name
      customerCount
      percentageOfTotal
      averageMetrics {
        name
        value
      }
      topProducts {
        name
        unitsSold
      }
    }
  }
}
```

### Implementation Considerations

To implement these reporting features efficiently:

1. **Optimize Aggregation Queries**: Use database features like window functions, materialized views, or pre-aggregation tables for faster reporting
2. **Cache Report Results**: Implement caching for reports that are expensive to generate but infrequently changed
3. **Background Processing**: Run complex reports asynchronously and notify users when complete
4. **Data Denormalization**: Consider denormalizing data for reporting to improve query performance
5. **Implement Incremental Updates**: For large datasets, update reports incrementally rather than regenerating completely

These advanced reporting features can be implemented using the existing architecture by extending:

- GraphQL schema with new query and mutation types
- Creating specialized service classes for different report types
- Leveraging JPA for simple aggregations and native SQL for complex aggregations
- Adding scheduled tasks for report generation
- Implementing export services for different file formats 
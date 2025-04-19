# Spring Boot GraphQL Dynamic Data Flow

This project demonstrates a dynamic data flow application using Spring Boot, GraphQL, and H2 database with advanced filtering, sorting, and pagination capabilities.

## Technologies Used

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA with Specifications
- Spring GraphQL with WebSocket Subscriptions
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
- Real-time updates through GraphQL Subscriptions via WebSocket
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
- GraphQL WebSocket Endpoint: ws://localhost:8080/graphql

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
    stockQuantity
    popularity
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
    category
    inStock
    rating
    tags
    stockQuantity
    popularity
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
    inStock
    stockQuantity
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
      nameContains: "phone"
    }
  ) {
    content {
      id
      name
      price
      category
    }
    pageInfo {
      totalElements
      totalPages
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
    pageInfo {
      totalElements
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
    pageInfo {
      totalElements
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
      inStock
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
      hasTags: ["premium", "wireless", "bluetooth"]
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
      price
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
      inStock
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
      minPopularity: 75
    }
  ) {
    content {
      name
      popularity
      price
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
      minPrice: 200
      maxPrice: 1000
      minRating: 4.0
      hasTags: ["premium", "wireless"]
      minStockQuantity: 5
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
      inStock
    }
    pageInfo {
      totalElements
      totalPages
      currentPage
      hasNext
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
      category
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
      category
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
      price
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
      price
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
      price
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
      inStock
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
      price
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
    attributes: ["id", "name", "price", "category"]
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
      inStock: true
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

## Relationship Queries

### Related Products

Find products related to a specific product:

```graphql
query {
  relatedProducts(id: 1, maxResults: 5) {
    id
    name
    price
    category
    rating
  }
}
```

### Frequently Bought Together

Find products frequently bought together with a specific product:

```graphql
query {
  frequentlyBoughtTogether(id: 1, maxResults: 3) {
    id
    name
    price
    category
  }
}
```

## GraphQL Mutations

### Create Operations

#### Add a New Product

Basic product creation:

```graphql
mutation {
  addProduct(product: {
    name: "Ultra HD Gaming Monitor"
    description: "32-inch curved gaming monitor with 144Hz refresh rate"
    price: 449.99
    category: "Electronics"
    inStock: true
    rating: 4.7
    tags: ["gaming", "monitor", "ultra-hd", "curved"]
    stockQuantity: 15
  }) {
    id
    name
    price
    category
  }
}
```

> **Note:** While the Product entity supports custom attributes internally, the current ProductInput schema doesn't directly expose them through the mutation API. Custom attributes can be managed through dedicated API endpoints once a product is created.

#### Bulk Add Products

Add multiple products at once:

```graphql
mutation {
  bulkAddProducts(products: [
    {
      name: "Wireless Keyboard"
      description: "Ergonomic wireless keyboard with backlight"
      price: 59.99
      category: "Computers"
      inStock: true
      rating: 4.2
      tags: ["keyboard", "wireless", "ergonomic"]
      stockQuantity: 25
    },
    {
      name: "Bluetooth Speaker"
      description: "Portable Bluetooth speaker with 20h battery life"
      price: 89.99
      category: "Audio"
      inStock: true
      rating: 4.5
      tags: ["speaker", "bluetooth", "portable"]
      stockQuantity: 30
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
    updatedAt
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

#### Partial Updates (Planned Feature)

> **Note:** This is a planned feature that may not be fully implemented in the current version.

The GraphQL schema includes support for partial updates through the `updateProductAttributes` mutation:

```graphql
mutation {
  updateProductAttributes(
    id: "1",
    attributes: {
      price: 999.99
      inStock: true
      stockQuantity: 25
      rating: 4.8
    }
  ) {
    id
    name
    price
    inStock
    stockQuantity
    rating
    updatedAt
  }
}
```

This would be useful for updates where you only want to modify specific fields without affecting others. The server applies a patch-like update rather than a full replacement.

### Custom Attributes Support

Although not directly exposed in the GraphQL input types, the Product entity supports custom attributes internally through a Map<String, String> field. This allows for storing key-value pairs that aren't part of the standard schema.

The internal model provides methods to manage these attributes:

```java
// Add a custom attribute
product.addCustomAttribute("resolution", "4K");

// Get a custom attribute
String resolution = product.getCustomAttribute("resolution");

// Get all custom attributes
Map<String, String> allAttributes = product.getCustomAttributes();
```

Custom attributes can be retrieved when querying products:

```graphql
query {
  productById(id: "5") {
    id
    name
    price
    # The following would return a Map of all custom attributes
    # if any are defined for this product
  }
}
```

Through the dynamic query interface, you could potentially access these custom attributes if the implementation supports it:

```graphql
query {
  dynamicProductQuery(
    attributes: ["name", "price", "powerConsumption", "brewingTechnology"]
    filter: {
      categories: ["Kitchen Appliances"]
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

The application implements robust error handling using GraphQL's standardized error format. Here are examples of common error responses:

### Entity Not Found Error

When requesting a non-existent product:

```graphql
query {
  productById(id: "999") {
    id
    name
  }
}
```

Response:
```json
{
  "data": {
    "productById": null
  },
  "errors": [
    {
      "message": "Product not found with ID: 999",
      "locations": [{"line": 2, "column": 3}],
      "path": ["productById"],
      "extensions": {
        "classification": "NOT_FOUND"
      }
    }
  ]
}
```

### Validation Error

When providing invalid input:

```graphql
mutation {
  addProduct(product: {
    name: "",  # Empty name is invalid
    price: -10.0,  # Negative price is invalid
    category: "Electronics"
  }) {
    id
  }
}
```

Response:
```json
{
  "data": {
    "addProduct": null
  },
  "errors": [
    {
      "message": "Product name cannot be empty",
      "locations": [{"line": 2, "column": 3}],
      "path": ["addProduct"],
      "extensions": {
        "classification": "ValidationError",
        "field": "name"
      }
    },
    {
      "message": "Price must be greater than zero",
      "locations": [{"line": 2, "column": 3}],
      "path": ["addProduct"],
      "extensions": {
        "classification": "ValidationError",
        "field": "price"
      }
    }
  ]
}
```

### Authorization Error (For Future Implementation)

In a secured version of the application:

```graphql
mutation {
  deleteProduct(id: "1")
}
```

Response:
```json
{
  "data": null,
  "errors": [
    {
      "message": "Not authorized to delete products",
      "locations": [{"line": 2, "column": 3}],
      "path": ["deleteProduct"],
      "extensions": {
        "classification": "FORBIDDEN"
      }
    }
  ]
}
```

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

## Advanced Reporting Features (Example Extensions)

> **Note:** The following reporting features are examples of how the system could be extended, but may not be fully implemented in the current version.

The system could be extended with the following features to create a powerful reporting platform:

### TimeframeType Examples

The system could support different timeframe types for reports through a `TimeframeType` enum:

#### DAILY

Daily timeframes would break down data by individual days:

```graphql
query {
  salesReportByTimeframe(
    timeframe: DAILY,
    startDate: "2023-01-01",
    endDate: "2023-01-07",
    filter: {
      categories: ["Electronics"]
    }
  ) {
    period  # "2023-01-01", "2023-01-02", etc.
    totalSales
    totalRevenue
  }
}
```

#### WEEKLY

Weekly timeframes would group data by 7-day periods:

```graphql
query {
  salesReportByTimeframe(
    timeframe: WEEKLY,
    startDate: "2023-01-01",
    endDate: "2023-02-28",
    filter: {
      categories: ["Electronics"]
    }
  ) {
    period  # "Week 2023-01-01 to 2023-01-07", "Week 2023-01-08 to 2023-01-14", etc.
    totalSales
    totalRevenue
  }
}
```

#### MONTHLY

Monthly timeframes would aggregate data by calendar months:

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
    period  # "JANUARY 2023", "FEBRUARY 2023", etc.
    totalSales
    totalRevenue
  }
}
```

#### QUARTERLY

Quarterly timeframes would aggregate data by fiscal quarters:

```graphql
query {
  salesReportByTimeframe(
    timeframe: QUARTERLY,
    startDate: "2023-01-01",
    endDate: "2023-12-31",
    filter: {
      categories: ["Electronics"]
    }
  ) {
    period  # "Q1 2023", "Q2 2023", etc.
    totalSales
    totalRevenue
  }
}
```

#### YEARLY

Yearly timeframes would aggregate data by calendar years:

```graphql
query {
  salesReportByTimeframe(
    timeframe: YEARLY,
    startDate: "2020-01-01",
    endDate: "2023-12-31",
    filter: {
      categories: ["Electronics"]
    }
  ) {
    period  # "2020", "2021", "2022", "2023"
    totalSales
    totalRevenue
  }
}
```

#### CUSTOM

Custom timeframes would treat the entire date range as a single period:

```graphql
query {
  salesReportByTimeframe(
    timeframe: CUSTOM,
    startDate: "2023-04-15",
    endDate: "2023-05-15",
    filter: {
      categories: ["Electronics"]
    }
  ) {
    period  # "2023-04-15 to 2023-05-15"
    totalSales
    totalRevenue
  }
}
```

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

## GraphQL Subscriptions

The application provides real-time updates through GraphQL Subscriptions over WebSocket. This allows clients to receive immediate notifications when specific events occur on the server.

### Available Subscriptions

1. **Product Updates** - Receive updates whenever a product is modified, added, or deleted:
   ```graphql
   subscription ProductUpdated {
     productUpdated {
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
       stockQuantity
       popularity
       operation
     }
   }
   ```

2. **Price Change Notifications** - Get notified when product prices change beyond a specified threshold:
   ```graphql
   subscription ProductPriceChanged {
     productPriceChanged(minPriceDifference: null) {
       oldPrice
       newPrice
       percentChange
       product {
         id
         name
         price
         category
         operation
       }
     }
   }
   ```

3. **Low Stock Alerts** - Monitor inventory levels and receive alerts when products are running low:
   ```graphql
   subscription LowStockAlert {
     lowStockAlert(threshold: 5) {
       id
       name
       stockQuantity
       category
       inStock
       operation
     }
   }
   ```

### Understanding Operation Types

All subscription responses include an `operation` field that indicates what happened to the product:

- **ADDED** - A new product was created
- **UPDATED** - An existing product was modified
- **DELETED** - A product was deleted

When a product is deleted, you'll receive a notification with:
- `operation: "DELETED"`
- `inStock: false`
- `stockQuantity: 0`

### Subscription Response Examples

#### Product Added Example:

```json
{
  "data": {
    "productUpdated": {
      "id": "14",
      "name": "New Smartphone",
      "price": 699.99,
      "category": "Electronics",
      "inStock": true,
      "stockQuantity": 25,
      "operation": "ADDED"
    }
  }
}
```

#### Product Updated Example:

```json
{
  "data": {
    "productUpdated": {
      "id": "3",
      "name": "Premium Laptop",
      "price": 1299.99,
      "category": "Electronics",
      "inStock": true,
      "stockQuantity": 12,
      "operation": "UPDATED"
    }
  }
}
```

#### Product Deleted Example:

```json
{
  "data": {
    "productUpdated": {
      "id": "13",
      "name": "Wireless Laser Printer",
      "price": 249.99,
      "category": "Office",
      "inStock": false,
      "stockQuantity": 0,
      "operation": "DELETED"
    }
  }
}
```

### Setting Up Subscriptions

To use GraphQL subscriptions in your client application:

1. **Connect to the WebSocket endpoint**:
   The WebSocket endpoint is available at: `ws://localhost:8080/graphql`

2. **Set up a subscription client**:
   Use a GraphQL client that supports subscriptions, such as Apollo Client, urql, or graphql-ws.

3. **Subscribe to the desired events**:
   Use one of the subscription queries listed above to start receiving real-time updates.

### Subscription Options

#### Price Change Threshold

For the `productPriceChanged` subscription, you can set a minimum price difference threshold:

- Use `minPriceDifference: 5.0` to only receive notifications when the price changes by $5 or more
- Use `minPriceDifference: null` to receive notifications for any price change

#### Low Stock Threshold

For the `lowStockAlert` subscription, you can set a stock level threshold:

- Use `threshold: 5` (default) to receive alerts when stock drops to 5 or lower
- Customize the threshold based on your inventory management needs


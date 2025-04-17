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

### Sample Filter Queries

#### Basic Price and Category Filtering

```graphql
query {
  productsWithFilter(
    filter: {
      minPrice: 100
      maxPrice: 500
      categories: ["Electronics", "Mobile Phones"]
      inStock: true
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
    }
  }
}
```

#### Advanced Text and Tag Filtering

```graphql
query {
  productsWithFilter(
    filter: {
      nameContains: "pro"
      hasTags: ["wireless", "premium"]
      minRating: 4.0
    }
  ) {
    content {
      name
      price
      tags
      rating
    }
  }
}
```

#### Stock and Popularity Filtering

```graphql
query {
  productsWithFilter(
    filter: {
      minStockQuantity: 5
      minPopularity: 50
      inStock: true
    }
  ) {
    content {
      name
      stockQuantity
      popularity
    }
  }
}
```

#### Date-based Filtering

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

#### Complex Multi-criteria Filtering

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
      id
      name
      price
      category
      rating
      tags
      stockQuantity
    }
    pageInfo {
      totalElements
      totalPages
      currentPage
    }
  }
}
```

## Product Statistics with Filters

The API also supports generating product statistics based on filter criteria:

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

## Dynamic Product Queries

The `dynamicProductQuery` allows requesting specific attributes from products with filtering:

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
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
        
        // Other filters...
        
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
}
```

#### 3. Dynamic Query Processing

For more complex filtering that can't be efficiently done at the database level, the `DynamicQueryService` applies additional filtering in memory:

```java
private boolean matchesFilter(Product product, ProductFilter filter) {
    // Various filter conditions
    
    // Example: Filter by price changed
    if (filter.getHasPriceChanged() != null && 
        filter.getHasPriceChanged() && 
        !product.hasPriceChanged()) {
        return false;
    }
    
    // More conditions...
    
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

## Sample GraphQL Queries

### Basic Queries

#### Get All Products

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

### Advanced Queries with Filtering, Sorting and Pagination

#### Get Products with Price Range

```graphql
query {
  productsWithFilter(
    filter: {
      minPrice: 100
      maxPrice: 500
      inStock: true
    }
    sort: {
      field: PRICE
      direction: ASC
    }
    page: {
      page: 0
      size: 5
    }
  ) {
    content {
      id
      name
      price
      category
      rating
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

#### Search Products by Name and Category

```graphql
query {
  productsWithFilter(
    filter: {
      nameContains: "smart"
      categories: ["Electronics", "Mobile Phones", "Smart Home"]
    }
  ) {
    content {
      id
      name
      category
      price
      tags
    }
    pageInfo {
      totalElements
    }
  }
}
```

#### Get Top-Rated Products

```graphql
query {
  productsWithFilter(
    filter: {
      minRating: 4.5
    }
    sort: {
      field: RATING
      direction: DESC
    }
  ) {
    content {
      name
      rating
      category
    }
  }
}
```

#### Find Products by Tags

```graphql
query {
  productsWithFilter(
    filter: {
      hasTags: ["wireless", "bluetooth"]
    }
  ) {
    content {
      name
      category
      tags
    }
  }
}
```

## Sample GraphQL Mutations

### Basic Mutations

#### Add a New Product

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
  }) {
    id
    name
    tags
  }
}
```

#### Update a Product

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
    tags
  }
}
```

#### Delete a Product

```graphql
mutation {
  deleteProduct(id: "3")
}
```

### Bulk Operations

#### Bulk Add Products

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
    },
    {
      name: "Mechanical Keyboard"
      description: "Tactile mechanical keyboard with customizable switches"
      price: 129.99
      category: "Computers"
      inStock: true
      rating: 4.3
      tags: ["keyboard", "mechanical", "typing"]
    }
  ]) {
    id
    name
  }
}
```

#### Bulk Delete Products

```graphql
mutation {
  bulkDeleteProducts(ids: ["5", "6", "7"])
}
```

## Project Structure

- `src/main/java/com/example/graphql/`
  - `GraphQLDemoApplication.java` - Main application class
  - `controller/` - GraphQL controllers for handling queries and mutations
  - `model/` - JPA entity classes
  - `repository/` - Spring Data JPA repositories and specifications
  - `service/` - Business logic services
  - `dto/` - Data transfer objects for GraphQL input types
  - `config/` - Configuration classes including data loader and GraphQL scalar configuration

- `src/main/resources/`
  - `application.properties` - Application configuration
  - `graphql/schema.graphqls` - GraphQL schema definition 
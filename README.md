# Spring Boot GraphQL Dynamic Data Flow

This project demonstrates a dynamic data flow application using Spring Boot, GraphQL, and H2 database.

## Technologies Used

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- Spring GraphQL
- H2 Database (in-memory)
- Lombok

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
- In-memory H2 database with demo data
- GraphiQL interface for testing GraphQL queries

## Accessing the Application

- H2 Console: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:graphqldb`
  - Username: `sa`
  - Password: (empty)
- GraphiQL Interface: http://localhost:8080/graphiql

## Sample GraphQL Queries

### Get All Products

```graphql
query {
  allProducts {
    id
    name
    description
    price
    category
    inStock
  }
}
```

### Get Product by ID

```graphql
query {
  productById(id: 1) {
    id
    name
    description
    price
  }
}
```

### Get Products by Category

```graphql
query {
  productsByCategory(category: "Electronics") {
    id
    name
    price
  }
}
```

## Sample GraphQL Mutations

### Add a New Product

```graphql
mutation {
  addProduct(product: {
    name: "Gaming Mouse",
    description: "High-precision gaming mouse",
    price: 79.99,
    category: "Gaming",
    inStock: true
  }) {
    id
    name
    description
  }
}
```

### Update a Product

```graphql
mutation {
  updateProduct(
    id: 1,
    product: {
      name: "Premium Laptop",
      description: "Updated description",
      price: 1499.99,
      category: "Electronics",
      inStock: true
    }
  ) {
    id
    name
    price
  }
}
```

### Delete a Product

```graphql
mutation {
  deleteProduct(id: 3)
}
```

## Project Structure

- `src/main/java/com/example/graphql/`
  - `GraphQLDemoApplication.java` - Main application class
  - `controller/` - GraphQL controllers for handling queries and mutations
  - `model/` - JPA entity classes
  - `repository/` - Spring Data JPA repositories
  - `service/` - Business logic services
  - `dto/` - Data transfer objects for GraphQL input types
  - `config/` - Configuration classes including data loader

- `src/main/resources/`
  - `application.properties` - Application configuration
  - `graphql/schema.graphqls` - GraphQL schema definition 
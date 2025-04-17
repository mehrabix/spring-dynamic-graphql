package com.example.graphql.controller;

import com.example.graphql.dto.PageInfo;
import com.example.graphql.dto.PageInput;
import com.example.graphql.dto.ProductFilter;
import com.example.graphql.dto.ProductInput;
import com.example.graphql.dto.ProductPage;
import com.example.graphql.dto.ProductSort;
import com.example.graphql.model.Product;
import com.example.graphql.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private Product testProduct;
    private List<Product> testProducts;
    private ProductInput testProductInput;

    @BeforeEach
    void setUp() {
        // Setup test product
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(99.99);
        testProduct.setCategory("Test Category");
        testProduct.setInStock(true);
        testProduct.setRating(4.5f);
        testProduct.setTags(Arrays.asList("tag1", "tag2"));
        testProduct.setStockQuantity(10);

        testProducts = Collections.singletonList(testProduct);

        // Setup test product input
        testProductInput = new ProductInput();
        testProductInput.setName("Test Product");
        testProductInput.setDescription("Test Description");
        testProductInput.setPrice(99.99);
        testProductInput.setCategory("Test Category");
        testProductInput.setInStock(true);
        testProductInput.setRating(4.5f);
        testProductInput.setTags(Arrays.asList("tag1", "tag2"));
    }

    @Test
    void testAllProducts() {
        when(productService.getAllProducts()).thenReturn(testProducts);
        
        List<Product> result = productController.allProducts();
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getName());
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void testProductById() {
        when(productService.getProductById(1L)).thenReturn(Optional.of(testProduct));
        
        Product result = productController.productById("1");
        
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Product", result.getName());
        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    void testProductsByCategory() {
        when(productService.getProductsByCategory("Test Category")).thenReturn(testProducts);
        
        List<Product> result = productController.productsByCategory("Test Category");
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Category", result.get(0).getCategory());
        verify(productService, times(1)).getProductsByCategory("Test Category");
    }

    @Test
    void testProductsWithFilter() {
        ProductFilter filter = new ProductFilter();
        filter.setInStock(true);
        
        ProductSort sort = new ProductSort();
        sort.setField(ProductSort.ProductSortField.PRICE);
        sort.setDirection(ProductSort.SortDirection.DESC);
        
        PageInput pageInput = new PageInput();
        pageInput.setPage(0);
        pageInput.setSize(10);
        
        PageInfo pageInfo = new PageInfo(1, 1, 0, 10);
        ProductPage productPage = new ProductPage(testProducts, pageInfo);
        
        when(productService.getProductsWithFilter(any(ProductFilter.class), any(ProductSort.class), any(PageInput.class)))
                .thenReturn(productPage);
        
        ProductPage result = productController.productsWithFilter(filter, sort, pageInput);
        
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getPageInfo().getTotalElements());
        verify(productService, times(1)).getProductsWithFilter(any(ProductFilter.class), any(ProductSort.class), any(PageInput.class));
    }

    @Test
    void testAddProduct() {
        when(productService.addProduct(any(Product.class))).thenReturn(testProduct);
        
        Product result = productController.addProduct(testProductInput);
        
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        verify(productService, times(1)).addProduct(any(Product.class));
    }

    @Test
    void testUpdateProduct() {
        when(productService.updateProduct(anyLong(), any(Product.class))).thenReturn(Optional.of(testProduct));
        
        Product result = productController.updateProduct("1", testProductInput);
        
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        verify(productService, times(1)).updateProduct(anyLong(), any(Product.class));
    }

    @Test
    void testDeleteProduct() {
        when(productService.deleteProduct(1L)).thenReturn(true);
        
        Boolean result = productController.deleteProduct("1");
        
        assertTrue(result);
        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void testBulkAddProducts() {
        List<ProductInput> inputs = Collections.singletonList(testProductInput);
        when(productService.bulkAddProducts(anyList())).thenReturn(testProducts);
        
        List<Product> result = productController.bulkAddProducts(inputs);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productService, times(1)).bulkAddProducts(anyList());
    }

    @Test
    void testBulkDeleteProducts() {
        List<String> ids = Collections.singletonList("1");
        when(productService.bulkDeleteProducts(anyList())).thenReturn(1);
        
        Integer result = productController.bulkDeleteProducts(ids);
        
        assertEquals(1, result);
        verify(productService, times(1)).bulkDeleteProducts(anyList());
    }
} 
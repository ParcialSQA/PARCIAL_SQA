package com.company.inventory.controller;

import com.company.inventory.model.Product;
import com.company.inventory.respnose.ProductResponseRest;
import com.company.inventory.services.IProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TEST-07: Cobertura de ProductRestController")
public class ProductRestControllerTest {

    @Mock
    private IProductService productService;

    @InjectMocks
    private ProductRestController productController;

    @Test
    @DisplayName("Cubre búsqueda de todos los productos")
    void testSearchProducts() {
        ProductResponseRest responseRest = new ProductResponseRest();
        ResponseEntity<ProductResponseRest> response = new ResponseEntity<>(responseRest, HttpStatus.OK);
        when(productService.search()).thenReturn(response);

        ResponseEntity<ProductResponseRest> result = productController.search();
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    @DisplayName("Cubre búsqueda por ID de producto")
    void testSearchProductById() {
        ProductResponseRest responseRest = new ProductResponseRest();
        ResponseEntity<ProductResponseRest> response = new ResponseEntity<>(responseRest, HttpStatus.OK);
        when(productService.searchById(1L)).thenReturn(response);

        ResponseEntity<ProductResponseRest> result = productController.searchById(1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    @DisplayName("Cubre búsqueda por nombre de producto")
    void testSearchProductByName() {
        ProductResponseRest responseRest = new ProductResponseRest();
        ResponseEntity<ProductResponseRest> response = new ResponseEntity<>(responseRest, HttpStatus.OK);
        when(productService.searchByName("Teclado")).thenReturn(response);

        ResponseEntity<ProductResponseRest> result = productController.searchByName("Teclado");
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    @DisplayName("Cubre guardado de producto con imagen")
    void testSaveProduct() throws IOException {
        ProductResponseRest responseRest = new ProductResponseRest();
        ResponseEntity<ProductResponseRest> response = new ResponseEntity<>(responseRest, HttpStatus.OK);
        when(productService.save(any(Product.class), eq(1L))).thenReturn(response);

        MockMultipartFile file = new MockMultipartFile("picture", "test.jpg", "image/jpeg", "image bytes".getBytes());

        ResponseEntity<ProductResponseRest> result = productController.save(file, "Laptop", 1000, 50, 1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    @DisplayName("Cubre actualización de producto con imagen")
    void testUpdateProduct() throws IOException {
        ProductResponseRest responseRest = new ProductResponseRest();
        ResponseEntity<ProductResponseRest> response = new ResponseEntity<>(responseRest, HttpStatus.OK);
        when(productService.update(any(Product.class), eq(1L), eq(10L))).thenReturn(response);

        MockMultipartFile file = new MockMultipartFile("picture", "test.jpg", "image/jpeg", "image bytes".getBytes());

        ResponseEntity<ProductResponseRest> result = productController.update(file, "Laptop", 1000, 50, 1L, 10L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    @DisplayName("Cubre eliminación de producto")
    void testDeleteProduct() {
        ProductResponseRest responseRest = new ProductResponseRest();
        ResponseEntity<ProductResponseRest> response = new ResponseEntity<>(responseRest, HttpStatus.OK);
        when(productService.deleteById(1L)).thenReturn(response);

        ResponseEntity<ProductResponseRest> result = productController.deleteById(1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    @DisplayName("Cubre exportación a Excel de producto")
    void testExportToExcel() throws IOException {
        ProductResponseRest responseRest = new ProductResponseRest();
        responseRest.getProduct().setProducts(new ArrayList<>());
        ResponseEntity<ProductResponseRest> response = new ResponseEntity<>(responseRest, HttpStatus.OK);
        when(productService.search()).thenReturn(response);

        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        productController.exportToExcel(mockHttpServletResponse);

        assertEquals("application/octet-stream", mockHttpServletResponse.getContentType());
        assertNotNull(mockHttpServletResponse.getHeader("Content-Disposition"));
    }
}

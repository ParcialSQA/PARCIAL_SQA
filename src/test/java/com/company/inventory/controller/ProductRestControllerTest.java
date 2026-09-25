package com.company.inventory.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import com.company.inventory.model.Product;
import com.company.inventory.respnose.ProductResponseRest;
import com.company.inventory.services.IProductService;

@ExtendWith(MockitoExtension.class)
public class ProductRestControllerTest {

    @Mock
    private IProductService productService;

    @InjectMocks
    private ProductRestController productController;

    @Test
    public void testSearchProducts_Success() {
        ProductResponseRest responseRest = new ProductResponseRest();
        ResponseEntity<ProductResponseRest> responseEntity = new ResponseEntity<>(responseRest, HttpStatus.OK);

        when(productService.search()).thenReturn(responseEntity);

        ResponseEntity<ProductResponseRest> result = productController.search();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(productService, times(1)).search();
    }

    @Test
    public void testSearchProductById_Success() {
        Long id = 1L;
        ProductResponseRest responseRest = new ProductResponseRest();
        ResponseEntity<ProductResponseRest> responseEntity = new ResponseEntity<>(responseRest, HttpStatus.OK);

        when(productService.searchById(id)).thenReturn(responseEntity);

        ResponseEntity<ProductResponseRest> result = productController.searchById(id);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(productService, times(1)).searchById(id);
    }

    @Test
    public void testSearchProductByName_Success() {
        String name = "Test";
        ProductResponseRest responseRest = new ProductResponseRest();
        ResponseEntity<ProductResponseRest> responseEntity = new ResponseEntity<>(responseRest, HttpStatus.OK);

        when(productService.searchByName(name)).thenReturn(responseEntity);

        ResponseEntity<ProductResponseRest> result = productController.searchByName(name);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(productService, times(1)).searchByName(name);
    }

    @Test
    public void testSaveProduct_Success() throws IOException {
        MockMultipartFile picture = new MockMultipartFile("picture", "test.jpg", "image/jpeg", "test image content".getBytes());
        String name = "Test Product";
        int price = 100;
        int account = 10;
        Long categoryId = 1L;

        ProductResponseRest responseRest = new ProductResponseRest();
        ResponseEntity<ProductResponseRest> responseEntity = new ResponseEntity<>(responseRest, HttpStatus.OK);

        when(productService.save(any(Product.class), eq(categoryId))).thenReturn(responseEntity);

        ResponseEntity<ProductResponseRest> result = productController.save(picture, name, price, account, categoryId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(productService, times(1)).save(any(Product.class), eq(categoryId));
    }

    @Test
    public void testDeleteProduct_Success() {
        Long id = 1L;
        ProductResponseRest responseRest = new ProductResponseRest();
        ResponseEntity<ProductResponseRest> responseEntity = new ResponseEntity<>(responseRest, HttpStatus.OK);

        when(productService.deleteById(id)).thenReturn(responseEntity);

        ResponseEntity<ProductResponseRest> result = productController.deleteById(id);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(productService, times(1)).deleteById(id);
    }
}

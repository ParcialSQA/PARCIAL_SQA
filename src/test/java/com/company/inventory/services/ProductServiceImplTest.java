package com.company.inventory.services;

import com.company.inventory.dao.ICategoryDao;
import com.company.inventory.dao.IProductDao;
import com.company.inventory.model.Category;
import com.company.inventory.model.Product;
import com.company.inventory.respnose.ProductResponseRest;
import com.company.inventory.util.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TEST-04: Pruebas Unitarias con Mockito para ProductServiceImpl")
public class ProductServiceImplTest {

    @Mock
    private IProductDao productDao;

    @Mock
    private ICategoryDao categoryDao;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Electrónica");

        product = new Product();
        product.setId(10L);
        product.setName("Teclado Mecánico");
        product.setPrice(150);
        product.setAccount(50);
        product.setCategory(category);
        
        // Se asegura que al intentar descomprimir la imagen, la misma sea válida para evitar DataFormatException o NullPointerException
        byte[] sampleImage = "fake-image-bytes".getBytes();
        product.setPicture(Util.compressZLib(sampleImage));
    }

    @Test
    @DisplayName("TC-PRD-01: Guardar producto exitoso cuando la categoría existe")
    void testSaveProduct_Success() {
        when(categoryDao.findById(1L)).thenReturn(Optional.of(category));
        when(productDao.save(any(Product.class))).thenReturn(product);

        ResponseEntity<ProductResponseRest> response = productService.save(product, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(categoryDao, times(1)).findById(1L);
        verify(productDao, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("TC-PRD-02: Guardar producto fallido - Categoría no encontrada (Retorna 404 y no guarda)")
    void testSaveProduct_CategoryNotFound() {
        when(categoryDao.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<ProductResponseRest> response = productService.save(product, 99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(categoryDao, times(1)).findById(99L);
        verify(productDao, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("TC-PRD-03: Buscar producto por ID existente - Retorna 200 OK")
    void testSearchById_Success() {
        when(productDao.findById(10L)).thenReturn(Optional.of(product));

        ResponseEntity<ProductResponseRest> response = productService.searchById(10L);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "El estatus debe ser 200 OK");
        assertNotNull(response.getBody());
        verify(productDao, times(1)).findById(10L);
    }

    @Test
    @DisplayName("TC-PRD-04: Buscar producto por nombre - Retorna lista coincidente")
    void testSearchByName_Success() {
        List<Product> list = new ArrayList<>();
        list.add(product);
        when(productDao.findByNameContainingIgnoreCase(anyString())).thenReturn(list);

        ResponseEntity<ProductResponseRest> response = productService.searchByName("Teclado");

        assertEquals(HttpStatus.OK, response.getStatusCode(), "El estatus debe ser 200 OK");
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getProduct());
        assertEquals(1, response.getBody().getProduct().getProducts().size());
        verify(productDao, times(1)).findByNameContainingIgnoreCase("Teclado");
    }

    @Test
    @DisplayName("TC-PRD-05: Eliminar producto por ID - Retorna 200 OK")
    void testDeleteById_Success() {
        doNothing().when(productDao).deleteById(10L);

        ResponseEntity<ProductResponseRest> response = productService.deleteById(10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productDao, times(1)).deleteById(10L);
    }
}

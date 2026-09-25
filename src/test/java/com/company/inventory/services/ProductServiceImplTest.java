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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TEST-04: Pruebas Unitarias Mockito para ProductServiceImpl")
class ProductServiceImplTest {

    @Mock
    private IProductDao productDao;

    @Mock
    private ICategoryDao categoryDao;

    @InjectMocks
    private ProductServiceImpl productService;

    private Category category;
    private Product product;
    private byte[] compressedPicture;

    @BeforeEach
    void setUp() {

        category = new Category();
        category.setId(1L);
        category.setName("Electronica");
        category.setDescription("Productos electronicos");

        byte[] originalPicture = "fake-image-bytes".getBytes();
        compressedPicture = Util.compressZLib(originalPicture);

        product = new Product();
        product.setId(10L);
        product.setName("Teclado Mecanico");
        product.setPrice(150);
        product.setAccount(50);
        product.setCategory(category);
        product.setPicture(compressedPicture);
    }

    // =========================================================
    // SAVE
    // =========================================================

    @Test
    @DisplayName("TC-PRD-01: Guardar producto exitosamente con categoria valida")
    void testSaveProduct_Success() {

        // Given
        when(categoryDao.findById(1L))
                .thenReturn(Optional.of(category));

        when(productDao.save(any(Product.class)))
                .thenReturn(product);

        // When
        ResponseEntity<ProductResponseRest> response =
                productService.save(product, 1L);

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertNotNull(
                response.getBody()
                        .getProduct()
                        .getProducts()
        );

        assertEquals(
                1,
                response.getBody()
                        .getProduct()
                        .getProducts()
                        .size()
        );

        assertEquals(
                "Teclado Mecanico",
                response.getBody()
                        .getProduct()
                        .getProducts()
                        .get(0)
                        .getName()
        );

        verify(categoryDao, times(1))
                .findById(1L);

        verify(productDao, times(1))
                .save(any(Product.class));
    }

    @Test
    @DisplayName("TC-PRD-02: No guardar producto si categoria no existe")
    void testSaveProduct_CategoryNotFound() {

        // Given
        when(categoryDao.findById(99L))
                .thenReturn(Optional.empty());

        // When
        ResponseEntity<ProductResponseRest> response =
                productService.save(product, 99L);

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        verify(categoryDao, times(1))
                .findById(99L);

        verify(productDao, never())
                .save(any(Product.class));
    }

    @Test
    @DisplayName("TC-PRD-03: DAO devuelve null al guardar producto")
    void testSaveProduct_ReturnsNull() {

        // Given
        when(categoryDao.findById(1L))
                .thenReturn(Optional.of(category));

        when(productDao.save(any(Product.class)))
                .thenReturn(null);

        // When
        ResponseEntity<ProductResponseRest> response =
                productService.save(product, 1L);

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        verify(categoryDao, times(1))
                .findById(1L);

        verify(productDao, times(1))
                .save(any(Product.class));
    }

    @Test
    @DisplayName("TC-PRD-04: Error de BD al guardar producto")
    void testSaveProduct_ExceptionError() {

        // Given
        when(categoryDao.findById(1L))
                .thenReturn(Optional.of(category));

        when(productDao.save(any(Product.class)))
                .thenThrow(new RuntimeException("DB Down"));

        // When
        ResponseEntity<ProductResponseRest> response =
                productService.save(product, 1L);

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );

        verify(categoryDao, times(1))
                .findById(1L);

        verify(productDao, times(1))
                .save(any(Product.class));
    }

    // =========================================================
    // SEARCH BY ID
    // =========================================================

    @Test
    @DisplayName("TC-PRD-05: Buscar producto por ID exitosamente")
    void testSearchById_Success() {

        // Given
        when(productDao.findById(10L))
                .thenReturn(Optional.of(product));

        // When
        ResponseEntity<ProductResponseRest> response =
                productService.searchById(10L);

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertEquals(
                1,
                response.getBody()
                        .getProduct()
                        .getProducts()
                        .size()
        );

        assertEquals(
                "Teclado Mecanico",
                response.getBody()
                        .getProduct()
                        .getProducts()
                        .get(0)
                        .getName()
        );

        verify(productDao, times(1))
                .findById(10L);
    }

    @Test
    @DisplayName("TC-PRD-06: Buscar producto por ID no existente")
    void testSearchById_NotFound() {

        // Given
        when(productDao.findById(99L))
                .thenReturn(Optional.empty());

        // When
        ResponseEntity<ProductResponseRest> response =
                productService.searchById(99L);

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        verify(productDao, times(1))
                .findById(99L);
    }

    @Test
    @DisplayName("TC-PRD-07: Error de BD al buscar producto por ID")
    void testSearchById_ExceptionError() {

        // Given
        when(productDao.findById(10L))
                .thenThrow(new RuntimeException("DB Down"));

        // When
        ResponseEntity<ProductResponseRest> response =
                productService.searchById(10L);

        // Then
        assertNotNull(response);

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );

        verify(productDao, times(1))
                .findById(10L);
    }

    // =========================================================
    // SEARCH BY NAME
    // =========================================================

    @Test
    @DisplayName("TC-PRD-08: Buscar producto por nombre exitosamente")
    void testSearchByName_Success() {

        // Given
        List<Product> products = new ArrayList<>();
        products.add(product);

        when(productDao.findByNameContainingIgnoreCase("Teclado"))
                .thenReturn(products);

        // When
        ResponseEntity<ProductResponseRest> response =
                productService.searchByName("Teclado");

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertEquals(
                1,
                response.getBody()
                        .getProduct()
                        .getProducts()
                        .size()
        );

        verify(productDao, times(1))
                .findByNameContainingIgnoreCase("Teclado");
    }

    @Test
    @DisplayName("TC-PRD-09: Buscar producto por nombre sin resultados")
    void testSearchByName_NotFound() {

        // Given
        when(productDao.findByNameContainingIgnoreCase("Inexistente"))
                .thenReturn(new ArrayList<>());

        // When
        ResponseEntity<ProductResponseRest> response =
                productService.searchByName("Inexistente");

        // Then
        assertNotNull(response);

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        verify(productDao, times(1))
                .findByNameContainingIgnoreCase("Inexistente");
    }

    @Test
    @DisplayName("TC-PRD-10: Error de BD al buscar producto por nombre")
    void testSearchByName_ExceptionError() {

        // Given
        when(productDao.findByNameContainingIgnoreCase("Teclado"))
                .thenThrow(new RuntimeException("DB Down"));

        // When
        ResponseEntity<ProductResponseRest> response =
                productService.searchByName("Teclado");

        // Then
        assertNotNull(response);

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );

        verify(productDao, times(1))
                .findByNameContainingIgnoreCase("Teclado");
    }

    // =========================================================
    // SEARCH ALL
    // =========================================================

    @Test
    @DisplayName("TC-PRD-11: Listar todos los productos exitosamente")
    void testSearch_Success() {

        // Given
        List<Product> products = new ArrayList<>();
        products.add(product);

        when(productDao.findAll())
                .thenReturn(products);

        // When
        ResponseEntity<ProductResponseRest> response =
                productService.search();

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertEquals(
                1,
                response.getBody()
                        .getProduct()
                        .getProducts()
                        .size()
        );

        verify(productDao, times(1))
                .findAll();
    }

    @Test
    @DisplayName("TC-PRD-12: Listar productos sin resultados")
    void testSearch_NotFound() {

        // Given
        when(productDao.findAll())
                .thenReturn(new ArrayList<>());

        // When
        ResponseEntity<ProductResponseRest> response =
                productService.search();

        // Then
        assertNotNull(response);

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        verify(productDao, times(1))
                .findAll();
    }

    @Test
    @DisplayName("TC-PRD-13: Error de BD al listar productos")
    void testSearch_ExceptionError() {

        // Given
        when(productDao.findAll())
                .thenThrow(new RuntimeException("DB Down"));

        // When
        ResponseEntity<ProductResponseRest> response =
                productService.search();

        // Then
        assertNotNull(response);

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );

        verify(productDao, times(1))
                .findAll();
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Test
    @DisplayName("TC-PRD-14: Eliminar producto exitosamente")
    void testDeleteById_Success() {

        // Given
        doNothing()
                .when(productDao)
                .deleteById(10L);

        // When
        ResponseEntity<ProductResponseRest> response =
                productService.deleteById(10L);

        // Then
        assertNotNull(response);

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        verify(productDao, times(1))
                .deleteById(10L);
    }

    @Test
    @DisplayName("TC-PRD-15: Error de BD al eliminar producto")
    void testDeleteById_ExceptionError() {

        // Given
        doThrow(new RuntimeException("DB Down"))
                .when(productDao)
                .deleteById(10L);

        // When
        ResponseEntity<ProductResponseRest> response =
                productService.deleteById(10L);

        // Then
        assertNotNull(response);

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );

        verify(productDao, times(1))
                .deleteById(10L);
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    @DisplayName("TC-PRD-16: Actualizar producto exitosamente")
    void testUpdate_Success() {

        // Given
        Product newData = new Product();
        newData.setName("Teclado Gamer");
        newData.setPrice(200);
        newData.setAccount(80);
        newData.setPicture(compressedPicture);

        when(categoryDao.findById(1L))
                .thenReturn(Optional.of(category));

        when(productDao.findById(10L))
                .thenReturn(Optional.of(product));

        when(productDao.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ResponseEntity<ProductResponseRest> response =
                productService.update(newData, 1L, 10L);

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertEquals(
                "Teclado Gamer",
                response.getBody()
                        .getProduct()
                        .getProducts()
                        .get(0)
                        .getName()
        );

        verify(categoryDao, times(1))
                .findById(1L);

        verify(productDao, times(1))
                .findById(10L);

        verify(productDao, times(1))
                .save(any(Product.class));
    }

    @Test
    @DisplayName("TC-PRD-17: No actualizar si categoria no existe")
    void testUpdate_CategoryNotFound() {

        // Given
        Product newData = new Product();
        newData.setName("Teclado Gamer");

        when(categoryDao.findById(99L))
                .thenReturn(Optional.empty());

        // When
        ResponseEntity<ProductResponseRest> response =
                productService.update(newData, 99L, 10L);

        // Then
        assertNotNull(response);

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        verify(categoryDao, times(1))
                .findById(99L);

        verify(productDao, never())
                .findById(anyLong());

        verify(productDao, never())
                .save(any(Product.class));
    }

    @Test
    @DisplayName("TC-PRD-18: No actualizar si producto no existe")
    void testUpdate_ProductNotFound() {

        // Given
        Product newData = new Product();
        newData.setName("Teclado Gamer");

        when(categoryDao.findById(1L))
                .thenReturn(Optional.of(category));

        when(productDao.findById(99L))
                .thenReturn(Optional.empty());

        // When
        ResponseEntity<ProductResponseRest> response =
                productService.update(newData, 1L, 99L);

        // Then
        assertNotNull(response);

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        verify(categoryDao, times(1))
                .findById(1L);

        verify(productDao, times(1))
                .findById(99L);

        verify(productDao, never())
                .save(any(Product.class));
    }

    @Test
    @DisplayName("TC-PRD-19: Error de BD al actualizar producto")
    void testUpdate_ExceptionError() {

        // Given
        Product newData = new Product();
        newData.setName("Teclado Gamer");

        when(categoryDao.findById(1L))
                .thenThrow(new RuntimeException("DB Down"));

        // When
        ResponseEntity<ProductResponseRest> response =
                productService.update(newData, 1L, 10L);

        // Then
        assertNotNull(response);

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );

        verify(categoryDao, times(1))
                .findById(1L);

        verify(productDao, never())
                .save(any(Product.class));
    }
}
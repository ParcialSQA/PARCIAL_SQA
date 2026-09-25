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
        
        byte[] sampleImage = "sample-data".getBytes();
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
    @DisplayName("Buscar producto por ID no existente - Retorna 404 NOT FOUND")
    void testSearchById_NotFound() {
        when(productDao.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<ProductResponseRest> response = productService.searchById(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(productDao, times(1)).findById(99L);
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
    @DisplayName("Buscar producto por nombre sin coincidencias - Retorna 404 NOT FOUND")
    void testSearchByName_NotFound() {
        when(productDao.findByNameContainingIgnoreCase(anyString())).thenReturn(new ArrayList<>());

        ResponseEntity<ProductResponseRest> response = productService.searchByName("Desconocido");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(productDao, times(1)).findByNameContainingIgnoreCase("Desconocido");
    }

    @Test
    @DisplayName("TC-PRD-05: Eliminar producto por ID - Retorna 200 OK")
    void testDeleteById_Success() {
        doNothing().when(productDao).deleteById(10L);

        ResponseEntity<ProductResponseRest> response = productService.deleteById(10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productDao, times(1)).deleteById(10L);
    }

    // -- Nuevos Test Negativos (Manejo de Excepciones para Cobertura del catch) --

    @Test
    @DisplayName("Guardar producto - Excepción en Base de Datos (Retorna 500 INTERNAL_SERVER_ERROR)")
    void testSaveProduct_DatabaseError() {
        when(categoryDao.findById(1L)).thenReturn(Optional.of(category));
        when(productDao.save(any(Product.class))).thenThrow(new RuntimeException("DB Error"));

        ResponseEntity<ProductResponseRest> response = productService.save(product, 1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(categoryDao, times(1)).findById(1L);
        verify(productDao, times(1)).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Guardar producto fallido - Producto guardado es null (Retorna 400 BAD REQUEST)")
    void testSaveProduct_NullSaved() {
        when(categoryDao.findById(1L)).thenReturn(Optional.of(category));
        when(productDao.save(any(Product.class))).thenReturn(null);

        ResponseEntity<ProductResponseRest> response = productService.save(product, 1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(categoryDao, times(1)).findById(1L);
        verify(productDao, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Buscar producto por ID - Excepción en Base de Datos (Retorna 500 INTERNAL_SERVER_ERROR)")
    void testSearchById_DatabaseError() {
        when(productDao.findById(anyLong())).thenThrow(new RuntimeException("DB Error"));

        ResponseEntity<ProductResponseRest> response = productService.searchById(10L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(productDao, times(1)).findById(10L);
    }

    @Test
    @DisplayName("Buscar producto por nombre - Excepción en Base de Datos (Retorna 500 INTERNAL_SERVER_ERROR)")
    void testSearchByName_DatabaseError() {
        when(productDao.findByNameContainingIgnoreCase(anyString())).thenThrow(new RuntimeException("DB Error"));

        ResponseEntity<ProductResponseRest> response = productService.searchByName("Teclado");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(productDao, times(1)).findByNameContainingIgnoreCase("Teclado");
    }

    @Test
    @DisplayName("Eliminar producto por ID - Excepción en Base de Datos (Retorna 500 INTERNAL_SERVER_ERROR)")
    void testDeleteById_DatabaseError() {
        doThrow(new RuntimeException("DB Error")).when(productDao).deleteById(anyLong());

        ResponseEntity<ProductResponseRest> response = productService.deleteById(10L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(productDao, times(1)).deleteById(10L);
    }
    
    @Test
    @DisplayName("Buscar todos los productos - Retorna 200 OK")
    void testSearchAll_Success() {
        List<Product> list = new ArrayList<>();
        list.add(product);
        when(productDao.findAll()).thenReturn(list);

        ResponseEntity<ProductResponseRest> response = productService.search();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productDao, times(1)).findAll();
    }
    
    @Test
    @DisplayName("Buscar todos los productos - Sin resultados (Retorna 404 NOT FOUND)")
    void testSearchAll_NotFound() {
        when(productDao.findAll()).thenReturn(new ArrayList<>());

        ResponseEntity<ProductResponseRest> response = productService.search();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(productDao, times(1)).findAll();
    }

    @Test
    @DisplayName("Buscar todos los productos - Excepción en Base de Datos (Retorna 500 INTERNAL_SERVER_ERROR)")
    void testSearchAll_DatabaseError() {
        when(productDao.findAll()).thenThrow(new RuntimeException("DB Error"));

        ResponseEntity<ProductResponseRest> response = productService.search();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(productDao, times(1)).findAll();
    }

    @Test
    @DisplayName("Actualizar producto - Éxito (Retorna 200 OK)")
    void testUpdateProduct_Success() {
        when(categoryDao.findById(1L)).thenReturn(Optional.of(category));
        when(productDao.findById(10L)).thenReturn(Optional.of(product));
        when(productDao.save(any(Product.class))).thenReturn(product);

        ResponseEntity<ProductResponseRest> response = productService.update(product, 1L, 10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productDao, times(1)).findById(10L);
        verify(productDao, times(1)).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Actualizar producto - Category Not Found (Retorna 404 NOT FOUND)")
    void testUpdateProduct_CategoryNotFound() {
        when(categoryDao.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<ProductResponseRest> response = productService.update(product, 99L, 10L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(categoryDao, times(1)).findById(99L);
        verify(productDao, never()).findById(anyLong());
    }
    
    @Test
    @DisplayName("Actualizar producto - Product Not Found (Retorna 404 NOT FOUND)")
    void testUpdateProduct_ProductNotFound() {
        when(categoryDao.findById(1L)).thenReturn(Optional.of(category));
        when(productDao.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<ProductResponseRest> response = productService.update(product, 1L, 99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(categoryDao, times(1)).findById(1L);
        verify(productDao, times(1)).findById(99L);
    }
    
    @Test
    @DisplayName("Actualizar producto - Producto guardado es null (Retorna 400 BAD REQUEST)")
    void testUpdateProduct_NullSaved() {
        when(categoryDao.findById(1L)).thenReturn(Optional.of(category));
        when(productDao.findById(10L)).thenReturn(Optional.of(product));
        when(productDao.save(any(Product.class))).thenReturn(null);

        ResponseEntity<ProductResponseRest> response = productService.update(product, 1L, 10L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(productDao, times(1)).findById(10L);
        verify(productDao, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Actualizar producto - Excepción en Base de Datos (Retorna 500 INTERNAL_SERVER_ERROR)")
    void testUpdateProduct_DatabaseError() {
        when(categoryDao.findById(1L)).thenReturn(Optional.of(category));
        when(productDao.findById(10L)).thenThrow(new RuntimeException("DB Error"));

        ResponseEntity<ProductResponseRest> response = productService.update(product, 1L, 10L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(categoryDao, times(1)).findById(1L);
        verify(productDao, times(1)).findById(10L);
    }
}
package com.company.inventory.controller;

import com.company.inventory.model.Product;
import com.company.inventory.respnose.ProductResponseRest;
import com.company.inventory.services.IProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class ProductRestControllerTest {

    @Mock
    private IProductService service;

    @InjectMocks
    private ProductRestController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    void testSearchProducts_Success() throws Exception {
        // Given
        Product product = new Product();
        product.setId(1L);
        product.setName("Arroz");
        when(service.search()).thenReturn(
                new ResponseEntity<>(responseWith(product), HttpStatus.OK));

        // When / Then
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product.products[0].name").value("Arroz"));

        verify(service, times(1)).search();
    }

    @Test
    void testSearchById_Success() throws Exception {
        // Given
        Long id = 1L;
        Product product = new Product();
        product.setId(id);
        product.setName("Arroz");
        ProductResponseRest response = responseWith(product);
        when(service.searchById(id)).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        // When / Then
        mockMvc.perform(get("/api/v1/products/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product.products[0].name").value("Arroz"));

        verify(service, times(1)).searchById(id);
    }

    @Test
    void testSearchByName_Success() throws Exception {
        // Given
        String name = "Arroz";
        Product product = new Product();
        product.setId(1L);
        product.setName(name);
        when(service.searchByName(name)).thenReturn(
                new ResponseEntity<>(responseWith(product), HttpStatus.OK));

        // When / Then
        mockMvc.perform(get("/api/v1/products/filter/{name}", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product.products[0].name").value(name));

        verify(service, times(1)).searchByName(name);
    }

    @Test
    void testSaveProduct_Success() throws Exception {
        // Given
        Long categoryId = 2L;
        MockMultipartFile picture = new MockMultipartFile(
                "picture", "arroz.png", "image/png", new byte[]{1, 2, 3});
        when(service.save(any(Product.class), eq(categoryId))).thenReturn(
                new ResponseEntity<>(new ProductResponseRest(), HttpStatus.OK));

        // When / Then
        mockMvc.perform(multipart("/api/v1/products")
                        .file(picture)
                        .param("name", "Arroz")
                        .param("price", "10")
                        .param("account", "5")
                        .param("categoryId", categoryId.toString()))
                .andExpect(status().isOk());

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(service, times(1)).save(productCaptor.capture(), eq(categoryId));
        Product savedProduct = productCaptor.getValue();
        assertEquals("Arroz", savedProduct.getName());
        assertEquals(10, savedProduct.getPrice());
        assertEquals(5, savedProduct.getAccount());
        assertNotNull(savedProduct.getPicture());
    }

    @Test
    void testSearchById_NotFound() throws Exception {
        // Given
        Long id = 99L;
        when(service.searchById(id)).thenReturn(
                new ResponseEntity<>(new ProductResponseRest(), HttpStatus.NOT_FOUND));

        // When / Then
        mockMvc.perform(get("/api/v1/products/{id}", id))
                .andExpect(status().isNotFound());

        verify(service, times(1)).searchById(id);
    }

    @Test
    void testDeleteProduct_Success() throws Exception {
        // Given
        Long id = 1L;
        when(service.deleteById(id)).thenReturn(
                new ResponseEntity<>(new ProductResponseRest(), HttpStatus.OK));

        // When / Then
        mockMvc.perform(delete("/api/v1/products/{id}", id))
                .andExpect(status().isOk());

        verify(service, times(1)).deleteById(id);
    }

    private ProductResponseRest responseWith(Product product) {
        ProductResponseRest response = new ProductResponseRest();
        response.getProduct().setProducts(List.of(product));
        return response;
    }
}

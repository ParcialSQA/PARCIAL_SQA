package com.company.inventory.model;

import com.company.inventory.respnose.CategoryResponse;
import com.company.inventory.respnose.CategoryResponseRest;
import com.company.inventory.respnose.ProductResponse;
import com.company.inventory.respnose.ProductResponseRest;
import com.company.inventory.respnose.ResponseRest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TEST-05: Cobertura Exhaustiva de Modelos y DTOs")
public class ModelCoverageTest {

    // =====================================================================
    // Category model
    // =====================================================================

    @Test
    @DisplayName("Category: constructor por defecto + todos los setters/getters")
    void testCategoryDefaultConstructor() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electrónica");
        category.setDescription("Artículos de tecnología");

        assertEquals(1L, category.getId());
        assertEquals("Electrónica", category.getName());
        assertEquals("Artículos de tecnología", category.getDescription());
    }

    @Test
    @DisplayName("Category: constructor con argumentos (@AllArgsConstructor)")
    void testCategoryAllArgsConstructor() {
        Category category = new Category(2L, "Ropa", "Prendas de vestir");

        assertEquals(2L, category.getId());
        assertEquals("Ropa", category.getName());
        assertEquals("Prendas de vestir", category.getDescription());
    }

    @Test
    @DisplayName("Category: equals(), hashCode() y toString() generados por @Data")
    void testCategoryEqualsHashCodeToString() {
        Category c1 = new Category(1L, "A", "desc");
        Category c2 = new Category(1L, "A", "desc");
        Category c3 = new Category(2L, "B", "other");

        assertEquals(c1, c2);
        assertNotEquals(c1, c3);
        assertEquals(c1.hashCode(), c2.hashCode());
        assertNotNull(c1.toString());
        assertTrue(c1.toString().contains("Electrónica") || c1.toString().contains("A"));
    }

    // =====================================================================
    // Product model
    // =====================================================================

    @Test
    @DisplayName("Product: constructor por defecto + todos los setters/getters incluyendo picture")
    void testProductDefaultConstructor() {
        Category category = new Category(1L, "Tech", "desc");

        Product product = new Product();
        product.setId(10L);
        product.setName("Laptop");
        product.setPrice(1200);
        product.setAccount(50);
        product.setCategory(category);
        product.setPicture(new byte[]{1, 2, 3, 4, 5});

        assertEquals(10L, product.getId());
        assertEquals("Laptop", product.getName());
        assertEquals(1200, product.getPrice());
        assertEquals(50, product.getAccount());
        assertNotNull(product.getCategory());
        assertEquals(1L, product.getCategory().getId());
        assertNotNull(product.getPicture());
        assertArrayEquals(new byte[]{1, 2, 3, 4, 5}, product.getPicture());
    }

    @Test
    @DisplayName("Product: picture null permanece null")
    void testProductPictureNull() {
        Product product = new Product();
        product.setPicture(null);
        assertNull(product.getPicture());
    }

    @Test
    @DisplayName("Product: price y account con valores extremos")
    void testProductPriceAccountBoundary() {
        Product product = new Product();
        product.setPrice(0);
        product.setAccount(0);
        assertEquals(0, product.getPrice());
        assertEquals(0, product.getAccount());

        product.setPrice(Integer.MAX_VALUE);
        product.setAccount(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, product.getPrice());
        assertEquals(Integer.MAX_VALUE, product.getAccount());
    }

    @Test
    @DisplayName("Product: equals(), hashCode() y toString() generados por @Data")
    void testProductEqualsHashCodeToString() {
        Product p1 = new Product();
        p1.setId(1L);
        p1.setName("Mouse");

        Product p2 = new Product();
        p2.setId(1L);
        p2.setName("Mouse");

        Product p3 = new Product();
        p3.setId(2L);
        p3.setName("Teclado");

        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotNull(p1.toString());
    }

    // =====================================================================
    // ResponseRest
    // =====================================================================

    @Test
    @DisplayName("ResponseRest: setMetadata(String,String,String) y getMetadata()")
    void testResponseRestMetadata() {
        ResponseRest response = new ResponseRest();

        response.setMetadata("ok", "00", "Éxito");
        assertEquals(1, response.getMetadata().size());
        assertEquals("ok", response.getMetadata().get(0).get("type"));
        assertEquals("00", response.getMetadata().get(0).get("code"));
        assertEquals("Éxito", response.getMetadata().get(0).get("date"));

        response.setMetadata("nok", "-1", "Error interno");
        assertEquals(2, response.getMetadata().size());
        assertEquals("nok", response.getMetadata().get(1).get("type"));
        assertEquals("-1", response.getMetadata().get(1).get("code"));
        assertEquals("Error interno", response.getMetadata().get(1).get("date"));
    }

    // =====================================================================
    // CategoryResponse
    // =====================================================================

    @Test
    @DisplayName("CategoryResponse: setCategory() y getCategory()")
    void testCategoryResponse() {
        CategoryResponse cr = new CategoryResponse();
        assertNull(cr.getCategory()); // Lombok @Data, lista no inicializada por defecto

        List<Category> cats = new ArrayList<>();
        cats.add(new Category(1L, "Abarrotes", "desc1"));
        cats.add(new Category(2L, "Lácteos", "desc2"));
        cr.setCategory(cats);

        assertNotNull(cr.getCategory());
        assertEquals(2, cr.getCategory().size());
        assertEquals("Abarrotes", cr.getCategory().get(0).getName());
        assertEquals("Lácteos", cr.getCategory().get(1).getName());
    }

    @Test
    @DisplayName("CategoryResponse: equals(), hashCode() y toString() generados por @Data")
    void testCategoryResponseEqualsHashCode() {
        CategoryResponse cr1 = new CategoryResponse();
        CategoryResponse cr2 = new CategoryResponse();
        assertEquals(cr1, cr2);
        assertEquals(cr1.hashCode(), cr2.hashCode());
        assertNotNull(cr1.toString());
    }

    // =====================================================================
    // CategoryResponseRest
    // =====================================================================

    @Test
    @DisplayName("CategoryResponseRest: getCategoryResponse(), setCategoryResponse() y setMetadata()")
    void testCategoryResponseRest() {
        CategoryResponseRest rest = new CategoryResponseRest();

        // El campo viene inicializado por defecto
        assertNotNull(rest.getCategoryResponse());

        // Reemplazamos con nuevo objeto
        CategoryResponse newCr = new CategoryResponse();
        newCr.setCategory(new ArrayList<>());
        rest.setCategoryResponse(newCr);

        assertNotNull(rest.getCategoryResponse());
        assertNotNull(rest.getCategoryResponse().getCategory());
        assertEquals(0, rest.getCategoryResponse().getCategory().size());

        rest.setMetadata("ok", "00", "Todo correcto");
        assertEquals(1, rest.getMetadata().size());
        assertEquals("ok", rest.getMetadata().get(0).get("type"));
    }

    // =====================================================================
    // ProductResponse
    // =====================================================================

    @Test
    @DisplayName("ProductResponse: setProducts() y getProducts()")
    void testProductResponse() {
        ProductResponse pr = new ProductResponse();
        assertNull(pr.getProducts()); // no inicializado por defecto con @Data

        List<Product> prods = new ArrayList<>();
        Product p = new Product();
        p.setId(5L);
        p.setName("Monitor");
        prods.add(p);
        pr.setProducts(prods);

        assertNotNull(pr.getProducts());
        assertEquals(1, pr.getProducts().size());
        assertEquals("Monitor", pr.getProducts().get(0).getName());
    }

    @Test
    @DisplayName("ProductResponse: equals(), hashCode() y toString() generados por @Data")
    void testProductResponseEqualsHashCode() {
        ProductResponse pr1 = new ProductResponse();
        ProductResponse pr2 = new ProductResponse();
        assertEquals(pr1, pr2);
        assertEquals(pr1.hashCode(), pr2.hashCode());
        assertNotNull(pr1.toString());
    }

    // =====================================================================
    // ProductResponseRest
    // =====================================================================

    @Test
    @DisplayName("ProductResponseRest: getProduct(), setProduct() y setMetadata()")
    void testProductResponseRest() {
        ProductResponseRest rest = new ProductResponseRest();

        // campo viene inicializado por defecto
        assertNotNull(rest.getProduct());

        // Reemplazamos con nuevo objeto
        ProductResponse newPr = new ProductResponse();
        newPr.setProducts(new ArrayList<>());
        rest.setProduct(newPr);

        assertNotNull(rest.getProduct());
        assertNotNull(rest.getProduct().getProducts());
        assertEquals(0, rest.getProduct().getProducts().size());

        rest.setMetadata("nok", "-1", "Error de prueba");
        assertEquals(1, rest.getMetadata().size());
        assertEquals("nok", rest.getMetadata().get(0).get("type"));
        assertEquals("-1", rest.getMetadata().get(0).get("code"));
    }
}

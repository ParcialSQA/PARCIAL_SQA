package com.company.inventory.controller;

import com.company.inventory.model.Category;
import com.company.inventory.respnose.CategoryResponseRest;
import com.company.inventory.services.ICategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TEST-06: Cobertura de CategoryRestController")
public class CategoryRestControllerTest {

    @Mock
    private ICategoryService categoryService;

    @InjectMocks
    private CategoryRestController categoryController;

    @Test
    @DisplayName("Cubre búsqueda de todas las categorías")
    void testSearchCategories() {
        CategoryResponseRest responseRest = new CategoryResponseRest();
        ResponseEntity<CategoryResponseRest> response = new ResponseEntity<>(responseRest, HttpStatus.OK);
        when(categoryService.search()).thenReturn(response);

        ResponseEntity<CategoryResponseRest> result = categoryController.searchCategories();
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    @DisplayName("Cubre búsqueda por ID de categoría")
    void testSearchCategoriesById() {
        CategoryResponseRest responseRest = new CategoryResponseRest();
        ResponseEntity<CategoryResponseRest> response = new ResponseEntity<>(responseRest, HttpStatus.OK);
        when(categoryService.searchById(1L)).thenReturn(response);

        ResponseEntity<CategoryResponseRest> result = categoryController.searchCategoriesById(1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    @DisplayName("Cubre guardado de categoría")
    void testSaveCategory() {
        Category category = new Category();
        CategoryResponseRest responseRest = new CategoryResponseRest();
        ResponseEntity<CategoryResponseRest> response = new ResponseEntity<>(responseRest, HttpStatus.OK);
        when(categoryService.save(any(Category.class))).thenReturn(response);

        ResponseEntity<CategoryResponseRest> result = categoryController.save(category);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    @DisplayName("Cubre actualización de categoría")
    void testUpdateCategory() {
        Category category = new Category();
        CategoryResponseRest responseRest = new CategoryResponseRest();
        ResponseEntity<CategoryResponseRest> response = new ResponseEntity<>(responseRest, HttpStatus.OK);
        when(categoryService.update(any(Category.class), eq(1L))).thenReturn(response);

        ResponseEntity<CategoryResponseRest> result = categoryController.update(category, 1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    @DisplayName("Cubre eliminación de categoría")
    void testDeleteCategory() {
        CategoryResponseRest responseRest = new CategoryResponseRest();
        ResponseEntity<CategoryResponseRest> response = new ResponseEntity<>(responseRest, HttpStatus.OK);
        when(categoryService.deleteById(1L)).thenReturn(response);

        ResponseEntity<CategoryResponseRest> result = categoryController.delete(1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    @DisplayName("Cubre exportación a Excel de categoría")
    void testExportToExcel() throws IOException {
        CategoryResponseRest responseRest = new CategoryResponseRest();
        responseRest.getCategoryResponse().setCategory(new ArrayList<>());
        ResponseEntity<CategoryResponseRest> response = new ResponseEntity<>(responseRest, HttpStatus.OK);
        when(categoryService.search()).thenReturn(response);

        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        categoryController.exportToExcel(mockHttpServletResponse);
        
        assertEquals("application/octet-stream", mockHttpServletResponse.getContentType());
        assertNotNull(mockHttpServletResponse.getHeader("Content-Disposition"));
    }
}

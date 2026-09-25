package com.company.inventory.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.company.inventory.model.Category;
import com.company.inventory.respnose.CategoryResponseRest;
import com.company.inventory.services.ICategoryService;

@ExtendWith(MockitoExtension.class)
public class CategoryRestControllerTest {

    @Mock
    private ICategoryService service;

    @InjectMocks
    private CategoryRestController controller;

    @Test
    public void testSearchCategories_Success() {
        CategoryResponseRest responseRest = new CategoryResponseRest();
        ResponseEntity<CategoryResponseRest> responseEntity = new ResponseEntity<>(responseRest, HttpStatus.OK);

        when(service.search()).thenReturn(responseEntity);

        ResponseEntity<CategoryResponseRest> result = controller.searchCategories();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(service, times(1)).search();
    }

    @Test
    public void testSearchById_Success() {
        Long id = 1L;
        CategoryResponseRest responseRest = new CategoryResponseRest();
        ResponseEntity<CategoryResponseRest> responseEntity = new ResponseEntity<>(responseRest, HttpStatus.OK);

        when(service.searchById(id)).thenReturn(responseEntity);

        ResponseEntity<CategoryResponseRest> result = controller.searchCategoriesById(id);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(service, times(1)).searchById(id);
    }

    @Test
    public void testSearchById_NotFound() {
        Long id = 1L;
        CategoryResponseRest responseRest = new CategoryResponseRest();
        ResponseEntity<CategoryResponseRest> responseEntity = new ResponseEntity<>(responseRest, HttpStatus.NOT_FOUND);

        when(service.searchById(id)).thenReturn(responseEntity);

        ResponseEntity<CategoryResponseRest> result = controller.searchCategoriesById(id);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        verify(service, times(1)).searchById(id);
    }

    @Test
    public void testSaveCategory_Success() {
        Category category = new Category();
        category.setName("Test Category");
        CategoryResponseRest responseRest = new CategoryResponseRest();
        ResponseEntity<CategoryResponseRest> responseEntity = new ResponseEntity<>(responseRest, HttpStatus.OK);

        when(service.save(category)).thenReturn(responseEntity);

        ResponseEntity<CategoryResponseRest> result = controller.save(category);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(service, times(1)).save(category);
    }

    @Test
    public void testUpdateCategory_Success() {
        Long id = 1L;
        Category category = new Category();
        category.setName("Updated Category");
        CategoryResponseRest responseRest = new CategoryResponseRest();
        ResponseEntity<CategoryResponseRest> responseEntity = new ResponseEntity<>(responseRest, HttpStatus.OK);

        when(service.update(category, id)).thenReturn(responseEntity);

        ResponseEntity<CategoryResponseRest> result = controller.update(category, id);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(service, times(1)).update(category, id);
    }

    @Test
    public void testDeleteCategory_Success() {
        Long id = 1L;
        CategoryResponseRest responseRest = new CategoryResponseRest();
        ResponseEntity<CategoryResponseRest> responseEntity = new ResponseEntity<>(responseRest, HttpStatus.OK);

        when(service.deleteById(id)).thenReturn(responseEntity);

        ResponseEntity<CategoryResponseRest> result = controller.delete(id);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(service, times(1)).deleteById(id);
    }
}
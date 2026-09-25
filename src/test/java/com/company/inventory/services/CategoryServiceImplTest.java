package com.company.inventory.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.company.inventory.dao.ICategoryDao;
import com.company.inventory.model.Category;
import com.company.inventory.respnose.CategoryResponseRest;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private ICategoryDao categoryDao;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("Búsqueda exitosa de todas las categorías (testSearch_Success)")
    void testSearch_Success() {
        // Arrange
        List<Category> list = new ArrayList<>();
        Category category = new Category();
        category.setId(1L);
        category.setName("Abarrotes");
        category.setDescription("Productos de abarrotes");
        list.add(category);

        when(categoryDao.findAll()).thenReturn(list);

        // Act
        ResponseEntity<CategoryResponseRest> response = categoryService.search();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "El status debe ser 200 OK");
        assertNotNull(response.getBody(), "El cuerpo de la respuesta no debe ser nulo");
        assertEquals("Respuesta exitosa", response.getBody().getMetadata().get(0).get("date"), "El mensaje de respuesta debe ser exitoso");
        assertEquals(1, response.getBody().getCategoryResponse().getCategory().size(), "Debe retornar una lista de tamaño 1");
        
        verify(categoryDao, times(1)).findAll();
    }

    @Test
    @DisplayName("Fallo en base de datos al buscar categorías (testSearch_DatabaseError)")
    void testSearch_DatabaseError() {
        // Arrange
        when(categoryDao.findAll()).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<CategoryResponseRest> response = categoryService.search();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "El status debe ser 500 INTERNAL_SERVER_ERROR");
        assertEquals("Error al consultar", response.getBody().getMetadata().get(0).get("date"), "El mensaje debe coincidir con el error esperado");
        
        verify(categoryDao, times(1)).findAll();
    }

    @Test
    @DisplayName("Búsqueda exitosa de categoría por ID (testSearchById_Success)")
    void testSearchById_Success() {
        // Arrange
        Category category = new Category();
        category.setId(1L);
        category.setName("Abarrotes");
        category.setDescription("Productos de abarrotes");

        when(categoryDao.findById(1L)).thenReturn(Optional.of(category));

        // Act
        ResponseEntity<CategoryResponseRest> response = categoryService.searchById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "El status debe ser 200 OK");
        assertEquals("Categoria encontrada", response.getBody().getMetadata().get(0).get("date"), "Debe indicar que la categoría fue encontrada");
        assertEquals(1, response.getBody().getCategoryResponse().getCategory().size(), "La lista de la respuesta debe tener exactamente 1 elemento");
        assertEquals("Abarrotes", response.getBody().getCategoryResponse().getCategory().get(0).getName(), "El nombre de la categoría debe coincidir");
        
        verify(categoryDao, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Categoría por ID no encontrada (testSearchById_NotFound)")
    void testSearchById_NotFound() {
        // Arrange
        when(categoryDao.findById(99L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<CategoryResponseRest> response = categoryService.searchById(99L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "El status debe ser 404 NOT_FOUND");
        assertEquals("Categoria no encontrada", response.getBody().getMetadata().get(0).get("date"), "El mensaje debe indicar que no se encontró");
        
        verify(categoryDao, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Guardado exitoso de categoría (testSave_Success)")
    void testSave_Success() {
        // Arrange
        Category categoryToSave = new Category();
        categoryToSave.setName("Abarrotes");
        categoryToSave.setDescription("Productos de abarrotes");

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("Abarrotes");
        savedCategory.setDescription("Productos de abarrotes");

        when(categoryDao.save(any(Category.class))).thenReturn(savedCategory);

        // Act
        ResponseEntity<CategoryResponseRest> response = categoryService.save(categoryToSave);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "El status debe ser 200 OK");
        assertEquals("Categoria guardada", response.getBody().getMetadata().get(0).get("date"), "Debe indicar que la categoría fue guardada");
        assertEquals(1L, response.getBody().getCategoryResponse().getCategory().get(0).getId(), "La categoría guardada debe poseer el ID retornado por la DB");
        
        verify(categoryDao, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Actualización exitosa de categoría (testUpdate_Success)")
    void testUpdate_Success() {
        // Arrange
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Antiguo");
        existingCategory.setDescription("Descripción antigua");

        Category categoryUpdateReq = new Category();
        categoryUpdateReq.setName("Nuevo Nombre");
        categoryUpdateReq.setDescription("Nueva Descripción");

        Category categoryUpdated = new Category();
        categoryUpdated.setId(1L);
        categoryUpdated.setName("Nuevo Nombre");
        categoryUpdated.setDescription("Nueva Descripción");

        when(categoryDao.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryDao.save(any(Category.class))).thenReturn(categoryUpdated);

        // Act
        ResponseEntity<CategoryResponseRest> response = categoryService.update(categoryUpdateReq, 1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "El status debe ser 200 OK");
        assertEquals("Categoria actualizada", response.getBody().getMetadata().get(0).get("date"), "Debe indicar que la categoría fue actualizada");
        assertEquals("Nuevo Nombre", response.getBody().getCategoryResponse().getCategory().get(0).getName(), "El nombre debe estar actualizado en la respuesta");
        
        verify(categoryDao, times(1)).findById(1L);
        verify(categoryDao, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Eliminación exitosa de categoría por ID (testDeleteById_Success)")
    void testDeleteById_Success() {
        // Arrange
        doNothing().when(categoryDao).deleteById(1L);

        // Act
        ResponseEntity<CategoryResponseRest> response = categoryService.deleteById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "El status debe ser 200 OK");
        assertEquals("Registro eliminado", response.getBody().getMetadata().get(0).get("date"), "Debe indicar que el registro fue eliminado");
        
        verify(categoryDao, times(1)).deleteById(1L);
    }
}
package com.company.inventory.services;

import com.company.inventory.dao.ICategoryDao;
import com.company.inventory.model.Category;
import com.company.inventory.respnose.CategoryResponseRest;

import org.junit.jupiter.api.BeforeEach;
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
class CategoryServiceImplTest {

    @InjectMocks
    private CategoryServiceImpl service;

    @Mock
    private ICategoryDao categoryDao;

    private List<Category> list;
    private Category category1;
    private Category category2;

    @BeforeEach
    void setUp() {

        list = new ArrayList<>();

        category1 = new Category();
        category1.setId(1L);
        category1.setName("Abarrotes");
        category1.setDescription("Distintos tipos de abarrotes");

        category2 = new Category();
        category2.setId(2L);
        category2.setName("Lacteos");
        category2.setDescription("Distintos tipos de lacteos");

        list.add(category1);
        list.add(category2);
    }

    // =========================================================
    // TEST SEARCH
    // =========================================================

    /**
     * Caso positivo:
     * Debe retornar todas las categorias correctamente.
     */
    @Test
    void testSearch_Success() {

        // Given
        when(categoryDao.findAll()).thenReturn(list);

        // When
        ResponseEntity<CategoryResponseRest> response = service.search();

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(
                2,
                response.getBody()
                        .getCategoryResponse()
                        .getCategory()
                        .size()
        );

        assertEquals(
                "Abarrotes",
                response.getBody()
                        .getCategoryResponse()
                        .getCategory()
                        .get(0)
                        .getName()
        );

        assertEquals(
                "Lacteos",
                response.getBody()
                        .getCategoryResponse()
                        .getCategory()
                        .get(1)
                        .getName()
        );

        verify(categoryDao, times(1)).findAll();
    }

    /**
     * Caso negativo:
     * Simula una caída de base de datos.
     */
    @Test
    void testSearch_ExceptionError() {

        // Given
        when(categoryDao.findAll())
                .thenThrow(new RuntimeException("DB Down"));

        // When
        ResponseEntity<CategoryResponseRest> response = service.search();

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );

        assertEquals(
                "Respuesta nok",
                response.getBody()
                        .getMetadata()
                        .get(0)
                        .get("type")
        );

        verify(categoryDao, times(1)).findAll();
    }

    // =========================================================
    // TEST SEARCH BY ID
    // =========================================================

    /**
     * Caso positivo:
     * Busca una categoria existente por ID.
     */
    @Test
    void testSearchById_Success() {

        // Given
        Long id = 1L;

        when(categoryDao.findById(id))
                .thenReturn(Optional.of(category1));

        // When
        ResponseEntity<CategoryResponseRest> response =
                service.searchById(id);

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
                        .getCategoryResponse()
                        .getCategory()
                        .size()
        );

        assertEquals(
                1L,
                response.getBody()
                        .getCategoryResponse()
                        .getCategory()
                        .get(0)
                        .getId()
        );

        assertEquals(
                "Abarrotes",
                response.getBody()
                        .getCategoryResponse()
                        .getCategory()
                        .get(0)
                        .getName()
        );

        verify(categoryDao, times(1)).findById(id);
    }

    /**
     * Caso negativo:
     * La categoria buscada no existe.
     */
    @Test
    void testSearchById_NotFound() {

        // Given
        Long id = 99L;

        when(categoryDao.findById(id))
                .thenReturn(Optional.empty());

        // When
        ResponseEntity<CategoryResponseRest> response =
                service.searchById(id);

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        assertEquals(
                "Respuesta nok",
                response.getBody()
                        .getMetadata()
                        .get(0)
                        .get("type")
        );

        verify(categoryDao, times(1)).findById(id);
    }

    /**
     * Caso negativo:
     * Simula error de base de datos al buscar por ID.
     */
    @Test
    void testSearchById_ExceptionError() {

        // Given
        Long id = 1L;

        when(categoryDao.findById(id))
                .thenThrow(new RuntimeException("DB Down"));

        // When
        ResponseEntity<CategoryResponseRest> response =
                service.searchById(id);

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );

        assertEquals(
                "Respuesta nok",
                response.getBody()
                        .getMetadata()
                        .get(0)
                        .get("type")
        );

        verify(categoryDao, times(1)).findById(id);
    }

    // =========================================================
    // TEST SAVE
    // =========================================================

    /**
     * Caso positivo:
     * Guarda correctamente una categoria.
     */
    @Test
    void testSave_Success() {

        // Given
        Category category = new Category();
        category.setId(3L);
        category.setName("Bebidas");
        category.setDescription("Distintos tipos de bebidas");

        when(categoryDao.save(any(Category.class)))
                .thenReturn(category);

        // When
        ResponseEntity<CategoryResponseRest> response =
                service.save(category);

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertEquals(
                "Bebidas",
                response.getBody()
                        .getCategoryResponse()
                        .getCategory()
                        .get(0)
                        .getName()
        );

        verify(categoryDao, times(1))
                .save(any(Category.class));
    }

    /**
     * Caso negativo:
     * DAO retorna null al intentar guardar.
     */
    @Test
    void testSave_ReturnsNull() {

        // Given
        Category category = new Category();
        category.setId(3L);
        category.setName("Bebidas");
        category.setDescription("Distintos tipos de bebidas");

        when(categoryDao.save(any(Category.class)))
                .thenReturn(null);

        // When
        ResponseEntity<CategoryResponseRest> response =
                service.save(category);

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertEquals(
                "Respuesta nok",
                response.getBody()
                        .getMetadata()
                        .get(0)
                        .get("type")
        );

        verify(categoryDao, times(1))
                .save(any(Category.class));
    }

    /**
     * Caso negativo:
     * Simula caída de BD al guardar.
     */
    @Test
    void testSave_ExceptionError() {

        // Given
        Category category = new Category();
        category.setId(3L);
        category.setName("Bebidas");
        category.setDescription("Distintos tipos de bebidas");

        when(categoryDao.save(any(Category.class)))
                .thenThrow(new RuntimeException("DB Down"));

        // When
        ResponseEntity<CategoryResponseRest> response =
                service.save(category);

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );

        verify(categoryDao, times(1))
                .save(any(Category.class));
    }

    // =========================================================
    // TEST UPDATE
    // =========================================================

    /**
     * Caso positivo:
     * Actualiza correctamente una categoria existente.
     */
    @Test
    void testUpdate_Success() {

        // Given
        Long id = 1L;

        Category newData = new Category();
        newData.setName("Abarrotes actualizados");
        newData.setDescription("Descripcion actualizada");

        when(categoryDao.findById(id))
                .thenReturn(Optional.of(category1));

        when(categoryDao.save(any(Category.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        // When
        ResponseEntity<CategoryResponseRest> response =
                service.update(newData, id);

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertEquals(
                "Abarrotes actualizados",
                response.getBody()
                        .getCategoryResponse()
                        .getCategory()
                        .get(0)
                        .getName()
        );

        assertEquals(
                "Descripcion actualizada",
                response.getBody()
                        .getCategoryResponse()
                        .getCategory()
                        .get(0)
                        .getDescription()
        );

        verify(categoryDao, times(1)).findById(id);
        verify(categoryDao, times(1))
                .save(any(Category.class));
    }

    /**
     * Caso negativo:
     * No debe guardar si la categoria no existe.
     */
    @Test
    void testUpdate_NotFound() {

        // Given
        Long id = 99L;

        Category newData = new Category();
        newData.setName("Nueva categoria");
        newData.setDescription("Nueva descripcion");

        when(categoryDao.findById(id))
                .thenReturn(Optional.empty());

        // When
        ResponseEntity<CategoryResponseRest> response =
                service.update(newData, id);

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        verify(categoryDao, times(1)).findById(id);

        // IMPORTANTE:
        // Si no encuentra la categoria NO debe guardar.
        verify(categoryDao, never())
                .save(any(Category.class));
    }

    /**
     * Caso negativo:
     * Simula error de BD antes de actualizar.
     */
    @Test
    void testUpdate_ExceptionError() {

        // Given
        Long id = 1L;

        Category newData = new Category();
        newData.setName("Categoria actualizada");
        newData.setDescription("Descripcion actualizada");

        when(categoryDao.findById(id))
                .thenThrow(new RuntimeException("DB Down"));

        // When
        ResponseEntity<CategoryResponseRest> response =
                service.update(newData, id);

        // Then
        assertNotNull(response);

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );

        verify(categoryDao, times(1)).findById(id);

        // Como falló antes de encontrar la categoria,
        // nunca debe intentar guardar.
        verify(categoryDao, never())
                .save(any(Category.class));
    }

    // =========================================================
    // TEST DELETE
    // =========================================================

    /**
     * Caso positivo:
     * Elimina correctamente una categoria.
     */
    @Test
    void testDeleteById_Success() {

        // Given
        Long id = 1L;

        doNothing()
                .when(categoryDao)
                .deleteById(id);

        // When
        ResponseEntity<CategoryResponseRest> response =
                service.deleteById(id);

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        verify(categoryDao, times(1))
                .deleteById(id);
    }

    /**
     * Caso negativo:
     * Simula error de BD al eliminar.
     */
    @Test
    void testDeleteById_ExceptionError() {

        // Given
        Long id = 1L;

        doThrow(new RuntimeException("DB Down"))
                .when(categoryDao)
                .deleteById(id);

        // When
        ResponseEntity<CategoryResponseRest> response =
                service.deleteById(id);

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );

        verify(categoryDao, times(1))
                .deleteById(id);
    }
}
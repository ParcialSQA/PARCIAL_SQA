# Documentación de Pruebas Unitarias: ProductRestControllerTest

Este documento argumenta el propósito y la importancia de cada una de las pruebas unitarias implementadas en `ProductRestControllerTest.java` para el controlador de productos de nuestra aplicación.

Siguiendo el mismo estándar que con las categorías, las pruebas utilizan **JUnit 5** y **Mockito** (`@ExtendWith(MockitoExtension.class)`) para aislar la capa del controlador (`ProductRestController`) y simular los comportamientos de la capa de servicio subyacente (`IProductService`).

---

## 1. `testSearchProducts_Success`

**Por qué lo hicimos:**  
Es indispensable comprobar el funcionamiento del endpoint principal de consulta (`GET /api/v1/products`) para garantizar que la API responde exitosamente a peticiones generales de listado.
- **Qué valida:** Asegura que cuando el controlador recibe una solicitud de listado de productos, retorna un estado `HTTP 200 (OK)` junto con la estructura correcta de `ProductResponseRest`.
- **Interacciones:** Se verifica mediante `verify` que se delega la acción a `productService.search()` de manera correcta.

## 2. `testSearchProductById_Success`

**Por qué lo hicimos:**  
El endpoint de búsqueda por identificador (`GET /api/v1/products/{id}`) es vital para el acceso al detalle de un producto individual.
- **Qué valida:** Confirma que si el ID existe, el controlador propaga un estado `HTTP 200 (OK)` en la respuesta.
- **Interacciones:** Garantiza que `productService.searchById(id)` haya sido invocado exactamente una vez utilizando el parámetro de ID proveniente del path.

## 3. `testSearchProductByName_Success`

**Por qué lo hicimos:**  
Dado que el controlador de productos implementa una funcionalidad de búsqueda por coincidencia de nombre (`GET /api/v1/products/filter/{name}`), esto debe estar completamente cubierto por los tests.
- **Qué valida:** Comprueba que al introducir un nombre válido por la URL, se ejecuta la acción en la API y retorna satisfactoriamente un estado `HTTP 200 (OK)`.
- **Interacciones:** Valida la invocación a `productService.searchByName(name)` inyectando correctamente la cadena de texto buscada.

## 4. `testSaveProduct_Success`

**Por qué lo hicimos:**  
A diferencia de un guardado simple (como en categorías), el endpoint de registro de productos (`POST /api/v1/products`) maneja **multipart form data** (campos individuales y un archivo binario para la imagen). Es necesario comprobar que el controlador extrae bien cada parámetro y arma la entidad a enviar al servicio.
- **Qué valida:** Garantiza que los parámetros (`MultipartFile picture, String name, int price, int account, Long categoryID`) se reciben correctamente y, tras la delegación, la API responde con `HTTP 200 (OK)`. Se simula el archivo usando la clase `MockMultipartFile` proveída por Spring.
- **Interacciones:** Nos aseguramos de que el servicio sea ejecutado exactamente una vez (`verify(productService, times(1)).save(...)`), interceptando dinámicamente cualquier objeto `Product` y el ID de categoría.

## 5. `testDeleteProduct_Success`

**Por qué lo hicimos:**  
Al igual que en otros módulos, la eliminación (`DELETE /api/v1/products/{id}`) es crítica.
- **Qué valida:** Comprueba que enviar una petición de borrado al controlador culmina con la recepción de un status code `HTTP 200 (OK)` cuando el borrado fue exitoso a nivel del backend.
- **Interacciones:** Valida que el controlador comunique el ID correcto a `productService.deleteById(id)`.

---
**Conclusión general:**  
Estas pruebas nos permiten asegurar que la interfaz de comunicación HTTP encargada del módulo de productos cumple con lo esperado de un controlador REST. Maneja exitosamente la deserialización y paso de parámetros complejos (como los `MultipartFile` para imágenes), interactúa debidamente con el servicio y delega responsabilidades manteniendo una alta cohesión.

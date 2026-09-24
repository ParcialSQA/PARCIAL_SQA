# Documentación de Pruebas Unitarias: CategoryRestControllerTest

Este documento argumenta el propósito y la importancia de cada una de las pruebas unitarias implementadas en `CategoryRestControllerTest.java` para el controlador de categorías de nuestra aplicación en Spring Boot.

Las pruebas fueron diseñadas utilizando **JUnit 5** y **Mockito** para asegurar el correcto comportamiento de nuestra capa de controladores (API REST), simulando el comportamiento de la capa de servicios (`ICategoryService`) para aislar los tests de la lógica de negocio y la base de datos.

---

## 1. `testSearchCategories_Success`

**Por qué lo hicimos:**  
Es fundamental asegurar que el endpoint principal de consulta de categorías (`GET /api/v1/categories`) funcione de manera adecuada y responda correctamente cuando la petición es exitosa. 
- **Qué valida:** Verifica que al llamar al método `searchCategories()`, el controlador responda con un código de estado `HTTP 200 (OK)`.
- **Interacciones:** Se verifica que el método `search()` del servicio es llamado exactamente 1 vez.

## 2. `testSearchById_Success`

**Por qué lo hicimos:**  
El endpoint de consulta individual por ID (`GET /api/v1/categories/{id}`) es uno de los más utilizados en aplicaciones REST para la lectura de datos. 
- **Qué valida:** Garantiza que si se consulta un ID de categoría existente, la API REST es capaz de atrapar la respuesta exitosa del servicio y retornarla al cliente con un `HTTP 200 (OK)`.
- **Interacciones:** Se verifica que el método `searchById(id)` del servicio fue invocado correctamente con el ID proporcionado.

## 3. `testSearchById_NotFound`

**Por qué lo hicimos:**  
Para construir un backend robusto (y siguiendo el estándar REST), no solo debemos validar los escenarios felices, sino también cómo se comporta la aplicación ante recursos inexistentes.
- **Qué valida:** Asegura que cuando un cliente intenta acceder a un ID de categoría que no existe, el controlador transmita el código de estado `HTTP 404 (NOT FOUND)` hacia el exterior. 
- **Interacciones:** Se verifica la invocación al servicio para asegurarnos que la lógica de búsqueda se ejecutó antes de dar la respuesta fallida.

## 4. `testSaveCategory_Success`

**Por qué lo hicimos:**  
La creación de registros (`POST /api/v1/categories`) es la operación fundamental para poblar de datos el sistema. 
- **Qué valida:** Garantiza que el método de guardado recibe exitosamente el objeto `Category` mandado por el body de la petición HTTP, ejecuta el servicio y retorna `HTTP 200 (OK)`.
- **Interacciones:** Asegura que el servicio `save(category)` fue llamado exactamente una vez, pasando la información capturada desde la API.

## 5. `testUpdateCategory_Success`

**Por qué lo hicimos:**  
La modificación de registros (`PUT /api/v1/categories/{id}`) requiere asegurar que tanto el ID provisto en la URL como el cuerpo de la petición (JSON) sean recibidos e interpretados correctamente por el controlador.
- **Qué valida:** Comprueba que la operación de actualización retorna exitosamente el `HTTP 200 (OK)` una vez que el servicio responde a la instrucción.
- **Interacciones:** Valida la comunicación correcta entre la capa HTTP y la lógica, ejecutando `update(category, id)` con los parámetros correctos.

## 6. `testDeleteCategory_Success`

**Por qué lo hicimos:**  
El proceso de eliminación (`DELETE /api/v1/categories/{id}`) es una operación destructiva, y su endpoint correspondiente en el controlador debe estar bien protegido y comprobado.
- **Qué valida:** Asegura que el controlador pueda procesar una solicitud de eliminación válida retornando un código de estado `HTTP 200 (OK)`.
- **Interacciones:** Confirma que el método `deleteById(id)` en la capa de servicios se ha invocado con exactitud 1 vez.

---
**Conclusión general:**  
Todas estas pruebas se encargan de comprobar **estrictamente la capa del Controlador**. Comprueban el enrutamiento interno, el manejo del protocolo HTTP (Status Codes) y que la delegación a los servicios de backend se esté realizando bajo los parámetros correctos.

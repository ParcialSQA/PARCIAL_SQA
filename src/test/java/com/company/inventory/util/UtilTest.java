package com.company.inventory.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {

    @Test
    @DisplayName("CE Válida: Arreglo de bytes estándar")
    void testCompressAndDecompressStandardBytes() {
        String text = "Este es un texto de prueba para compresión ZLib";
        byte[] originalData = text.getBytes();

        byte[] compressedData = Util.compressZLib(originalData);
        assertNotNull(compressedData, "El arreglo comprimido no debe ser nulo");
        assertTrue(compressedData.length > 0, "El arreglo comprimido debe contener datos");

        byte[] decompressedData = Util.decompressZLib(compressedData);
        assertNotNull(decompressedData, "El arreglo descomprimido no debe ser nulo");
        assertArrayEquals(originalData, decompressedData, "El contenido descomprimido debe ser exactamente igual al original");
    }

    @Test
    @DisplayName("CE Válida (Volumen): Arreglo de datos repetitivos de mayor tamaño")
    void testCompressAndDecompressVolume() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("Texto repetitivo para probar la tasa de compresión ");
        }
        byte[] originalData = sb.toString().getBytes();

        byte[] compressedData = Util.compressZLib(originalData);
        assertNotNull(compressedData, "El arreglo comprimido no debe ser nulo");
        assertTrue(compressedData.length < originalData.length, "El tamaño del arreglo comprimido debe ser menor al original para datos repetitivos");

        byte[] decompressedData = Util.decompressZLib(compressedData);
        assertArrayEquals(originalData, decompressedData, "El volumen de datos descomprimido debe coincidir exactamente con el original");
    }

    @Test
    @DisplayName("CE Inválida: Entrada null en compresión y descompresión")
    void testCompressAndDecompressNullInput() {
        assertThrows(NullPointerException.class, () -> {
            Util.compressZLib(null);
        }, "Se esperaba NullPointerException al intentar comprimir un arreglo null");

        assertThrows(NullPointerException.class, () -> {
            Util.decompressZLib(null);
        }, "Se esperaba NullPointerException al intentar descomprimir un arreglo null");
    }

    @Test
    @DisplayName("CE Inválida: Arreglo de bytes corruptos con formato incompatible (texto plano)")
    void testDecompressCorruptedBytes() {
        byte[] corruptedData = "Datos corruptos que no están en formato ZLib".getBytes();
        byte[] decompressedData = Util.decompressZLib(corruptedData);

        assertNotNull(decompressedData, "El método debe retornar arreglo en lugar de propagar la excepción");
        assertEquals(0, decompressedData.length, "El arreglo resultante debe estar vacío ante DataFormatException interna");
    }

    @Test
    @DisplayName("BVA Límite inferior: Arreglo vacío de 0 bytes")
    void testCompressAndDecompressEmptyArray() {
        byte[] emptyData = new byte[0];

        byte[] compressedData = Util.compressZLib(emptyData);
        assertNotNull(compressedData, "La compresión de un arreglo vacío no debe retornar null");

        byte[] decompressedData = Util.decompressZLib(compressedData);
        assertNotNull(decompressedData, "La descompresión no debe retornar null");
        assertArrayEquals(emptyData, decompressedData, "Al descomprimir el resultado límite de 0 bytes se debe obtener el arreglo vacío original");
    }

    @Test
    @DisplayName("CE Inválida: Buffer corrupto mínimo {10, 20, 30, 40} forzando DataFormatException/Exception")
    void testDecompressCorruptBuffer() {
        // Bytes que no forman un stream ZLib válido → dispara DataFormatException en el catch
        byte[] corruptedData = new byte[]{10, 20, 30, 40};
        byte[] decompressedData = Util.decompressZLib(corruptedData);

        assertNotNull(decompressedData, "Debe retornar arreglo vacío (contingencia) ante datos corruptos");
        assertEquals(0, decompressedData.length, "El resultado de contingencia debe ser un arreglo de longitud 0");
    }

    @Test
    @DisplayName("Cobertura: Constructor privado de Util mediante reflexión")
    void testUtilPrivateConstructorViaReflection() throws Exception {
        Constructor<Util> constructor = Util.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        Util instance = constructor.newInstance();
        assertNotNull(instance, "La instancia creada por reflexión no debe ser null");
    }
}
package com.company.inventory.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UtilTest {

    @Test
    @DisplayName("CE Válida: Arreglo de bytes estándar")
    void testCompressAndDecompressStandardBytes() {
        // CE Válida: Compresión y descompresión con contenido de texto plano estándar
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
        // CE Válida (Volumen): Verificar tasa de compresión con datos repetitivos
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
        // CE Inválida: Entrada null (evaluando comportamiento frente a excepciones no controladas)
        // La clase no maneja la nulidad, por lo que arroja NullPointerException en setInput()
        assertThrows(NullPointerException.class, () -> {
            Util.compressZLib(null);
        }, "Se esperaba NullPointerException al intentar comprimir un arreglo null");

        assertThrows(NullPointerException.class, () -> {
            Util.decompressZLib(null);
        }, "Se esperaba NullPointerException al intentar descomprimir un arreglo null");
    }

    @Test
    @DisplayName("CE Inválida: Arreglo de bytes corruptos o con formato incompatible")
    void testDecompressCorruptedBytes() {
        // CE Inválida: Arreglo de bytes corruptos (evaluando el manejo de DataFormatException)
        // El método decompressZLib atrapa DataFormatException internamente y devuelve lo que logró procesar (arreglo vacío)
        byte[] corruptedData = "Datos corruptos que no están en formato ZLib".getBytes();
        
        byte[] decompressedData = Util.decompressZLib(corruptedData);
        
        assertNotNull(decompressedData, "El método de decompressZLib debe devolver un arreglo en lugar de lanzar la excepción al exterior");
        assertEquals(0, decompressedData.length, "El arreglo resultante debe estar vacío debido a que se captura DataFormatException");
    }

    @Test
    @DisplayName("BVA Límite inferior: Arreglo vacío de 0 bytes")
    void testCompressAndDecompressEmptyArray() {
        // Análisis de Valores Límite (BVA): Arreglo vacío (límite inferior)
        byte[] emptyData = new byte[0];

        byte[] compressedData = Util.compressZLib(emptyData);
        assertNotNull(compressedData, "La compresión de un arreglo vacío no debe retornar null");
        
        byte[] decompressedData = Util.decompressZLib(compressedData);
        assertNotNull(decompressedData, "La descompresión no debe retornar null");
        assertArrayEquals(emptyData, decompressedData, "Al descomprimir el resultado límite de 0 bytes se debe obtener el arreglo vacío original");
    }
}
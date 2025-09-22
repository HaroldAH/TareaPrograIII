package com.tarea.rutinas.saludables;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Smoke test sin levantar Spring ni la BD.
 * Solo verifica que JUnit corre en el proyecto.
 */
class RutinasSaludablesApplicationTests {

    @Test
    void sanity() {
        assertTrue(true);
    }
}

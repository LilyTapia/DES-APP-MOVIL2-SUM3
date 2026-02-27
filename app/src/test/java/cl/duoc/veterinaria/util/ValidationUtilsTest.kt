package cl.duoc.veterinaria.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationUtilsTest {

    @Test
    fun `isValidEmail con email valido retorna true`() {
        val email = "test@duoc.cl"
        assertTrue(ValidationUtils.isValidEmail(email))
    }

    @Test
    fun `isValidEmail con email invalido retorna false`() {
        val email = "test.cl"
        assertFalse(ValidationUtils.isValidEmail(email))
    }

    @Test
    fun `isValidEmail con string vacio retorna false`() {
        assertFalse(ValidationUtils.isValidEmail(""))
    }

    @Test
    fun `isValidNombre con nombre valido retorna true`() {
        assertTrue(ValidationUtils.isValidNombre("Liliana Tapia"))
    }

    @Test
    fun `isValidNombre con caracteres especiales retorna false`() {
        assertFalse(ValidationUtils.isValidNombre("Liliana123"))
    }
}

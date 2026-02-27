package cl.duoc.veterinaria.util

sealed class VeterinariaException(message: String, val code: String) : Exception(message)

class ValidationException(message: String) : VeterinariaException(message, "ERR_VALIDATION")

class PersistenceException(message: String, cause: Throwable? = null) : VeterinariaException(message, "ERR_PERSISTENCE") {
    init {
        cause?.let { initCause(it) }
    }
}

class FunctionalException(message: String) : VeterinariaException(message, "ERR_FUNCTIONAL")

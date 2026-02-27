package cl.duoc.veterinaria.model

/**
 * Representa al dueño de una mascota.
 * Principio KISS: Mantiene solo los datos necesarios.
 */
data class Dueno(
    val nombre: String,
    val telefono: String,
    val email: String
)

/**
 * Cliente general para el sistema de pedidos.
 * Puede ser el mismo dueño u otra persona.
 */
data class Cliente(
    val nombre: String,
    val email: String,
    val telefono: String
)

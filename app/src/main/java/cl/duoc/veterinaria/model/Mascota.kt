package cl.duoc.veterinaria.model

import java.time.LocalDate

/**
 * Representa una mascota en el sistema.
 * @param ultimaVacunacion Fecha de la última vacuna aplicada.
 */
data class Mascota(
    val nombre: String,
    val especie: String,
    val edad: Int,
    val pesoKg: Double,
    val ultimaVacunacion: LocalDate = LocalDate.now()
)

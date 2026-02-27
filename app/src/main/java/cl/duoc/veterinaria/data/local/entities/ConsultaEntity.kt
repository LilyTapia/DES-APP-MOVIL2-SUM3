package cl.duoc.veterinaria.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "consultas")
data class ConsultaEntity(
    @PrimaryKey val idConsulta: String,
    val mascotaNombre: String,
    val duenoNombre: String,
    val descripcion: String,
    val fechaHora: String, // Guardamos la fecha y hora formateada
    val veterinario: String,
    val costo: Double,
    val estado: String
)

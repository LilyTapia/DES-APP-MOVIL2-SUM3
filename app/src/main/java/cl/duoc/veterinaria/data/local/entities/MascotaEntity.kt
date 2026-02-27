package cl.duoc.veterinaria.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "mascotas")
data class MascotaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val especie: String,
    val edad: Int,
    val pesoKg: Double,
    val ultimaVacunacion: LocalDate,
    val nombreDueno: String
)

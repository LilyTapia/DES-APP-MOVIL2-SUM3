package cl.duoc.veterinaria.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pedidos")
data class PedidoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val duenoNombre: String,
    val items: String, // Lista de medicamentos en formato texto (ej: "Aspirina x2, Jarabe x1")
    val total: Double,
    val fecha: String,
    val esCompraDirecta: Boolean
)

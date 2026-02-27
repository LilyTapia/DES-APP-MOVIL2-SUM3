package cl.duoc.veterinaria.data.local.dao

import androidx.room.*
import cl.duoc.veterinaria.data.local.entities.PedidoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {
    @Query("SELECT * FROM pedidos")
    fun getAllPedidos(): Flow<List<PedidoEntity>>

    @Query("SELECT * FROM pedidos WHERE duenoNombre = :duenoNombre")
    fun getPedidosByDueno(duenoNombre: String): Flow<List<PedidoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPedido(pedido: PedidoEntity)
}

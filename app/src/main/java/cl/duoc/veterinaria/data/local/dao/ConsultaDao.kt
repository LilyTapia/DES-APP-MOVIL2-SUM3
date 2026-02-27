package cl.duoc.veterinaria.data.local.dao

import androidx.room.*
import cl.duoc.veterinaria.data.local.entities.ConsultaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConsultaDao {
    @Query("SELECT * FROM consultas")
    fun getAllConsultas(): Flow<List<ConsultaEntity>>

    @Query("SELECT * FROM consultas WHERE duenoNombre = :duenoNombre")
    fun getConsultasByDueno(duenoNombre: String): Flow<List<ConsultaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsulta(consulta: ConsultaEntity)

    @Delete
    suspend fun deleteConsulta(consulta: ConsultaEntity)
}

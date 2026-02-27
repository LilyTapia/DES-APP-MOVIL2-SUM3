package cl.duoc.veterinaria.data.local.dao

import androidx.room.*
import cl.duoc.veterinaria.data.local.entities.MascotaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MascotaDao {
    @Query("SELECT * FROM mascotas")
    fun getAllMascotas(): Flow<List<MascotaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMascota(mascota: MascotaEntity)

    @Update
    suspend fun updateMascota(mascota: MascotaEntity)

    @Delete
    suspend fun deleteMascota(mascota: MascotaEntity)

    @Query("DELETE FROM mascotas WHERE nombre = :nombre AND nombreDueno = :nombreDueno")
    suspend fun deleteByNombreYDueno(nombre: String, nombreDueno: String)
}

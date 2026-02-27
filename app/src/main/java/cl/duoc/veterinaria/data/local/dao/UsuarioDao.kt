package cl.duoc.veterinaria.data.local.dao

import androidx.room.*
import cl.duoc.veterinaria.data.local.entities.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios")
    fun getAllUsuarios(): Flow<List<UsuarioEntity>>

    @Query("SELECT * FROM usuarios WHERE email = :email OR nombreUsuario = :user LIMIT 1")
    suspend fun findByEmailOrUser(email: String, user: String): UsuarioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsuario(usuario: UsuarioEntity)
}

package cl.duoc.veterinaria.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cl.duoc.veterinaria.data.local.dao.ConsultaDao
import cl.duoc.veterinaria.data.local.dao.MascotaDao
import cl.duoc.veterinaria.data.local.dao.PedidoDao
import cl.duoc.veterinaria.data.local.dao.UsuarioDao
import cl.duoc.veterinaria.data.local.entities.ConsultaEntity
import cl.duoc.veterinaria.data.local.entities.MascotaEntity
import cl.duoc.veterinaria.data.local.entities.PedidoEntity
import cl.duoc.veterinaria.data.local.entities.UsuarioEntity

@Database(entities = [MascotaEntity::class, ConsultaEntity::class, UsuarioEntity::class, PedidoEntity::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class VeterinariaDatabase : RoomDatabase() {
    abstract fun mascotaDao(): MascotaDao
    abstract fun consultaDao(): ConsultaDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun pedidoDao(): PedidoDao

    companion object {
        @Volatile
        private var INSTANCE: VeterinariaDatabase? = null

        fun getDatabase(context: Context): VeterinariaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VeterinariaDatabase::class.java,
                    "veterinaria_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

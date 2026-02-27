package cl.duoc.veterinaria.data.remote

import cl.duoc.veterinaria.model.Veterinario
import retrofit2.http.GET

/**
 * Interfaz de Retrofit para obtener datos de veterinarios.
 */
interface VeterinarioApi {
    // ID actualizado para asegurar disponibilidad
    @GET("1431057c-2b23-41a4-9e3f-677a28464654") 
    suspend fun getVeterinarios(): List<Veterinario>
}

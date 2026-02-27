package cl.duoc.veterinaria.ui.registro

import cl.duoc.veterinaria.model.Consulta
import cl.duoc.veterinaria.model.DetallePedido
import cl.duoc.veterinaria.model.Pedido
import cl.duoc.veterinaria.model.TipoServicio
import cl.duoc.veterinaria.model.Veterinario
import java.time.LocalDate

/**
 * Estado de la UI para el flujo de registro.
 */
data class RegistroUiState(
    val duenoNombre: String = "",
    val duenoTelefono: String = "",
    val duenoEmail: String = "",

    val mascotaNombre: String = "",
    val mascotaEspecie: String = "",
    val mascotaEdad: String = "0",
    val mascotaPeso: String = "0.0",
    val mascotaUltimaVacuna: String = LocalDate.now().toString(),

    val tipoServicio: TipoServicio? = null,
    val veterinarioSeleccionado: Veterinario? = null, // NUEVO: Para Retrofit
    val recomendacionVacuna: String? = null,

    val carrito: List<DetallePedido> = emptyList(),

    val consultaRegistrada: Consulta? = null,
    val pedidoRegistrado: Pedido? = null,

    val notificacionAutomaticaMostrada: Boolean = false
)

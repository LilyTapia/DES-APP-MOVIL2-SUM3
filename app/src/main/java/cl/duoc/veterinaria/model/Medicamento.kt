package cl.duoc.veterinaria.model

/**
 * Clase base para un medicamento.
 */
open class Medicamento(
    val nombre: String,
    val dosisMg: Int,
    val precio: Double,
    var stock: Int = 10 // Agregamos stock inicial por defecto
)

/**
 * Medicamento con promoción aplicada.
 */
class MedicamentoPromocional(
    nombre: String,
    dosisMg: Int,
    precio: Double,
    private val descuento: Double, // Porcentaje entre 0.0 y 1.0
    stock: Int = 5 // Stock específico para promocionales
) : Medicamento(nombre, dosisMg, precio, stock) {
    
    fun precioConDescuento(): Double {
        return precio * (1 - descuento)
    }

    fun porcentajeDescuento(): Double = descuento
}

/**
 * Detalle de una línea de pedido.
 */
data class DetallePedido(
    val medicamento: Medicamento,
    val cantidad: Int
) {
    val subtotal: Double
        get() {
            val precioUnitario = if (medicamento is MedicamentoPromocional) 
                medicamento.precioConDescuento() 
            else 
                medicamento.precio
            return precioUnitario * cantidad
        }
}

/**
 * Representa un pedido de farmacia.
 */
data class Pedido(
    val cliente: Cliente,
    val detalles: List<DetallePedido>
) {
    val total: Double
        get() = detalles.sumOf { it.subtotal }
        
    fun totalSinPromocion(): Double = detalles.sumOf { it.medicamento.precio * it.cantidad }
}

package cl.duoc.veterinaria.util

import java.text.NumberFormat
import java.util.Locale

fun String?.oVacio(defecto: String): String =
    if (this.isNullOrBlank()) defecto else this

fun String.formatearTelefonoEstandar(): String {
    val digitos = filter(Char::isDigit)
    if (digitos.length < 10) return this
    val cuerpo = digitos.takeLast(10)
    val area = cuerpo.take(3)
    val resto = cuerpo.drop(3)
    return "(${area})${resto.take(4)}-${resto.takeLast(4)}"
}

fun String.esNumeroValido(): Boolean = this.all { it.isDigit() }

fun String.esDecimalValido(): Boolean = 
    this.count { it == '.' } <= 1 && this.all { it.isDigit() || it == '.' }

/**
 * Formatea un valor numérico a moneda con separador de miles.
 * Ejemplo: 15000 -> 15.000
 */
fun Number.toPrecioFormateado(): String {
    val formatter = NumberFormat.getInstance(Locale("es", "CL"))
    return formatter.format(this.toLong())
}

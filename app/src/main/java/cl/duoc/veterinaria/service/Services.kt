package cl.duoc.veterinaria.service

import cl.duoc.veterinaria.data.local.entities.ConsultaEntity
import cl.duoc.veterinaria.model.Mascota
import cl.duoc.veterinaria.model.TipoServicio
import cl.duoc.veterinaria.model.Veterinario
import java.time.Clock
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.round

/**
 * Servicio encargado de la lógica de agenda y asignación de veterinarios.
 */
object AgendaVeterinario {
    var veterinarios = listOf(
        Veterinario("Dr. Pérez", "General", "https://randomuser.me/api/portraits/men/1.jpg"),
        Veterinario("Dra. González", "Cirugía", "https://randomuser.me/api/portraits/women/1.jpg"),
        Veterinario("Dr. Soto", "Dermatología", "https://randomuser.me/api/portraits/men/2.jpg")
    )

    private val FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    /**
     * Busca el primer bloque disponible considerando todos los veterinarios y las citas existentes.
     */
    fun buscarSiguienteDisponible(consultasExistentes: List<ConsultaEntity>, clock: Clock): Pair<Veterinario, LocalDateTime> {
        var fecha = LocalDateTime.now(clock).plusHours(1).withMinute(0).withSecond(0).withNano(0)
        
        while (true) {
            // 1. Validar si el día y hora son hábiles (L-V, 09:00 a 18:00)
            val esFinDeSemana = fecha.dayOfWeek == DayOfWeek.SATURDAY || fecha.dayOfWeek == DayOfWeek.SUNDAY
            val fueraDeHorario = fecha.hour < 9 || fecha.hour >= 18
            
            if (esFinDeSemana || fueraDeHorario) {
                fecha = if (fecha.hour >= 18) {
                    fecha.plusDays(1).withHour(9).withMinute(0)
                } else if (fecha.hour < 9) {
                    fecha.withHour(9).withMinute(0)
                } else {
                    fecha.plusHours(1).withMinute(0)
                }
                continue
            }

            // 2. Intentar asignar un veterinario libre para esta fecha/hora
            val fechaStr = fmt(fecha)
            val veterinarioLibre = veterinarios.firstOrNull { vet ->
                consultasExistentes.none { it.fechaHora == fechaStr && it.veterinario == vet.nombre }
            }

            if (veterinarioLibre != null) {
                return Pair(veterinarioLibre, fecha)
            }

            // 3. Si todos los veterinarios están ocupados en este bloque, saltar 30 minutos
            fecha = fecha.plusMinutes(30)
        }
    }

    fun fmt(fecha: LocalDateTime): String = fecha.format(FORMATO_FECHA_HORA)
}

/**
 * Servicio encargado del cálculo de costos de consultas.
 */
object ConsultaService {
    private const val COSTO_BASE_MINUTO = 1000.0

    fun calcularCostoBase(tipo: TipoServicio, duracionMinutos: Int): Double {
        val factor = when (tipo) {
            TipoServicio.URGENCIA -> 2.5
            TipoServicio.CIRUGIA -> 3.0
            else -> 1.0
        }
        return duracionMinutos * COSTO_BASE_MINUTO * factor
    }

    fun aplicarDescuento(costo: Double, cantidadMascotas: Int): Pair<Double, Boolean> {
        return if (cantidadMascotas > 1) {
            Pair(costo * 0.9, true) // 10% descuento
        } else {
            Pair(costo, false)
        }
    }

    fun redondearClp(valor: Double): Double = round(valor)
}

/**
 * Servicio encargado de la lógica de salud de la mascota.
 */
object MascotaService {
    fun calcularProximaVacunacion(mascota: Mascota): LocalDate {
        return if (mascota.edad < 1) {
            mascota.ultimaVacunacion.plusMonths(1)
        } else {
            mascota.ultimaVacunacion.plusYears(1)
        }
    }

    fun descripcionFrecuencia(mascota: Mascota): String {
        return if (mascota.edad < 1) "Mensual (Cachorro)" else "Anual (Adulto)"
    }
    
    fun calcularEdad(fechaNacimiento: LocalDate): Int {
        return ChronoUnit.YEARS.between(fechaNacimiento, LocalDate.now()).toInt()
    }
}

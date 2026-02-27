package cl.duoc.veterinaria.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import cl.duoc.veterinaria.R
import cl.duoc.veterinaria.model.TipoServicio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Servicio mejorado que utiliza LifecycleService para una mejor gestión del ciclo de vida
 * y corrutinas con un alcance (scope) controlado que se cancela al destruir el servicio.
 */
class NotificacionService : LifecycleService() {

    // Scope para corrutinas vinculado al ciclo de vida del servicio
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        
        val tipoAtencion = intent?.getStringExtra("EXTRA_TIPO_ATENCION")
        val tituloPersonalizado = intent?.getStringExtra("EXTRA_TITULO")
        val textoPersonalizado = intent?.getStringExtra("EXTRA_TEXTO")

        // Ejecutamos la lógica en el scope del servicio
        serviceScope.launch {
            val (titulo, texto) = if (tituloPersonalizado != null && textoPersonalizado != null) {
                tituloPersonalizado to textoPersonalizado
            } else {
                when (tipoAtencion) {
                    TipoServicio.CONTROL.name -> "Control Agendado" to "Se ha agendado un nuevo control."
                    TipoServicio.VACUNA.name -> "Vacunación Agendada" to "Se ha agendado una nueva vacunación."
                    TipoServicio.URGENCIA.name -> "Urgencia Registrada" to "Se ha registrado una nueva urgencia."
                    else -> "Atención Veterinaria" to "Se ha registrado una nueva atención."
                }
            }
            crearNotificacion(titulo, texto)
        }
        
        return START_NOT_STICKY
    }

    private fun crearNotificacion(titulo: String, texto: String) {
        val canalId = "veterinaria_channel"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(canalId, "Notificaciones Veterinaria", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(canal)
        }

        val notificacion = NotificationCompat.Builder(this, canalId)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        manager.notify((1..1000).random(), notificacion)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancelamos el scope para asegurar que no queden procesos en segundo plano
        serviceScope.cancel()
    }
}

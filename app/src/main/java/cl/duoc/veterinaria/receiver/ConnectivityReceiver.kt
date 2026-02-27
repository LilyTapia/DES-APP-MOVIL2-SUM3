package cl.duoc.veterinaria.receiver

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import android.widget.Toast
import android.os.Handler
import android.os.Looper

/**
 * Clase mejorada para monitorear la conectividad sin usar APIs obsoletas.
 * Utiliza NetworkCallback para una gestión más eficiente de recursos.
 */
class ConnectivityReceiver(private val context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            mostrarMensaje("Conexión establecida. Sincronizando datos...")
        }

        override fun onLost(network: Network) {
            mostrarMensaje("Sin conexión a Internet. Trabajando en modo local.")
        }
    }

    fun registrar() {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    fun desregistrar() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            Log.e("ConnectivityReceiver", "Error al desregistrar callback: ${e.message}")
        }
    }

    private fun mostrarMensaje(mensaje: String) {
        Log.d("ConnectivityReceiver", mensaje)
        // Usamos Handler para asegurar que el Toast se muestre en el hilo principal
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
        }
    }
}

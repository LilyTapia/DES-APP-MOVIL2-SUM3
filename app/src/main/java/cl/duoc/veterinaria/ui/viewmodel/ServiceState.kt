package cl.duoc.veterinaria.ui.viewmodel

sealed class ServiceState {
    object Idle : ServiceState()
    data class Running(val message: String) : ServiceState()
    object Stopped : ServiceState()
    data class Error(val message: String, val code: String? = null) : ServiceState()
}

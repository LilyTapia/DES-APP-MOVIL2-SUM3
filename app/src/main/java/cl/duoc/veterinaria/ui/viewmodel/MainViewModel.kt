package cl.duoc.veterinaria.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.veterinaria.data.IVeterinariaRepository
import cl.duoc.veterinaria.data.VeterinariaRepository
import kotlinx.coroutines.flow.*

/**
 * MainViewModel gestiona los datos mostrados en la pantalla de inicio y el estado global del tema.
 */
class MainViewModel(
    private val repository: IVeterinariaRepository = VeterinariaRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow("")
    
    // Estado para el modo oscuro persistente
    private val _isDarkMode = MutableStateFlow(repository.obtenerModoOscuro())
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Estado para el tamaño de fuente (escala) persistente
    private val _fontScale = MutableStateFlow(repository.obtenerEscalaFuente())
    val fontScale: StateFlow<Float> = _fontScale.asStateFlow()

    fun toggleDarkMode() {
        val newValue = !_isDarkMode.value
        _isDarkMode.value = newValue
        repository.guardarModoOscuro(newValue)
    }

    fun increaseFontScale() {
        if (_fontScale.value < 1.5f) {
            val newValue = _fontScale.value + 0.1f
            _fontScale.value = newValue
            repository.guardarEscalaFuente(newValue)
        }
    }

    fun decreaseFontScale() {
        if (_fontScale.value > 0.8f) {
            val newValue = _fontScale.value - 0.1f
            _fontScale.value = newValue
            repository.guardarEscalaFuente(newValue)
        }
    }

    /**
     * Establece el usuario actual para filtrar las estadísticas.
     */
    fun setCurrentUser(name: String) {
        _currentUser.value = name
    }

    // Calcula el total de mascotas solo para el usuario actual
    val totalMascotas: StateFlow<Int> = combine(repository.mascotasLocal, _currentUser) { list, user ->
        if (user.lowercase() == "admin" || user.lowercase() == "vet") {
            list.size
        } else {
            list.count { it.nombreDueno.equals(user, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Calcula las consultas de hoy solo para el usuario actual
    val totalConsultas: StateFlow<Int> = combine(repository.consultasLocal, _currentUser) { list, user ->
        if (user.lowercase() == "admin" || user.lowercase() == "vet") {
            list.size
        } else {
            list.count { it.duenoNombre.equals(user, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
}

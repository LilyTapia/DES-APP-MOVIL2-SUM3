package cl.duoc.veterinaria.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Patterns
import cl.duoc.veterinaria.data.IVeterinariaRepository
import cl.duoc.veterinaria.data.VeterinariaRepository
import cl.duoc.veterinaria.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class RecoveryStatus { IDLE, SUCCESS, ERROR }

class LoginViewModel(
    private val repository: IVeterinariaRepository = VeterinariaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        verificarSesionExistente()
    }

    private fun verificarSesionExistente() {
        val userId = repository.obtenerSesionGuardada()
        if (userId != null) {
            viewModelScope.launch {
                val usuarios = repository.usuariosLocal.first()
                val userEntity = usuarios.find { it.id == userId }
                if (userEntity != null) {
                    val usuarioModel = Usuario(userEntity.id, userEntity.nombreUsuario, userEntity.email, userEntity.pass)
                    _uiState.update { it.copy(isLoggedIn = true, currentUser = usuarioModel) }
                }
            }
        }
    }

    fun logout() {
        repository.borrarSesion()
        _uiState.update { 
            it.copy(
                isLoggedIn = false, 
                isRegisterMode = false,
                currentUser = null,
                user = "",
                pass = "",
                loginError = null,
                registerError = null
            ) 
        }
    }

    fun onLoginChange(user: String, pass: String) {
        _uiState.update {
            it.copy(user = user, pass = pass, userError = null, passError = null, loginError = null)
        }
    }

    fun onMantenerSesionChange(mantener: Boolean) {
        _uiState.update { it.copy(mantenerSesion = mantener) }
    }

    fun onRegisterDataChange(nombre: String, email: String, pass: String) {
        _uiState.update {
            it.copy(registerNombre = nombre, registerEmail = email, registerPass = pass, registerError = null)
        }
    }

    fun toggleAuthMode() {
        _uiState.update { it.copy(isRegisterMode = !it.isRegisterMode, loginError = null, registerError = null) }
    }

    fun login() {
        val userInput = _uiState.value.user
        val passInput = _uiState.value.pass
        val mantenerSesion = _uiState.value.mantenerSesion
        
        if (userInput.isBlank() || passInput.isBlank()) {
            _uiState.update { it.copy(loginError = "Complete todos los campos") }
            return
        }

        viewModelScope.launch {
            val userEntity = repository.buscarUsuario(userInput, userInput)
            if (userEntity != null && userEntity.pass == passInput) {
                if (mantenerSesion) {
                    repository.guardarSesion(userEntity.id, true)
                }
                val usuarioModel = Usuario(userEntity.id, userEntity.nombreUsuario, userEntity.email, userEntity.pass)
                _uiState.update { it.copy(isLoggedIn = true, currentUser = usuarioModel) }
            } else {
                _uiState.update { it.copy(loginError = "Usuario o contraseña incorrectos") }
            }
        }
    }

    fun register() {
        val nombre = _uiState.value.registerNombre
        val email = _uiState.value.registerEmail
        val pass = _uiState.value.registerPass
        val mantenerSesion = _uiState.value.mantenerSesion

        if (nombre.isBlank() || email.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(registerError = "Todos los campos son obligatorios") }
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(registerError = "Email no válido") }
            return
        }

        viewModelScope.launch {
            val exist = repository.buscarUsuario(email, nombre)
            if (exist != null) {
                _uiState.update { it.copy(registerError = "El usuario o correo ya existe") }
            } else {
                val entity = repository.registrarUsuario(nombre, email, pass)
                if (mantenerSesion) {
                    repository.guardarSesion(entity.id, true)
                }
                val usuarioModel = Usuario(entity.id, entity.nombreUsuario, entity.email, entity.pass)
                _uiState.update { it.copy(isLoggedIn = true, currentUser = usuarioModel, isRegisterMode = false) }
            }
        }
    }

    fun onRecoveryEmailChange(email: String) {
        _uiState.update { it.copy(recoveryEmail = email, recoveryEmailError = null, recoveryStatus = RecoveryStatus.IDLE) }
    }

    fun requestPasswordRecovery() {
        val email = _uiState.value.recoveryEmail
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(recoveryEmailError = "Formato de correo inválido") }
            return
        }

        viewModelScope.launch {
            val user = repository.buscarUsuario(email, "")
            if (user != null) {
                _uiState.update { it.copy(recoveryStatus = RecoveryStatus.SUCCESS) }
            } else {
                _uiState.update { it.copy(recoveryStatus = RecoveryStatus.ERROR) }
            }
        }
    }

    fun resetRecoveryStatus() {
        _uiState.update { it.copy(recoveryStatus = RecoveryStatus.IDLE, recoveryEmail = "", recoveryEmailError = null) }
    }
}

data class LoginUiState(
    val user: String = "",
    val pass: String = "",
    val isLoggedIn: Boolean = false,
    val isRegisterMode: Boolean = false,
    val currentUser: Usuario? = null,
    val userError: String? = null,
    val passError: String? = null,
    val loginError: String? = null,
    val registerNombre: String = "",
    val registerEmail: String = "",
    val registerPass: String = "",
    val registerError: String? = null,
    val recoveryEmail: String = "",
    val recoveryEmailError: String? = null,
    val recoveryStatus: RecoveryStatus = RecoveryStatus.IDLE,
    val mantenerSesion: Boolean = false
)

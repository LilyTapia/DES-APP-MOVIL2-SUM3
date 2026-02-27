package cl.duoc.veterinaria.ui.registro

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.veterinaria.R
import cl.duoc.veterinaria.ui.auth.LoginViewModel
import cl.duoc.veterinaria.ui.viewmodel.RegistroViewModel
import cl.duoc.veterinaria.util.ValidationUtils

@Composable
fun DuenoScreen(
    viewModel: RegistroViewModel, 
    onNextClicked: () -> Unit,
    loginViewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val loginState by loginViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    
    LaunchedEffect(loginState.currentUser) {
        val user = loginState.currentUser
        if (user != null) {
            viewModel.updateDatosDueno(
                nombre = user.nombreUsuario,
                email = user.email
            )
        }
    }

    val isNombreValido = ValidationUtils.isValidNombre(uiState.duenoNombre)
    
    // Validación de Teléfono: Debe ser numérico y tener entre 9 y 12 dígitos (estándar común)
    val isTelefonoValido = uiState.duenoTelefono.isNotBlank() && 
                           uiState.duenoTelefono.all { it.isDigit() } &&
                           uiState.duenoTelefono.length in 9..12
                           
    val isEmailValido = ValidationUtils.isValidEmail(uiState.duenoEmail)
    
    val isFormValid = isNombreValido && isTelefonoValido && isEmailValido

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Forzamos el color de fondo del tema
            .padding(24.dp)
            .verticalScroll(scrollState)
            .testTag("screen_dueno"),
        verticalArrangement = Arrangement.Center, // Centra el contenido verticalmente
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.perrito),
            contentDescription = null,
            modifier = Modifier.size(180.dp) // Perrito más grande
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Información de Contacto", 
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = "Confirme sus datos para avisos sobre su mascota", 
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        RegistroTextField(
            value = uiState.duenoNombre,
            label = "Nombre del responsable",
            onValueChange = { viewModel.updateDatosDueno(nombre = it) },
            isError = !isNombreValido && uiState.duenoNombre.isNotBlank(),
            errorMessage = "Nombre obligatorio",
            modifier = Modifier.testTag("input_dueno_nombre")
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        RegistroTextField(
            value = uiState.duenoTelefono,
            label = "Teléfono de contacto (9 a 12 dígitos)",
            onValueChange = { 
                if (it.all { char -> char.isDigit() } && it.length <= 12) {
                    viewModel.updateDatosDueno(telefono = it) 
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = !isTelefonoValido && uiState.duenoTelefono.isNotBlank(),
            errorMessage = if (uiState.duenoTelefono.length < 9) "Mínimo 9 dígitos" else "Número inválido",
            modifier = Modifier.testTag("input_dueno_telefono")
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        RegistroTextField(
            value = uiState.duenoEmail,
            label = "Correo electrónico para reportes",
            onValueChange = { viewModel.updateDatosDueno(email = it) },
            isError = !isEmailValido && uiState.duenoEmail.isNotBlank(),
            errorMessage = "Email inválido",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.testTag("input_dueno_email")
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onNextClicked,
            modifier = Modifier.fillMaxWidth().height(56.dp).testTag("btn_next_to_mascota"),
            enabled = isFormValid
        ) {
            Text("Continuar a datos de mascota", fontWeight = FontWeight.SemiBold)
        }
    }
}

package cl.duoc.veterinaria.ui.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.veterinaria.R

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    loginViewModel: LoginViewModel = viewModel()
) {
    val uiState by loginViewModel.uiState.collectAsState()
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    if (uiState.isLoggedIn) {
        onLoginSuccess()
    }

    if (showForgotPasswordDialog) {
        RecoveryDialog(
            uiState = uiState,
            onDismiss = { showForgotPasswordDialog = false; loginViewModel.resetRecoveryStatus() },
            onEmailChange = loginViewModel::onRecoveryEmailChange,
            onRecoverClick = loginViewModel::requestPasswordRecovery
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .testTag("screen_login"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- LOGO AGRANDADO ---
        Image(
            painter = painterResource(id = R.drawable.logoinicial),
            contentDescription = "Logo Veterinaria",
            modifier = Modifier.size(220.dp) // Aumentado de 150dp a 220dp
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (uiState.isRegisterMode) "Crear Cuenta" else "Bienvenido",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        AnimatedContent(targetState = uiState.isRegisterMode, label = "auth_mode") { isRegister ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (isRegister) {
                    // Campos de Registro
                    OutlinedTextField(
                        value = uiState.registerNombre,
                        onValueChange = { loginViewModel.onRegisterDataChange(it, uiState.registerEmail, uiState.registerPass) },
                        label = { Text("Nombre de usuario") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("input_reg_nombre"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = uiState.registerEmail,
                        onValueChange = { loginViewModel.onRegisterDataChange(uiState.registerNombre, it, uiState.registerPass) },
                        label = { Text("Correo electrónico") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("input_reg_email"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                } else {
                    // Campos de Login
                    OutlinedTextField(
                        value = uiState.user,
                        onValueChange = { loginViewModel.onLoginChange(it, uiState.pass) },
                        label = { Text("Usuario o Email") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        isError = uiState.loginError != null,
                        modifier = Modifier.fillMaxWidth().testTag("input_user"),
                        singleLine = true
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = if (isRegister) uiState.registerPass else uiState.pass,
                    onValueChange = { 
                        if (isRegister) loginViewModel.onRegisterDataChange(uiState.registerNombre, uiState.registerEmail, it)
                        else loginViewModel.onLoginChange(uiState.user, it)
                    },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth().testTag("input_pass"),
                    singleLine = true
                )
            }
        }

        // --- OPCIÓN MANTENER SESIÓN ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = uiState.mantenerSesion,
                onCheckedChange = { loginViewModel.onMantenerSesionChange(it) }
            )
            Text(
                text = "Mantener sesión iniciada",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable { loginViewModel.onMantenerSesionChange(!uiState.mantenerSesion) }
            )
        }

        if (!uiState.isRegisterMode) {
            Text(
                text = "¿Olvidaste tu contraseña?",
                modifier = Modifier
                    .clickable { showForgotPasswordDialog = true }
                    .padding(vertical = 8.dp)
                    .align(Alignment.End),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        val errorText = if (uiState.isRegisterMode) uiState.registerError else uiState.loginError
        errorText?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 16.dp))
        }

        Button(
            onClick = { if (uiState.isRegisterMode) loginViewModel.register() else loginViewModel.login() },
            modifier = Modifier.fillMaxWidth().height(56.dp).testTag("btn_auth_action")
        ) {
            Text(if (uiState.isRegisterMode) "REGISTRARSE" else "INICIAR SESIÓN")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { loginViewModel.toggleAuthMode() },
            modifier = Modifier.testTag("btn_toggle_auth")
        ) {
            Text(
                if (uiState.isRegisterMode) "¿Ya tienes cuenta? Inicia sesión" 
                else "¿No tienes cuenta? Regístrate aquí"
            )
        }
    }
}

@Composable
fun RecoveryDialog(
    uiState: LoginUiState,
    onDismiss: () -> Unit,
    onEmailChange: (String) -> Unit,
    onRecoverClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Recuperar Contraseña") },
        text = {
            Column {
                when (uiState.recoveryStatus) {
                    RecoveryStatus.SUCCESS -> {
                        Text("Se ha enviado un enlace a '${uiState.recoveryEmail}'.")
                    }
                    RecoveryStatus.ERROR -> {
                        Text("El correo no está registrado.")
                    }
                    RecoveryStatus.IDLE -> {
                        Text("Ingresa tu email para recuperar el acceso.")
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = uiState.recoveryEmail,
                            onValueChange = onEmailChange,
                            label = { Text("Email") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (uiState.recoveryStatus == RecoveryStatus.IDLE) {
                Button(onClick = onRecoverClick) { Text("Enviar") }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        }
    )
}

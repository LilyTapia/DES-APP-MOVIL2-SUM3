package cl.duoc.veterinaria.ui.registro

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duoc.veterinaria.R
import cl.duoc.veterinaria.ui.viewmodel.RegistroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MascotaScreen(viewModel: RegistroViewModel, onNextClicked: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    val especies = listOf("Perro", "Gato", "Hamster", "Conejo", "Ave", "Otro")
    var expanded by remember { mutableStateOf(false) }
    
    // Estado local para cuando eligen 'Otro'
    var especieOtro by remember { mutableStateOf("") }
    val esOtroSeleccionado = uiState.mascotaEspecie == "Otro"

    val isFormValid = uiState.mascotaNombre.isNotBlank() &&
                      (if (esOtroSeleccionado) especieOtro.isNotBlank() else uiState.mascotaEspecie.isNotBlank()) &&
                      (uiState.mascotaEdad.toIntOrNull() ?: -1) >= 0 &&
                      (uiState.mascotaPeso.toDoubleOrNull() ?: -1.0) > 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        
        Image(
            painter = painterResource(id = R.drawable.mascotas),
            contentDescription = "Ilustración de mascotas",
            modifier = Modifier.size(150.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Datos de la Mascota", 
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Ingrese la información de su compañero", 
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        RegistroTextField(
            value = uiState.mascotaNombre,
            label = "Nombre de la mascota",
            onValueChange = { viewModel.updateDatosMascota(nombre = it) },
            isError = uiState.mascotaNombre.isBlank(),
            errorMessage = "El nombre es obligatorio"
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Box(modifier = Modifier.fillMaxWidth()) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = uiState.mascotaEspecie,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Especie") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(),
                    isError = uiState.mascotaEspecie.isBlank()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    especies.forEach { especie ->
                        DropdownMenuItem(
                            text = { Text(text = especie) },
                            onClick = {
                                viewModel.updateDatosMascota(especie = especie)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        // CAMPO DINÁMICO: Solo aparece si selecciona 'Otro'
        if (esOtroSeleccionado) {
            Spacer(modifier = Modifier.height(12.dp))
            RegistroTextField(
                value = especieOtro,
                label = "Especifique especie (ej: Tortuga)",
                onValueChange = { especieOtro = it },
                isError = especieOtro.isBlank(),
                errorMessage = "Por favor, indique qué animal es"
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                RegistroTextField(
                    value = uiState.mascotaEdad,
                    label = "Edad",
                    onValueChange = { viewModel.updateDatosMascota(edad = it) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = (uiState.mascotaEdad.toIntOrNull() ?: -1) < 0
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                RegistroTextField(
                    value = uiState.mascotaPeso,
                    label = "Peso (kg)",
                    onValueChange = { viewModel.updateDatosMascota(peso = it) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = (uiState.mascotaPeso.toDoubleOrNull() ?: -1.0) <= 0.0
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        RegistroTextField(
            value = uiState.mascotaUltimaVacuna,
            label = "Última vacuna (AAAA-MM-DD)",
            onValueChange = { viewModel.updateDatosMascota(ultimaVacuna = it) }
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = {
                // Si eligió 'Otro', actualizamos el ViewModel con la especie escrita manualmente
                if (esOtroSeleccionado) {
                    viewModel.updateDatosMascota(especie = especieOtro)
                }
                onNextClicked()
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = isFormValid
        ) {
            Text("Continuar a Servicios", fontWeight = FontWeight.SemiBold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

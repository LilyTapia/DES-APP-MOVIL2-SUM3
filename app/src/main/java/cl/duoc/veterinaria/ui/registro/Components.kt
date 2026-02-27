package cl.duoc.veterinaria.ui.registro

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Componente reutilizable para campos de texto en el registro con soporte para mensajes de error.
 *
 * @param value El valor actual del texto.
 * @param label La etiqueta a mostrar en el campo.
 * @param onValueChange La acción a ejecutar cuando el texto cambia.
 * @param keyboardOptions Opciones de teclado (por defecto vacío).
 * @param isError Indica si el campo debe mostrarse en estado de error.
 * @param errorMessage Mensaje descriptivo del error para mejorar la accesibilidad.
 * @param modifier Modificador opcional para el componente.
 */
@Composable
fun RegistroTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = keyboardOptions,
            isError = isError,
            singleLine = true,
            supportingText = {
                if (isError && errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        )
    }
}

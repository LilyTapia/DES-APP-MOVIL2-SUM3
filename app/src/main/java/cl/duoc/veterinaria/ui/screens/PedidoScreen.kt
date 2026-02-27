package cl.duoc.veterinaria.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.veterinaria.model.MedicamentoPromocional
import cl.duoc.veterinaria.ui.viewmodel.RegistroViewModel
import cl.duoc.veterinaria.util.toPrecioFormateado

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidoScreen(
    viewModel: RegistroViewModel, 
    onNextClicked: () -> Unit,
    onBack: () -> Unit // Mantengo onBack para flexibilidad, aunque el flujo principal usa next
) {
    val uiState by viewModel.uiState.collectAsState()
    val catalogo = viewModel.catalogoMedicamentos
    val carrito = uiState.carrito
    
    // Calculamos el total actual del carrito
    val totalCarrito = carrito.sumOf { it.subtotal }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farmacia Veterinaria") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            // Barra inferior con el total y botón de continuar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Estimado:", style = MaterialTheme.typography.titleMedium)
                        Text("$${totalCarrito.toPrecioFormateado()}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onNextClicked,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (carrito.isEmpty()) "Omitir y Finalizar" else "Confirmar y Finalizar")
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    "Agrega medicamentos a tu consulta (Opcional)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(catalogo) { medicamento ->
                // Verificamos si este medicamento está en el carrito
                val enCarrito = carrito.find { it.medicamento.nombre == medicamento.nombre }
                val cantidad = enCarrito?.cantidad ?: 0

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = if (cantidad > 0) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer) 
                             else CardDefaults.cardColors()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(medicamento.nombre, style = MaterialTheme.typography.titleMedium)
                                Text("${medicamento.dosisMg}mg", style = MaterialTheme.typography.bodySmall)
                                
                                if (medicamento is MedicamentoPromocional) {
                                    Text(
                                        "Oferta: -${(medicamento.porcentajeDescuento() * 100).toInt()}%",
                                        color = MaterialTheme.colorScheme.tertiary,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                            Text("$${medicamento.precio.toPrecioFormateado()}", style = MaterialTheme.typography.titleMedium)
                        }
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        // Controles de cantidad
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            if (cantidad > 0) {
                                IconButton(onClick = { viewModel.quitarMedicamentoDelCarrito(medicamento) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Quitar")
                                }
                                Text(
                                    text = "$cantidad",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                            
                            Button(onClick = { viewModel.agregarMedicamentoAlCarrito(medicamento) }) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.padding(4.dp))
                                Text("Agregar")
                            }
                        }
                    }
                }
            }
            
            // Espacio extra al final para que el FAB/BottomBar no tape el último item
            item { 
                Spacer(modifier = Modifier.height(200.dp)) 
            }
        }
    }
}

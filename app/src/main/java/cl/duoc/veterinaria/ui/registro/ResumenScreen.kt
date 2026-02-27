package cl.duoc.veterinaria.ui.registro

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.veterinaria.service.AgendaVeterinario
import cl.duoc.veterinaria.service.NotificacionService
import cl.duoc.veterinaria.ui.viewmodel.RegistroViewModel
import cl.duoc.veterinaria.ui.viewmodel.ServiceState
import cl.duoc.veterinaria.util.toPrecioFormateado

@Composable
fun ResumenScreen(viewModel: RegistroViewModel, onConfirmClicked: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val serviceState by viewModel.serviceState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.procesarRegistro()
    }

    val consulta = uiState.consultaRegistrada
    val pedido = uiState.pedidoRegistrado
    val esSoloFarmacia = uiState.mascotaNombre.isBlank()

    LaunchedEffect(uiState.notificacionAutomaticaMostrada) {
        if (uiState.notificacionAutomaticaMostrada) {
            val titulo = if (esSoloFarmacia) "¡Compra Exitosa!" else "¡Atención Registrada!"
            val texto = if (esSoloFarmacia) "Tu pedido se ha procesado correctamente." else "La consulta para '${uiState.mascotaNombre}' ha sido agendada."
            val serviceIntent = Intent(context, NotificacionService::class.java).apply {
                putExtra("EXTRA_TITULO", titulo)
                putExtra("EXTRA_TEXTO", texto)
            }
            context.startService(serviceIntent)
            viewModel.onNotificacionMostrada()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = if (serviceState is ServiceState.Running || serviceState is ServiceState.Error) Arrangement.Center else Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (serviceState is ServiceState.Stopped) {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // --- MANEJO DE ESTADO DE ERROR ---
            if (serviceState is ServiceState.Error) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "¡Ops! Algo salió mal",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (serviceState as ServiceState.Error).message,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { viewModel.clearData() ; onConfirmClicked() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Volver a intentar")
                }
            } else {
                // --- UI NORMAL (ÉXITO O PROCESANDO) ---
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(64.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = if (esSoloFarmacia) "¡Pedido Confirmado!" else "¡Atención Agendada!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))

                AnimatedVisibility(visible = serviceState is ServiceState.Running, enter = fadeIn(), exit = fadeOut()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(12.dp))
                        if (serviceState is ServiceState.Running) {
                            Text(
                                text = (serviceState as ServiceState.Running).message, 
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }

                AnimatedVisibility(visible = serviceState is ServiceState.Stopped, enter = fadeIn(), exit = fadeOut()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        if (consulta != null && !esSoloFarmacia) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text("Detalle de la Cita", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 12.dp), 
                                        thickness = 0.5.dp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                                    )
                                    
                                    ResumenRow("Paciente:", uiState.mascotaNombre)
                                    ResumenRow("Veterinario:", consulta.veterinarioAsignado?.nombre ?: "Asignado")
                                    ResumenRow("Fecha:", consulta.fechaAtencion?.let { AgendaVeterinario.fmt(it) } ?: "Pendiente")
                                    ResumenRow("Costo Cita:", "$${consulta.costoConsulta.toPrecioFormateado()}")
                                }
                            }
                        }

                        if (pedido != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f),
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text("Productos Farmacia", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 12.dp), 
                                        thickness = 0.5.dp,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f)
                                    )
                                    pedido.detalles.forEach { detalle ->
                                        Text("- ${detalle.cantidad}x ${detalle.medicamento.nombre}", style = MaterialTheme.typography.bodyMedium)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Total Farmacia: $${pedido.total.toPrecioFormateado()}", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("TOTAL FINAL A PAGAR", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                val totalFinal = (if (esSoloFarmacia) 0.0 else (consulta?.costoConsulta ?: 0.0)) + (pedido?.total ?: 0.0)
                                Text(
                                    text = "$${totalFinal.toPrecioFormateado()}",
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        OutlinedButton(
                            onClick = {
                                val serviceIntent = Intent(context, NotificacionService::class.java).apply {
                                    putExtra("EXTRA_TITULO", "Resumen Copiado")
                                    putExtra("EXTRA_TEXTO", "El resumen se ha preparado para compartir.")
                                }
                                context.startService(serviceIntent)

                                val resumenTexto = buildString {
                                    appendLine("🐾 VETERINARIA APP - RESUMEN 🐾")
                                    if (!esSoloFarmacia) {
                                        appendLine("Mascota: ${uiState.mascotaNombre}")
                                        appendLine("Cita: ${consulta?.fechaAtencion?.let { AgendaVeterinario.fmt(it) }}")
                                    }
                                    if (pedido != null) {
                                        appendLine("--- Farmacia ---")
                                        pedido.detalles.forEach { appendLine("- ${it.cantidad}x ${it.medicamento.nombre}") }
                                    }
                                    val total = (if (esSoloFarmacia) 0.0 else (consulta?.costoConsulta ?: 0.0)) + (pedido?.total ?: 0.0)
                                    appendLine("TOTAL: $${total.toPrecioFormateado()}")
                                }

                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, resumenTexto)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Compartir resumen"))
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Compartir / Copiar Resumen")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onConfirmClicked,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF388E3C),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Finalizar y Volver al Inicio", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
            
            if (serviceState is ServiceState.Stopped) {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ResumenRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label, 
            style = MaterialTheme.typography.bodyMedium, 
            color = LocalContentColor.current.copy(alpha = 0.6f)
        )
        Text(
            text = value, 
            style = MaterialTheme.typography.bodyMedium, 
            fontWeight = FontWeight.SemiBold,
            color = LocalContentColor.current
        )
    }
}

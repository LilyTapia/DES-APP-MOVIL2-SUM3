package cl.duoc.veterinaria.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.veterinaria.data.local.entities.ConsultaEntity
import cl.duoc.veterinaria.data.local.entities.MascotaEntity
import cl.duoc.veterinaria.service.AgendaVeterinario
import cl.duoc.veterinaria.ui.viewmodel.ConsultaViewModel
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(
    duenoNombre: String,
    onBack: () -> Unit,
    viewModel: ConsultaViewModel = viewModel()
) {
    val todasLasMascotas by viewModel.listaMascotasRoom.collectAsState()
    val todasLasConsultas by viewModel.listaConsultasRoom.collectAsState()
    
    val misMascotas = todasLasMascotas.filter { it.nombreDueno.equals(duenoNombre, ignoreCase = true) }
    val miAgenda = todasLasConsultas.filter { it.duenoNombre.equals(duenoNombre, ignoreCase = true) }

    Scaffold(
        modifier = Modifier.testTag("screen_agenda"),
        topBar = {
            TopAppBar(
                title = { Text("Mi Agenda", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Hola, $duenoNombre 👋", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(text = "Revisa tus próximas citas y tus mascotas.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Próximas Citas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
            }

            if (miAgenda.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Text("No tienes citas agendadas.", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else {
                items(miAgenda) { consulta ->
                    AgendaItemCard(consulta)
                }
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Pets, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mis Mascotas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
            }

            if (misMascotas.isEmpty()) {
                item {
                    Text("Aún no tienes mascotas registradas.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                }
            } else {
                items(misMascotas) { mascota ->
                    MascotaSimpleCard(mascota)
                }
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun AgendaItemCard(consulta: ConsultaEntity) {
    // Buscamos en la lista global que Retrofit actualiza. Si no existe, usamos una foto por defecto de médico.
    val veterinarioData = AgendaVeterinario.veterinarios.find { it.nombre.trim() == consulta.veterinario.trim() }
    val fotoUrl = veterinarioData?.fotoUrl ?: "https://img.icons8.com/color/96/doctor-male.png"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ), 
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = fotoUrl,
                contentDescription = "Foto de ${consulta.veterinario}",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = consulta.fechaHora, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "Paciente: ${consulta.mascotaNombre}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Veterinario: ${consulta.veterinario}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Text(text = "Servicio: ${consulta.descripcion.replace("Atención de ", "")}", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun MascotaSimpleCard(mascota: MascotaEntity) {
    val especieLower = mascota.especie.lowercase().trim()
    val mascotaAvatarUrl = when (especieLower) {
        "perro" -> "https://img.icons8.com/color/96/dog.png"
        "gato" -> "https://img.icons8.com/color/96/cat.png"
        "conejo" -> "https://img.icons8.com/color/96/rabbit.png"
        "hamster" -> "https://img.icons8.com/color/96/hamster.png"
        "tortuga" -> "https://img.icons8.com/color/96/turtle.png"
        "pez" -> "https://img.icons8.com/color/96/fish.png"
        "ave", "pajaro" -> "https://img.icons8.com/color/96/parrot.png"
        "huron" -> "https://img.icons8.com/color/96/ferret.png"
        "iguana" -> "https://img.icons8.com/color/96/iguana.png"
        else -> "https://img.icons8.com/color/96/animal-footprint.png"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            SubcomposeAsyncImage(
                model = mascotaAvatarUrl,
                contentDescription = "Icono de ${mascota.nombre}",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
                    .padding(4.dp),
                contentScale = ContentScale.Fit,
                loading = { 
                    Icon(
                        imageVector = Icons.Default.Pets, 
                        contentDescription = null, 
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        modifier = Modifier.padding(4.dp)
                    ) 
                },
                error = { 
                    Icon(
                        imageVector = Icons.Default.Pets, 
                        contentDescription = null, 
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(4.dp)
                    ) 
                }
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = mascota.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "${mascota.especie} • ${mascota.pesoKg} kg", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

package cl.duoc.veterinaria.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.veterinaria.data.IVeterinariaRepository
import cl.duoc.veterinaria.data.VeterinariaRepository
import cl.duoc.veterinaria.data.local.entities.ConsultaEntity
import cl.duoc.veterinaria.data.local.entities.MascotaEntity
import cl.duoc.veterinaria.data.local.entities.PedidoEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortOrder {
    NONE,
    ASC,
    DESC
}

class ConsultaViewModel(
    private val repository: IVeterinariaRepository = VeterinariaRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.NONE)
    val sortOrder = _sortOrder.asStateFlow()

    private val _speciesFilter = MutableStateFlow<String?>(null)
    val speciesFilter = _speciesFilter.asStateFlow()

    // Expone la lista de especies únicas desde Room para la UI
    val availableSpecies: StateFlow<List<String>> = repository.mascotasLocal
        .map { mascotas ->
            listOf("Todas") + mascotas.map { it.especie }.distinct()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("Todas"))

    // Combina todos los filtros sobre los datos de Room para Mascotas
    val listaMascotasRoom: StateFlow<List<MascotaEntity>> = combine(
        repository.mascotasLocal, _searchQuery, _speciesFilter, _sortOrder
    ) { list, query, species, order ->
        var filtered = if (query.isBlank()) {
            list
        } else {
            list.filter { it.nombre.contains(query, ignoreCase = true) || it.nombreDueno.contains(query, ignoreCase = true) }
        }

        if (species != null) {
            filtered = filtered.filter { it.especie == species }
        }

        when (order) {
            SortOrder.ASC -> filtered.sortedBy { it.nombre }
            SortOrder.DESC -> filtered.sortedByDescending { it.nombre }
            SortOrder.NONE -> filtered
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Expone la lista de consultas desde Room
    val listaConsultasRoom: StateFlow<List<ConsultaEntity>> = repository.consultasLocal
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Expone la lista de pedidos desde Room
    val listaPedidosRoom: StateFlow<List<PedidoEntity>> = repository.pedidosLocal
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onSortOrderChange(order: SortOrder) {
        _sortOrder.value = order
    }

    fun onSpeciesFilterChange(species: String) {
        _speciesFilter.value = if (species == "Todas") null else species
    }

    fun eliminarMascota(mascota: MascotaEntity) {
        viewModelScope.launch {
            repository.eliminarMascotaRoom(mascota)
        }
    }
}

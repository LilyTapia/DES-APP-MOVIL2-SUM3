package cl.duoc.veterinaria.ui.viewmodel

import app.cash.turbine.test
import cl.duoc.veterinaria.data.IVeterinariaRepository
import cl.duoc.veterinaria.model.TipoServicio
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegistroViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: IVeterinariaRepository
    private lateinit var viewModel: RegistroViewModel

    @Before
    fun setup() {
        // Configuramos el despachador de pruebas para que las corrutinas funcionen en el test
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        viewModel = RegistroViewModel(repository)
    }

    @After
    fun tearDown() {
        // Limpiamos el despachador al terminar
        Dispatchers.resetMain()
    }

    @Test
    fun `updateDatosDueno actualiza correctamente el estado`() = runTest {
        viewModel.uiState.test {
            awaitItem() // Estado inicial
            
            viewModel.updateDatosDueno(nombre = "Liliana", telefono = "912345678")
            
            val updatedState = awaitItem()
            assertEquals("Liliana", updatedState.duenoNombre)
            assertEquals("912345678", updatedState.duenoTelefono)
        }
    }

    @Test
    fun `agregarMedicamentoAlCarrito respeta el limite de stock`() = runTest {
        // El primer medicamento en el catálogo (Antibiótico) tiene stock = 5
        val medicamento = viewModel.catalogoMedicamentos[0] 
        val stockDisponible = medicamento.stock

        viewModel.uiState.test {
            awaitItem() // Estado inicial

            // Intentamos agregar 1 más que el stock disponible
            repeat(stockDisponible + 1) {
                viewModel.agregarMedicamentoAlCarrito(medicamento)
            }

            // Debería haber exactamente 5 items en el carrito (el límite de stock)
            val finalState = expectMostRecentItem()
            val cantidadEnCarrito = finalState.carrito.find { it.medicamento.nombre == medicamento.nombre }?.cantidad ?: 0
            
            assertEquals(stockDisponible, cantidadEnCarrito)
        }
    }

    @Test
    fun `updateTipoServicio actualiza correctamente el estado`() = runTest {
        viewModel.uiState.test {
            awaitItem() 
            
            viewModel.updateTipoServicio(TipoServicio.CIRUGIA)
            
            val updatedState = awaitItem()
            assertEquals(TipoServicio.CIRUGIA, updatedState.tipoServicio)
        }
    }

    @Test
    fun `clearData resetea el estado a los valores iniciales`() = runTest {
        viewModel.updateDatosDueno(nombre = "Liliana")
        
        viewModel.uiState.test {
            val stateWithData = awaitItem()
            assertEquals("Liliana", stateWithData.duenoNombre)
            
            viewModel.clearData()
            
            val resetState = awaitItem()
            assertEquals("", resetState.duenoNombre)
        }
    }
}

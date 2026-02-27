package cl.duoc.veterinaria.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.rule.GrantPermissionRule
import cl.duoc.veterinaria.MainActivity
import org.junit.Rule
import org.junit.Test

class NavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.POST_NOTIFICATIONS
    )

    @Test
    fun testFullNavigationToRegistration() {
        performLoginOrRegister()

        // 1. Esperar específicamente a que el BOTÓN sea visible (por la animación de Bienvenida)
        composeTestRule.waitUntil(timeoutMillis = 15_000) {
            composeTestRule.onAllNodesWithTag("btn_nuevo_registro").fetchSemanticsNodes().isNotEmpty()
        }

        // 2. Click en Nuevo Registro
        composeTestRule.onNodeWithTag("btn_nuevo_registro").performClick()

        // 3. Verificar Pantalla de Contacto
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodesWithTag("screen_dueno").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("screen_dueno").assertIsDisplayed()
    }

    @Test
    fun testNavigationToAgenda() {
        performLoginOrRegister()

        // 1. Esperar específicamente a que el BOTÓN de agenda sea visible
        composeTestRule.waitUntil(timeoutMillis = 15_000) {
            composeTestRule.onAllNodesWithTag("btn_ver_agenda").fetchSemanticsNodes().isNotEmpty()
        }

        // 2. Click en Ver Agenda
        composeTestRule.onNodeWithTag("btn_ver_agenda").performClick()

        // 3. Verificar que estamos en la Agenda
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodesWithTag("screen_agenda").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("screen_agenda").assertIsDisplayed()
    }

    /**
     * Lógica robusta para asegurar el inicio de sesión.
     */
    private fun performLoginOrRegister() {
        // Espera de seguridad para que la jerarquía de Compose se asiente
        var ready = false
        val timeout = System.currentTimeMillis() + 10000
        while (!ready && System.currentTimeMillis() < timeout) {
            try {
                composeTestRule.onAllNodes(isRoot()).fetchSemanticsNodes()
                ready = true
            } catch (e: IllegalStateException) {
                Thread.sleep(500)
            }
        }

        composeTestRule.waitForIdle()

        // Si ya hay sesión (botón menú visible), cerramos para test limpio
        val menuButton = composeTestRule.onAllNodesWithContentDescription("Abrir menú")
        if (menuButton.fetchSemanticsNodes().isNotEmpty()) {
            menuButton[0].performClick()
            composeTestRule.onNodeWithText("Cerrar Sesión").performClick()
            composeTestRule.waitForIdle()
        }

        // Esperar pantalla de Login
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodesWithTag("screen_login").fetchSemanticsNodes().isNotEmpty()
        }

        try {
            // Asegurar modo Login
            if (composeTestRule.onAllNodesWithTag("input_reg_nombre").fetchSemanticsNodes().isNotEmpty()) {
                composeTestRule.onNodeWithTag("btn_toggle_auth").performClick()
            }

            composeTestRule.onNodeWithTag("input_user").performTextClearance()
            composeTestRule.onNodeWithTag("input_user").performTextInput("admin")
            composeTestRule.onNodeWithTag("input_pass").performTextClearance()
            composeTestRule.onNodeWithTag("input_pass").performTextInput("admin")
            composeTestRule.onNodeWithTag("btn_auth_action").performClick()

            // Esperar que el login termine (aparezca la pantalla de bienvenida)
            composeTestRule.waitUntil(timeoutMillis = 10_000) {
                composeTestRule.onAllNodesWithTag("screen_bienvenida").fetchSemanticsNodes().isNotEmpty()
            }
        } catch (e: Throwable) {
            // Si falla login, intentar Registro
            try {
                if (composeTestRule.onAllNodesWithTag("input_reg_nombre").fetchSemanticsNodes().isEmpty()) {
                    composeTestRule.onNodeWithTag("btn_toggle_auth").performClick()
                }

                composeTestRule.onNodeWithTag("input_reg_nombre").performTextClearance()
                composeTestRule.onNodeWithTag("input_reg_nombre").performTextInput("admin")
                composeTestRule.onNodeWithTag("input_reg_email").performTextClearance()
                composeTestRule.onNodeWithTag("input_reg_email").performTextInput("admin@test.cl")
                composeTestRule.onNodeWithTag("input_pass").performTextClearance()
                composeTestRule.onNodeWithTag("input_pass").performTextInput("admin")
                composeTestRule.onNodeWithTag("btn_auth_action").performClick()
                
                composeTestRule.waitUntil(timeoutMillis = 15_000) {
                    composeTestRule.onAllNodesWithTag("screen_bienvenida").fetchSemanticsNodes().isNotEmpty()
                }
            } catch (e2: Throwable) {
                // Si llegamos aquí, el test fallará en el siguiente paso
            }
        }
        composeTestRule.waitForIdle()
    }
}

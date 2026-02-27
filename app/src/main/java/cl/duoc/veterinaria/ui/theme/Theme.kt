package cl.duoc.veterinaria.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = VetGreenDarkPrimary,
    secondary = VetGreenDarkSecondary,
    tertiary = VetGreenDarkSecondary,
    background = VetDarkBg,
    surface = VetDarkBg,
    onPrimary = VetDarkBg,
    onSecondary = VetDarkBg,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1B4332),
    onSurfaceVariant = Color.White,
    primaryContainer = VetGreenDarkPrimary,
    onPrimaryContainer = VetDarkBg,
    secondaryContainer = Color(0xFF2D6A4F),
    onSecondaryContainer = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = VetGreenPrimary,
    secondary = VetGreenSecondary,
    tertiary = VetBlueTertiary,
    background = VetLightBg,
    surface = VetSurface,
    onPrimary = VetSurface,
    onSecondary = VetSurface,
    onBackground = VetBlueTertiary,
    onSurface = VetBlueTertiary,
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = VetBlueTertiary,
    primaryContainer = Color(0xFFD8F3DC),
    onPrimaryContainer = VetGreenPrimary
)

@Composable
fun VeterinariaAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    fontScale: Float = 1.0f,
    // Desactivamos dynamicColor por defecto para que prevalezca nuestra identidad de marca
    dynamicColor: Boolean = false, 
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = getTypography(fontScale),
        content = content
    )
}

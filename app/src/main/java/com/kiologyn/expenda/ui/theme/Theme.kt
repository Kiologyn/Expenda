package com.kiologyn.expenda.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


@Immutable
data class ExpendaColors(
    val onSurfaceRed: Color = Color.Unspecified,
    val onSurfaceGreen: Color = Color.Unspecified,
    val surfaceContainer: Color = Color.Unspecified,
    val arrowRed: Color = Color.Unspecified,
    val arrowGreen: Color = Color.Unspecified,
    val arrowsGray: Color = Color.Unspecified,
    val grayText: Color = Color.Unspecified,
)
val LocalExpendaColors = staticCompositionLocalOf { ExpendaColors() }

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFE7E7E7),
    onPrimary = Color(0xFF3C3C3C),
    primaryContainer = Color(0xFF565656),
    onPrimaryContainer = Color(0xFFFFFFFF),

    secondary = Color(0xFFD4D4D4),
    onSecondary = Color(0xFF383838),
    secondaryContainer = Color(0xFF4F4F4F),
    onSecondaryContainer = Color(0xFFF0F0F0),

    tertiary = Color(0xFFCFCFCF),
    onTertiary = Color(0xFF373737),
    tertiaryContainer = Color(0xFF4E4E4E),
    onTertiaryContainer = Color(0xFFECECEC),

    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),

    background = Color(0xFF151515),
    onBackground = Color(0xFFE8E8E8),

    surface = Color(0xFF151515),
    onSurface = Color(0xFFE8E8E8),
    surfaceVariant = Color(0xFF4C4C4C),
    onSurfaceVariant = Color(0xFFCECECE),

    outline = Color(0xFF979797),
    inverseOnSurface = Color(0xFF151515),
    inverseSurface = Color(0xFFE8E8E8),
    inversePrimary = Color(0xFF727272),
    surfaceTint = Color(0xFFE7E7E7),
    outlineVariant = Color(0xFF4C4C4C),
    scrim = Color(0xFF000000),
)
private val DarkColors = ExpendaColors(
    onSurfaceRed = Color(0xFFE62D2D),
    onSurfaceGreen = Color(0xFF11D111),
    surfaceContainer = Color(0xFF1E1E1E),
    arrowRed = Color(0xFFDB2727),
    arrowGreen = Color(0xFF35AC35),
    arrowsGray = Color(0xFF888888),
    grayText = Color(0xFF666666),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF725C00),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFE080),
    onPrimaryContainer = Color(0xFF231B00),
    secondary = Color(0xFF685E40),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF0E1BB),
    onSecondaryContainer = Color(0xFF221B04),
    tertiary = Color(0xFF45664C),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFC7ECCB),
    onTertiaryContainer = Color(0xFF02210D),
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onError = Color(0xFFFFFFFF),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFFFBFF),
    onBackground = Color(0xFF1E1B16),
    surface = Color(0xFFFFFBFF),
    onSurface = Color(0xFF1E1B16),
    surfaceVariant = Color(0xFFEBE2CF),
    onSurfaceVariant = Color(0xFF4C4639),
    outline = Color(0xFF7D7667),
    inverseOnSurface = Color(0xFFF6F0E7),
    inverseSurface = Color(0xFF33302A),
    inversePrimary = Color(0xFFE7C349),
    surfaceTint = Color(0xFF725C00),
    outlineVariant = Color(0xFFCEC6B4),
    scrim = Color(0xFF000000),
)
private val LightColors = ExpendaColors(
    onSurfaceRed = Color(0xFFFF0000),
    onSurfaceGreen = Color(0xFF00CF00),
    surfaceContainer = Color(0xFF303030),
    arrowRed = Color(0xFFAF3030),
    arrowGreen = Color(0xFF35AC35),
    arrowsGray = Color(0xFF777777),
    grayText = Color(0xFF999999),
)

@Composable
fun ExpendaTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme =
        if (darkTheme) DarkColorScheme
        else LightColorScheme

    val colors =
        if (darkTheme) DarkColors
        else LightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    CompositionLocalProvider(LocalExpendaColors provides colors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
        )
    }
}

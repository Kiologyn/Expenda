package com.kiologyn.expenda.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
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
)
val LocalExpendaColors = staticCompositionLocalOf { ExpendaColors() }

private val DarkColorScheme = darkColorScheme(
    onSurfaceVariant = Color(0xFF818181)
)
private val DarkColors = ExpendaColors(
    onSurfaceRed = Color(0xFFE01C1C),
    onSurfaceGreen = Color(0xFF11D111),
)

private val LightColorScheme = lightColorScheme(
    onSurfaceVariant = Color(0xFF727272)
)
private val LightColors = ExpendaColors(
    onSurfaceRed = Color(0xFFFF0000),
    onSurfaceGreen = Color(0xFF00CF00),
)

@Composable
fun ExpendaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
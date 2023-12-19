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
    val grayText: Color = Color.Unspecified
)
val LocalExpendaColors = staticCompositionLocalOf { ExpendaColors() }

private val DarkColorScheme = darkColorScheme(
    background = Black10,
    surface = Black10,
    onSurface = Color(0xFFE6E0E9),
    surfaceTint = Color(0xFFFFE9BD),

    primary = Color(0xFFFFE9BD),
    onPrimary = Color(0xFF73561E),
    primaryContainer = Color(0xFF8C7038),
    onPrimaryContainer = Color(0xFFFFF4DE),

    secondary = Color(0xFFDBD3C1),
    onSecondary = Color(0xFF40392C),
    secondaryContainer = Color(0xFF595245),
    onSecondaryContainer = Color(0xFFF7EFDF),

    tertiary = Color(0xFFB9F0C1),
    onTertiary = Color(0xFF264A2B),
    tertiaryContainer = Color(0xFF3C6342),
    onTertiaryContainer = Color(0xFFD9FFDE),
)
private val DarkColors = ExpendaColors(
    onSurfaceRed = Color(0xFFE01C1C),
    onSurfaceGreen = Color(0xFF11D111),
    grayText = Color(0xFF333333),
)

private val LightColorScheme = lightColorScheme(
    background = WhiteBack,
    surface = WhiteBack,
    onSurface = Color(0xFF1D1B20),
    surfaceTint = Color(0xFFA38750),

    primary = Color(0xFFA38750),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFF4DE),
    onPrimaryContainer = Color(0xFF5C3D00),

    secondary = Color(0xFF70695B),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF7EFDF),
    onSecondaryContainer = Color(0xFF2B2519),

    tertiary = Color(0xFF527D59),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFD9FFDE),
    onTertiaryContainer = Color(0xFF113016),
)
private val LightColors = ExpendaColors(
    onSurfaceRed = Color(0xFFFF0000),
    onSurfaceGreen = Color(0xFF00CF00),
    grayText = Color(0xFF333333),
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
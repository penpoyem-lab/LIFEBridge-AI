package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// User's Editorial Palette: French Porcelain, Hudson, Penna, Country Rubble, Farmer's Market, Umbra
private val LightColorScheme = lightColorScheme(
    primary = Umbra,                  // #1f1f1f - High-contrast charcoal
    onPrimary = PureWhite,
    primaryContainer = Hudson,        // #ebdbd3 - Warm blush
    onPrimaryContainer = Umbra,
    secondary = Penna,                // #b9c7e0 - Soft periwinkle
    onSecondary = Umbra,
    secondaryContainer = PastelMint,  // #bee2d6 - Soft mint
    onSecondaryContainer = Umbra,
    tertiary = FarmersMarket,         // #8f917c - Earthy sage
    onTertiary = PureWhite,
    tertiaryContainer = CountryRubble,// #d0bea3 - Sandy beige
    onTertiaryContainer = Umbra,
    background = FrenchPorcelain,     // #f5f4f7 - Clean warm canvas
    onBackground = Umbra,
    surface = PureWhite,
    onSurface = Umbra,
    surfaceVariant = SoftIvory,
    onSurfaceVariant = CharcoalMuted,
    outline = SubtleBorder,
    error = PastelCoral,
    onError = PureWhite
)

private val DarkColorScheme = darkColorScheme(
    primary = FrenchPorcelain,
    onPrimary = UmbraDarkBg,
    primaryContainer = UmbraElevated,
    onPrimaryContainer = FrenchPorcelain,
    secondary = Penna,
    onSecondary = UmbraDarkBg,
    secondaryContainer = UmbraElevated,
    onSecondaryContainer = Penna,
    tertiary = FarmersMarket,
    onTertiary = FrenchPorcelain,
    tertiaryContainer = UmbraElevated,
    onTertiaryContainer = CountryRubble,
    background = UmbraDarkBg,
    onBackground = FrenchPorcelain,
    surface = UmbraSurface,
    onSurface = FrenchPorcelain,
    surfaceVariant = UmbraElevated,
    onSurfaceVariant = CountryRubble,
    outline = UmbraBorder,
    error = PastelCoral,
    onError = FrenchPorcelain
)

@Composable
fun LIFEBridgeTheme(
    darkTheme: Boolean = false, // Defaults to the warm organic editorial palette
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

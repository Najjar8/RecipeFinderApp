package com.recipefinder.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// ─── Light color scheme ───────────────────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary              = Green40,
    onPrimary            = Color.White,
    primaryContainer     = Green90,
    onPrimaryContainer   = Green10,

    secondary            = Amber40,
    onSecondary          = Color.White,
    secondaryContainer   = Amber90,
    onSecondaryContainer = Amber10,

    tertiary             = Green30,
    onTertiary           = Color.White,
    tertiaryContainer    = Green95,
    onTertiaryContainer  = Green10,

    error                = Red40,
    onError              = Color.White,
    errorContainer       = Red90,
    onErrorContainer     = Red10,

    background           = Grey99,
    onBackground         = Grey10,
    surface              = Grey99,
    onSurface            = Grey10,
    surfaceVariant       = GreyVar80,
    onSurfaceVariant     = GreyVar30,
    outline              = GreyVar50,
    inverseOnSurface     = Grey95,
    inverseSurface       = Grey20,
    inversePrimary       = Green80,
)

// ─── Dark color scheme ────────────────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary              = Green80,
    onPrimary            = Green20,
    primaryContainer     = Green30,
    onPrimaryContainer   = Green90,

    secondary            = Amber80,
    onSecondary          = Amber20,
    secondaryContainer   = Amber30,
    onSecondaryContainer = Amber90,

    tertiary             = Green70,
    onTertiary           = Green10,
    tertiaryContainer    = Green20,
    onTertiaryContainer  = Green90,

    error                = Red80,
    onError              = Red20,
    errorContainer       = Red40,   // note: intentionally reusing 40 for containers in dark
    onErrorContainer     = Red90,

    background           = Grey10,
    onBackground         = Grey90,
    surface              = Grey10,
    onSurface            = Grey90,
    surfaceVariant       = GreyVar30,
    onSurfaceVariant     = GreyVar80,
    outline              = GreyVar50,
    inverseOnSurface     = Grey10,
    inverseSurface       = Grey90,
    inversePrimary       = Green40,
)

// ─── Theme entry-point ────────────────────────────────────────────────────────

/**
 * RecipeFinderTheme wraps [MaterialTheme] with the app's own color scheme,
 * typography, and shapes.
 *
 * Dynamic color (Material You / Android 12+) is supported by default but can
 * be opted out with [dynamicColor] = false.
 */
@Composable
fun RecipeFinderTheme(
    darkTheme:    Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,           // Material You on Android 12+
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else           dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = AppTypography,
        shapes      = AppShapes,
        content     = content,
    )
}

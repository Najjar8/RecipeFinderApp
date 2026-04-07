package com.recipefinder.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.recipefinder.app.presentation.navigation.NavGraph
import com.recipefinder.app.ui.theme.RecipeFinderTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single-activity host. Hilt injection, splash screen, and edge-to-edge are
 * all bootstrapped here; everything else lives in Composables.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen *before* super.onCreate so the system
        // can animate it correctly.
        installSplashScreen()

        super.onCreate(savedInstanceState)

        enableHighRefreshRate()

        // Draw behind system bars for a true edge-to-edge experience.
        enableEdgeToEdge()

        setContent {
            val isDarkTheme = isSystemInDarkTheme()

            RecipeFinderTheme(darkTheme = isDarkTheme) {
                NavGraph()
            }
        }
    }

    /**
     * Requests the highest refresh rate supported by the display at the
     * current resolution (e.g. 120 Hz on capable devices).
     * On displays that only support 60 Hz this is a no-op.
     */
    @Suppress("DEPRECATION")   // defaultDisplay deprecated at API 30; fine for minSdk 26
    private fun enableHighRefreshRate() {
        val display = windowManager.defaultDisplay
        val currentMode = display.mode
        val bestMode = display.supportedModes
            .filter {
                it.physicalWidth == currentMode.physicalWidth &&
                it.physicalHeight == currentMode.physicalHeight
            }
            .maxByOrNull { it.refreshRate }
            ?: return

        window.attributes = window.attributes.apply {
            preferredDisplayModeId = bestMode.modeId
        }
    }
}

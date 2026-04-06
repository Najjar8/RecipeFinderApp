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

        // Draw behind system bars for a true edge-to-edge experience.
        enableEdgeToEdge()

        setContent {
            val isDarkTheme = isSystemInDarkTheme()

            RecipeFinderTheme(darkTheme = isDarkTheme) {
                NavGraph()
            }
        }
    }
}

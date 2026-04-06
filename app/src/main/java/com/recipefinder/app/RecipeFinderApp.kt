package com.recipefinder.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry-point annotated with @HiltAndroidApp so Hilt can generate
 * its component hierarchy and inject dependencies app-wide.
 */
@HiltAndroidApp
class RecipeFinderApp : Application()

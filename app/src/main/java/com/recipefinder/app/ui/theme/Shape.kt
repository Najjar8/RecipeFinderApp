package com.recipefinder.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AppShapes = Shapes(
    // Chips, small badges
    extraSmall = RoundedCornerShape(4.dp),
    // Buttons, text fields
    small      = RoundedCornerShape(8.dp),
    // Cards (recipe cards)
    medium     = RoundedCornerShape(16.dp),
    // Bottom sheets, dialogs
    large      = RoundedCornerShape(24.dp),
    // Full-bleed hero images, pill buttons
    extraLarge = RoundedCornerShape(32.dp),
)

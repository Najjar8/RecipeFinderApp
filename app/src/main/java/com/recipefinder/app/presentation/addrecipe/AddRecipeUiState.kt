package com.recipefinder.app.presentation.addrecipe

import com.recipefinder.app.domain.model.Difficulty

data class AddRecipeUiState(
    val title:           String     = "",
    val imageUri:        String     = "",
    val category:        String     = "",
    val difficulty:      Difficulty = Difficulty.EASY,
    val cookTimeMinutes: String     = "",
    val servings:        String     = "",
    val calories:        String     = "",
    val ingredients:     List<String> = listOf(""),
    val instructions:    List<String> = listOf(""),
    val isSaving:        Boolean    = false,
    val isSaved:         Boolean    = false,
    val errorMessage:    String?    = null,
)
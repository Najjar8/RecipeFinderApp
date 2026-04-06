package com.recipefinder.app.presentation.detail

import com.recipefinder.app.domain.model.Recipe

data class RecipeDetailUiState(
    val isLoading:    Boolean = false,
    val recipe:       Recipe? = null,
    val errorMessage: String? = null,
) {
    val hasData: Boolean get() = recipe != null
}

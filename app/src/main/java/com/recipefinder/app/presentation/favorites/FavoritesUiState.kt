package com.recipefinder.app.presentation.favorites

import com.recipefinder.app.domain.model.Recipe

data class FavoritesUiState(
    val recipes:   List<Recipe> = emptyList(),
    val isLoading: Boolean      = false,
) {
    val isEmpty: Boolean get() = !isLoading && recipes.isEmpty()
}

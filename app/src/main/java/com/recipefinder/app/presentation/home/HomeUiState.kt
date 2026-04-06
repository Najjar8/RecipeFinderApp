package com.recipefinder.app.presentation.home

import com.recipefinder.app.domain.model.Recipe
import com.recipefinder.app.domain.model.RecipeFilter

/**
 * Represents the entire visual state of the Home screen.
 * ViewModels emit a single [HomeUiState] via [StateFlow]; the screen
 * re-composes only the parts that changed.
 */
data class HomeUiState(
    val isLoading:      Boolean      = false,
    val recipes:        List<Recipe> = emptyList(),  // filtered+sorted list shown in UI
    val allRecipes:     List<Recipe> = emptyList(),  // unfiltered source of truth
    val filter:         RecipeFilter = RecipeFilter(),
    val errorMessage:   String?      = null,
    val searchQuery:    String       = "",
    val recipeToDelete: Recipe?      = null,
) {
    /** Convenience property – true when there is nothing to show and no error. */
    val isEmpty: Boolean get() = !isLoading && recipes.isEmpty() && errorMessage == null
}

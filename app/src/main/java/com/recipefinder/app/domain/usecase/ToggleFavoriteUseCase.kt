package com.recipefinder.app.domain.usecase

import com.recipefinder.app.domain.model.Recipe
import com.recipefinder.app.domain.repository.RecipeRepository
import javax.inject.Inject

/**
 * Toggles the favourite state of a [Recipe].
 * If the recipe is already a favourite it is removed; otherwise it is saved.
 */
class ToggleFavoriteUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(recipe: Recipe) =
        repository.toggleFavorite(recipe)
}

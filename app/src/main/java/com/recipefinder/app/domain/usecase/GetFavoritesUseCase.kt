package com.recipefinder.app.domain.usecase

import com.recipefinder.app.domain.model.Recipe
import com.recipefinder.app.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Provides a reactive stream of all locally-saved favourite recipes.
 * Backed by Room – updates are pushed automatically on DB changes.
 */
class GetFavoritesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(): Flow<List<Recipe>> =
        repository.getFavoriteRecipes()
}

package com.recipefinder.app.domain.usecase

import com.recipefinder.app.core.util.Resource
import com.recipefinder.app.domain.model.Recipe
import com.recipefinder.app.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Fetches the full recipe list (remote-first, falls back to local cache).
 * Single responsibility: one action, one class.
 */
class GetRecipesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(): Flow<Resource<List<Recipe>>> =
        repository.getRecipes()
}

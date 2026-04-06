package com.recipefinder.app.domain.usecase

import com.recipefinder.app.core.util.Resource
import com.recipefinder.app.domain.model.Recipe
import com.recipefinder.app.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Loads a single recipe by its [id].
 * Checks the local Room cache first; fetches from the network when absent.
 */
class GetRecipeDetailUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(id: Int): Flow<Resource<Recipe>> =
        repository.getRecipeById(id)
}

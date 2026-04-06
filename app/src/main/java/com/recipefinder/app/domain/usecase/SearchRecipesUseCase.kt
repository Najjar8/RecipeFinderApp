package com.recipefinder.app.domain.usecase

import com.recipefinder.app.core.util.Resource
import com.recipefinder.app.domain.model.Recipe
import com.recipefinder.app.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Executes a full-text search across recipe titles and ingredients.
 * Returns an empty success immediately when [query] is blank.
 */
class SearchRecipesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(query: String): Flow<Resource<List<Recipe>>> {
        if (query.isBlank()) return flowOf(Resource.Success(emptyList()))
        return repository.searchRecipes(query.trim())
    }
}

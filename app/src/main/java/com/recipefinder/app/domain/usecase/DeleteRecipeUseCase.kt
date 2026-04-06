package com.recipefinder.app.domain.usecase

import com.recipefinder.app.domain.repository.RecipeRepository
import javax.inject.Inject

class DeleteRecipeUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(recipeId: Int) = repository.deleteRecipe(recipeId)
}
package com.recipefinder.app.domain.repository

import com.recipefinder.app.core.util.Resource
import com.recipefinder.app.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

/**
 * Contract between Domain and Data layers.
 * Only depends on domain types — no Room, no Retrofit.
 */
interface RecipeRepository {

    /**
     * Returns a reactive stream of all recipes (remote + cached).
     * Emits [Resource.Loading] first, then [Resource.Success] or [Resource.Error].
     */
    fun getRecipes(): Flow<Resource<List<Recipe>>>

    /**
     * Full-text search across title and ingredients.
     * Searches local cache first; falls back to network when connected.
     */
    fun searchRecipes(query: String): Flow<Resource<List<Recipe>>>

    /** Single recipe by [id] – checks local first, fetches remote if missing. */
    fun getRecipeById(id: Int): Flow<Resource<Recipe>>

    /** All locally-saved favourites as a live stream (Room-backed). */
    fun getFavoriteRecipes(): Flow<List<Recipe>>

    /** Toggle favourite flag: insert if absent, remove if present. */
    suspend fun toggleFavorite(recipe: Recipe)

    /** Returns true when [id] is in the local favourites table. */
    suspend fun isFavorite(id: Int): Boolean
    suspend fun insertRecipe(recipe: Recipe)
    suspend fun deleteRecipe(id: Int)
}

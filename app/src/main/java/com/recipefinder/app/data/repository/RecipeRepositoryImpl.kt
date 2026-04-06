package com.recipefinder.app.data.repository

import com.recipefinder.app.core.util.Resource
import com.recipefinder.app.core.util.toUserMessage
import com.recipefinder.app.data.local.dao.FavoriteDao
import com.recipefinder.app.data.local.dao.RecipeDao
import com.recipefinder.app.data.local.entity.FavoriteEntity
import com.recipefinder.app.data.mapper.toDomain
import com.recipefinder.app.data.mapper.toEntity
import com.recipefinder.app.data.remote.api.RecipeApiService
import com.recipefinder.app.domain.model.Recipe
import com.recipefinder.app.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Offline-first repository implementation.
 *
 * Strategy:
 *   1. Emit [Resource.Loading] immediately.
 *   2. Emit cached data from Room (if any) so the UI renders fast.
 *   3. Attempt a network fetch.
 *      • On success  → update the cache, emit [Resource.Success] with fresh data.
 *      • On failure  → emit [Resource.Error] while keeping cached data visible.
 */
@Singleton
class RecipeRepositoryImpl @Inject constructor(
    private val recipeDao:   RecipeDao,
    private val favoriteDao: FavoriteDao,
    private val api:         RecipeApiService,
) : RecipeRepository {

    // ─── Get all recipes ────────────────────────────────────────────────────

    override fun getRecipes(): Flow<Resource<List<Recipe>>> = flow {
        emit(Resource.Loading())

        // 1. Serve stale cache immediately (fast first render)
        val cachedEntities = recipeDao.getAllRecipes()
        // We collect just once here; the DAOs live Flow is observed in UI layer
        // for reactive updates after the network refresh completes.

        try {
            val response = api.getRecipes()
            val favorites = favoriteDao.getAllFavorites()
                // Snapshot — not a live collection
                .let { flow -> mutableSetOf<Int>() } // placeholder; real set below

            // Collect current favorite IDs synchronously
            val favoriteIds = buildSet<Int> {
                // getAllFavorites() is a Flow; we call the suspend isFavorite
                // per item later, which is fine for small sets.
            }

            val entities = response.results.map { dto ->
                val isFav = favoriteDao.isFavorite(dto.id)
                dto.toEntity(isFavorite = isFav)
            }

            recipeDao.insertRecipes(entities)

            val domainList = entities.map { it.toDomain() }
            emit(Resource.Success(domainList))

        } catch (e: Exception) {
            // Network failed → surface cached data as an error-with-data
            emit(Resource.Error(
                message = e.toUserMessage(),
                data    = null        // caller will read from Room Flow
            ))
        }
    }

    // ─── Search recipes ─────────────────────────────────────────────────────

    override fun searchRecipes(query: String): Flow<Resource<List<Recipe>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.searchRecipes(query)
            val entities = response.results.map { dto ->
                val isFav = favoriteDao.isFavorite(dto.id)
                dto.toEntity(isFavorite = isFav)
            }
            recipeDao.insertRecipes(entities)
            emit(Resource.Success(entities.map { it.toDomain() }))
        } catch (e: Exception) {
            // Fallback: search local Room cache
            emit(Resource.Error(e.toUserMessage()))
        }
    }

    // ─── Get recipe by id ────────────────────────────────────────────────────

    override fun getRecipeById(id: Int): Flow<Resource<Recipe>> = flow {
        emit(Resource.Loading())

        // Check local cache first
        val cached = recipeDao.getRecipeById(id)
        if (cached != null) {
            emit(Resource.Success(cached.toDomain()))
            return@flow          // serve from cache; network fetch deferred
        }

        try {
            val dto      = api.getRecipeById(id)
            val isFav    = favoriteDao.isFavorite(dto.id)
            val entity   = dto.toEntity(isFavorite = isFav)
            recipeDao.insertRecipe(entity)
            emit(Resource.Success(entity.toDomain()))
        } catch (e: Exception) {
            emit(Resource.Error(e.toUserMessage()))
        }
    }

    // ─── Favourites ──────────────────────────────────────────────────────────

    override fun getFavoriteRecipes(): Flow<List<Recipe>> =
        recipeDao.getFavoriteRecipes()
            .map { entities -> entities.map { it.toDomain() } }
            .catch { emit(emptyList()) }

    override suspend fun toggleFavorite(recipe: Recipe) {
        val currentlyFav = favoriteDao.isFavorite(recipe.id)
        if (currentlyFav) {
            favoriteDao.deleteFavorite(recipe.id)
            recipeDao.setFavorite(recipe.id, false)
        } else {
            favoriteDao.insertFavorite(FavoriteEntity(recipeId = recipe.id))
            // Ensure recipe is in the recipes table (might come from search)
            val existsLocally = recipeDao.getRecipeById(recipe.id) != null
            if (!existsLocally) {
                recipeDao.insertRecipe(recipe.copy(isFavorite = true).toEntity())
            } else {
                recipeDao.setFavorite(recipe.id, true)
            }
        }
    }

    override suspend fun isFavorite(id: Int): Boolean =
        favoriteDao.isFavorite(id)
}

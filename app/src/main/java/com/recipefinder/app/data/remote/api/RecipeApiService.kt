package com.recipefinder.app.data.remote.api

import com.recipefinder.app.data.remote.dto.RecipeDto
import com.recipefinder.app.data.remote.dto.RecipeListResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API interface. All network calls are suspend functions so they
 * integrate naturally with coroutines. The actual base-URL and mock
 * interception are configured in [NetworkModule].
 */
interface RecipeApiService {

    /**
     * GET /recipes/complexSearch
     * Returns a paged list of recipes. In mock mode the [MockInterceptor]
     * short-circuits this call and returns canned JSON.
     */
    @GET("recipes/complexSearch")
    suspend fun getRecipes(
        @Query("number")    number: Int    = 20,
        @Query("offset")    offset: Int    = 0,
        @Query("addRecipeInformation") addInfo: Boolean = true,
        @Query("fillIngredients")      fillIngredients: Boolean = true,
    ): RecipeListResponseDto

    /**
     * GET /recipes/complexSearch?query={query}
     * Remote full-text search.
     */
    @GET("recipes/complexSearch")
    suspend fun searchRecipes(
        @Query("query")  query: String,
        @Query("number") number: Int = 20,
        @Query("addRecipeInformation") addInfo: Boolean = true,
        @Query("fillIngredients")      fillIngredients: Boolean = true,
    ): RecipeListResponseDto

    /** GET /recipes/{id}/information */
    @GET("recipes/{id}/information")
    suspend fun getRecipeById(
        @Path("id") id: Int,
        @Query("includeNutrition") includeNutrition: Boolean = false,
    ): RecipeDto
}

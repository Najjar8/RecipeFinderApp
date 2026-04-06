package com.recipefinder.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.recipefinder.app.data.local.entity.RecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    // ─── Reads ──────────────────────────────────────────────────────────────

    /** Live stream of ALL cached recipes, ordered by title. */
    @Query("SELECT * FROM recipes ORDER BY title ASC")
    fun getAllRecipes(): Flow<List<RecipeEntity>>

    /** Full-text search across title AND serialised ingredients JSON. */
    @Query(
        """
        SELECT * FROM recipes
        WHERE title     LIKE '%' || :query || '%'
           OR ingredients LIKE '%' || :query || '%'
        ORDER BY title ASC
        """
    )
    fun searchRecipes(query: String): Flow<List<RecipeEntity>>

    /** Single recipe by primary key – returns null if not cached. */
    @Query("SELECT * FROM recipes WHERE id = :id LIMIT 1")
    suspend fun getRecipeById(id: Int): RecipeEntity?

    /** Live stream of favourite recipes only. */
    @Query("SELECT * FROM recipes WHERE isFavorite = 1 ORDER BY title ASC")
    fun getFavoriteRecipes(): Flow<List<RecipeEntity>>

    // ─── Writes ─────────────────────────────────────────────────────────────

    /** Upsert: replace on conflict so refreshes are idempotent. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<RecipeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity)

    @Update
    suspend fun updateRecipe(recipe: RecipeEntity)

    /** Flip the [isFavorite] flag on a single row. */
    @Query("UPDATE recipes SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Int, isFavorite: Boolean)

    @Query("DELETE FROM recipes WHERE id = :id")
    suspend fun deleteRecipeById(id: Int)

    /** Purge cache older than [olderThanMs]. Keeps favourites regardless of age. */
    @Query(
        "DELETE FROM recipes WHERE cachedAt < :olderThanMs AND isFavorite = 0"
    )
    suspend fun deleteStaleCache(olderThanMs: Long)

    // ─── Transactions ────────────────────────────────────────────────────────

    /** Atomic upsert of a list followed by stale-cache pruning. */
    @Transaction
    suspend fun refreshRecipes(recipes: List<RecipeEntity>, maxAgeMs: Long) {
        insertRecipes(recipes)
        deleteStaleCache(System.currentTimeMillis() - maxAgeMs)
    }
}

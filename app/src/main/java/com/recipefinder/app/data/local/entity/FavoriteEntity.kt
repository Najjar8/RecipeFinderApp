package com.recipefinder.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Lightweight join table. Stores only the [recipeId] so we can quickly answer
 * "is this recipe a favourite?" without loading all recipe columns.
 *
 * The full [RecipeEntity] is updated with [isFavorite] = true in the recipes
 * table as well, but this table is the authoritative source.
 */
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val recipeId: Int,
    val savedAt: Long = System.currentTimeMillis(),
)

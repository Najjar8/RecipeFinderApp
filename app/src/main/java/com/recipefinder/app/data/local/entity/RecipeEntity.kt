package com.recipefinder.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.recipefinder.app.data.local.database.StringListConverter

/**
 * Room entity that mirrors the remote recipe DTO.
 * [isFavorite] is kept here so a single query can return full recipe + favourite flag.
 */
@Entity(tableName = "recipes")
@TypeConverters(StringListConverter::class)
data class RecipeEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val imageUrl: String,
    val cookTimeMinutes: Int,
    val servings: Int,
    val likes: Int,
    val difficulty: String,          // stored as label string; mapped back to enum
    val category: String,
    val ingredients: List<String>,
    val instructions: List<String>,
    val isFavorite: Boolean = false,
    val rating: Double = 0.0,
    val authorName: String = "",
    val authorAvatar: String = "",
    val calories: Int = 0,
    val cachedAt: Long = System.currentTimeMillis(),
)

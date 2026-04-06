package com.recipefinder.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.recipefinder.app.data.local.dao.FavoriteDao
import com.recipefinder.app.data.local.dao.RecipeDao
import com.recipefinder.app.data.local.entity.FavoriteEntity
import com.recipefinder.app.data.local.entity.RecipeEntity
import com.recipefinder.app.core.constants.AppConstants

// ─── Type Converter ──────────────────────────────────────────────────────────

/**
 * Converts [List<String>] ↔ JSON string for Room storage.
 * Stored as a compact JSON array in a single TEXT column.
 */
class StringListConverter {
    private val gson = Gson()
    private val type = object : TypeToken<List<String>>() {}.type

    @TypeConverter
    fun fromList(list: List<String>): String = gson.toJson(list)

    @TypeConverter
    fun toList(json: String): List<String> =
        gson.fromJson(json, type) ?: emptyList()
}

// ─── Database ────────────────────────────────────────────────────────────────

/**
 * Single Room database for the entire application.
 * Version bumps require a migration strategy – see [AppConstants.DATABASE_VERSION].
 */
@Database(
    entities  = [RecipeEntity::class, FavoriteEntity::class],
    version   = AppConstants.DATABASE_VERSION,
    exportSchema = true,
)
@TypeConverters(StringListConverter::class)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
    abstract fun favoriteDao(): FavoriteDao
}

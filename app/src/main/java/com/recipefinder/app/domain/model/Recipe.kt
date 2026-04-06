package com.recipefinder.app.domain.model

/**
 * Pure-Kotlin domain model. No Android or framework dependencies here.
 * This is the single source of truth flowing through the app.
 */
data class Recipe(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val cookTimeMinutes: Int,
    val servings: Int,
    val likes: Int,
    val difficulty: Difficulty,
    val category: String,
    val ingredients: List<String>,
    val instructions: List<String>,
    val isFavorite: Boolean = false,
    val rating: Double = 0.0,
    val authorName: String = "",
    val authorAvatar: String = "",
    val calories: Int = 0,
)

enum class Difficulty(val label: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard");

    companion object {
        fun fromLabel(label: String): Difficulty =
            entries.firstOrNull { it.label.equals(label, ignoreCase = true) } ?: EASY
    }
}

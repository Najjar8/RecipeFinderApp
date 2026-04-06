package com.recipefinder.app.domain.model

/**
 * Value object that describes the active filter/sort state.
 * Immutable – ViewModel creates a new copy each time the user changes a filter.
 */
data class RecipeFilter(
    val difficulty: Difficulty? = null,          // null = "All"
    val sortOrder: SortOrder   = SortOrder.DEFAULT,
    val maxCookTimeMinutes: Int? = null,         // null = no cap
    val category: String? = null,               // null = "All"
)

enum class SortOrder(val label: String) {
    DEFAULT("Default"),
    NAME_ASC("Name (A–Z)"),
    NAME_DESC("Name (Z–A)"),
    COOK_TIME_ASC("Cook Time ↑"),
    COOK_TIME_DESC("Cook Time ↓"),
    MOST_LIKED("Most Liked"),
}

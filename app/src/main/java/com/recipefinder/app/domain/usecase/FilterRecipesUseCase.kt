package com.recipefinder.app.domain.usecase

import com.recipefinder.app.domain.model.Recipe
import com.recipefinder.app.domain.model.RecipeFilter
import com.recipefinder.app.domain.model.SortOrder
import javax.inject.Inject

/**
 * Pure in-memory filter + sort. No I/O – safe to call synchronously on any
 * dispatcher. Receives an already-loaded [List<Recipe>] and a [RecipeFilter]
 * and returns the processed list.
 */
class FilterRecipesUseCase @Inject constructor() {

    operator fun invoke(recipes: List<Recipe>, filter: RecipeFilter): List<Recipe> {
        var result = recipes

        // ── Difficulty filter ──────────────────────────────────────────────
        filter.difficulty?.let { diff ->
            result = result.filter { it.difficulty == diff }
        }

        // ── Category filter ────────────────────────────────────────────────
        filter.category?.let { cat ->
            result = result.filter { it.category.equals(cat, ignoreCase = true) }
        }

        // ── Max cook-time cap ──────────────────────────────────────────────
        filter.maxCookTimeMinutes?.let { cap ->
            result = result.filter { it.cookTimeMinutes <= cap }
        }

        // ── Sort ───────────────────────────────────────────────────────────
        result = when (filter.sortOrder) {
            SortOrder.NAME_ASC      -> result.sortedBy      { it.title }
            SortOrder.NAME_DESC     -> result.sortedByDescending { it.title }
            SortOrder.COOK_TIME_ASC -> result.sortedBy      { it.cookTimeMinutes }
            SortOrder.COOK_TIME_DESC-> result.sortedByDescending { it.cookTimeMinutes }
            SortOrder.MOST_LIKED    -> result.sortedByDescending { it.likes }
            SortOrder.DEFAULT       -> result
        }

        return result
    }
}

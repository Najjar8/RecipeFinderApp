package com.recipefinder.app.presentation.navigation

/**
 * Sealed hierarchy of all navigation destinations.
 * Using a sealed class (instead of raw strings) guarantees exhaustive
 * when-expressions and eliminates typo-driven routing bugs.
 */
sealed class Screen(val route: String) {

    // ── Bottom-nav destinations ───────────────────────────────────────────────
    data object Home      : Screen("home")
    data object Search    : Screen("search")
    data object Favorites : Screen("favorites")

    // ── Drill-down destinations ───────────────────────────────────────────────
    data object RecipeDetail : Screen("recipe_detail/{recipeId}") {
        fun createRoute(recipeId: Int) = "recipe_detail/$recipeId"
        const val ARG_RECIPE_ID = "recipeId"
    }

    companion object {
        /** All destinations that appear in the bottom navigation bar. */
        val bottomNavItems = listOf(Home, Search, Favorites)
    }
}

package com.recipefinder.app.presentation.search

import com.recipefinder.app.domain.model.Recipe

/**
 * UI state for the Search screen.
 * [suggestions] holds recently-searched terms for quick re-search.
 */
data class SearchUiState(
    val query:        String       = "",
    val results:      List<Recipe> = emptyList(),
    val isLoading:    Boolean      = false,
    val errorMessage: String?      = null,
    val hasSearched:  Boolean      = false,   // false = show idle/suggestions UI
    val suggestions:  List<String> = emptyList(),
) {
    val isEmpty: Boolean get() =
        hasSearched && !isLoading && results.isEmpty() && errorMessage == null
}

package com.recipefinder.app.presentation.search

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.NorthWest
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.recipefinder.app.R
import com.recipefinder.app.presentation.components.EmptyState
import com.recipefinder.app.presentation.components.ErrorMessage
import com.recipefinder.app.presentation.components.LoadingIndicator
import com.recipefinder.app.presentation.components.RecipeCard
import com.recipefinder.app.presentation.components.RecipeSearchBar
import com.recipefinder.app.ui.theme.RecipeFinderTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onRecipeClick: (Int) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    RecipeSearchBar(
                        query         = uiState.query,
                        onQueryChange = viewModel::onQueryChange,
                        modifier      = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp),
                    )
                },
            )
        }
    ) { innerPadding ->

        AnimatedContent(
            targetState = when {
                uiState.isLoading                    -> SearchContentState.LOADING
                uiState.errorMessage != null         -> SearchContentState.ERROR
                uiState.isEmpty                      -> SearchContentState.EMPTY
                uiState.hasSearched                  -> SearchContentState.RESULTS
                else                                 -> SearchContentState.IDLE
            },
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label          = "searchContent",
            modifier       = Modifier.padding(innerPadding),
        ) { contentState ->
            when (contentState) {
                SearchContentState.LOADING -> LoadingIndicator()

                SearchContentState.ERROR -> ErrorMessage(
                    message = uiState.errorMessage ?: stringResource(R.string.error_generic),
                    onRetry = viewModel::onSearchSubmit,
                )

                SearchContentState.EMPTY -> EmptyState(
                    message = "No recipes found for \"${uiState.query}\"\nTry different keywords."
                )

                SearchContentState.RESULTS -> SearchResultsGrid(
                    uiState                 = uiState,
                    onRecipeClick           = onRecipeClick,
                    onFavClick              = viewModel::onToggleFavorite,
                    animatedVisibilityScope = animatedVisibilityScope,
                    sharedTransitionScope   = sharedTransitionScope,
                )

                SearchContentState.IDLE -> SearchIdleState(
                    suggestions       = uiState.suggestions,
                    onSuggestionClick = viewModel::onSuggestionClick,
                )
            }
        }
    }
}

// ─── Content state enum ───────────────────────────────────────────────────────

private enum class SearchContentState { IDLE, LOADING, RESULTS, EMPTY, ERROR }

// ─── Search results grid ──────────────────────────────────────────────────────

@Composable
private fun SearchResultsGrid(
    uiState: SearchUiState,
    onRecipeClick: (Int) -> Unit,
    onFavClick: (com.recipefinder.app.domain.model.Recipe) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text     = "${uiState.results.size} result${if (uiState.results.size != 1) "s" else ""} for \"${uiState.query}\"",
            style    = MaterialTheme.typography.bodySmall,
            color    = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
        LazyVerticalGrid(
            columns               = GridCells.Adaptive(minSize = 160.dp),
            contentPadding        = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement   = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier              = Modifier.fillMaxSize(),
        ) {
            items(
                items = uiState.results,
                key   = { it.id },
            ) { recipe ->
                RecipeCard(
                    recipe                  = recipe,
                    onCardClick             = onRecipeClick,
                    onFavoriteClick         = onFavClick,
                    animatedVisibilityScope = animatedVisibilityScope,
                    sharedTransitionScope   = sharedTransitionScope,
                )
            }
        }
    }
}

// ─── Idle / suggestions state ─────────────────────────────────────────────────

@Composable
private fun SearchIdleState(
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(16.dp))

        if (suggestions.isNotEmpty()) {
            Text(
                text  = "Recent searches",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            LazyColumn {
                items(suggestions) { suggestion ->
                    SuggestionRow(
                        text    = suggestion,
                        onClick = { onSuggestionClick(suggestion) },
                    )
                    HorizontalDivider()
                }
            }
        } else {
            // Hint chips for first-time users
            SearchHints()
        }
    }
}

@Composable
private fun SuggestionRow(text: String, onClick: () -> Unit) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector        = Icons.Outlined.History,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier           = Modifier.size(18.dp),
            )
            Text(text = text, style = MaterialTheme.typography.bodyMedium)
        }
        Icon(
            imageVector        = Icons.Outlined.NorthWest,
            contentDescription = "Use suggestion",
            tint               = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier           = Modifier.size(16.dp),
        )
    }
}

@Composable
private fun SearchHints() {
    val hints = listOf("Chicken", "Pasta", "Vegetarian", "Dessert", "Quick meals", "Pizza")
    Column {
        Spacer(Modifier.height(32.dp))
        Icon(
            imageVector        = Icons.Outlined.History,
            contentDescription = null,
            tint               = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier           = Modifier
                .size(56.dp)
                .align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text     = "Search for recipes by name,\ningredient, or category",
            style    = MaterialTheme.typography.bodyMedium,
            color    = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text  = "Try searching for",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(8.dp))
        hints.forEach { hint ->
            SuggestionRow(text = hint, onClick = {})
            HorizontalDivider()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchIdlePreview() {
    RecipeFinderTheme {
        SearchIdleState(
            suggestions       = listOf("Chicken", "Pasta", "Pizza"),
            onSuggestionClick = {},
        )
    }
}

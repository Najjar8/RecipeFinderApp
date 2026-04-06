package com.recipefinder.app.presentation.favorites

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.recipefinder.app.R
import com.recipefinder.app.core.util.formatCookTime
import com.recipefinder.app.domain.model.Difficulty
import com.recipefinder.app.domain.model.Recipe
import com.recipefinder.app.presentation.components.DifficultyBadge
import com.recipefinder.app.presentation.components.LoadingIndicator
import com.recipefinder.app.ui.theme.HeartRed
import com.recipefinder.app.ui.theme.RecipeFinderTheme
@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onRecipeClick: (Int) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text  = stringResource(R.string.nav_favorites),
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> LoadingIndicator(Modifier.padding(innerPadding))
            uiState.isEmpty   -> FavoritesEmptyState(Modifier.padding(innerPadding))
            else              -> FavoritesList(
                recipes        = uiState.recipes,
                innerPadding   = innerPadding,
                onRecipeClick  = onRecipeClick,
                onRemove       = viewModel::onRemoveFavorite,
            )
        }
    }
}

// ─── Favorites list with swipe-to-dismiss ─────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoritesList(
    recipes: List<Recipe>,
    innerPadding: PaddingValues,
    onRecipeClick: (Int) -> Unit,
    onRemove: (Recipe) -> Unit,
) {
    LazyColumn(
        contentPadding      = PaddingValues(
            start  = 16.dp,
            end    = 16.dp,
            top    = innerPadding.calculateTopPadding() + 8.dp,
            bottom = innerPadding.calculateBottomPadding() + 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier            = Modifier.fillMaxSize(),
    ) {
        item {
            Text(
                text  = "${recipes.size} saved recipe${if (recipes.size != 1) "s" else ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(4.dp))
        }

        items(
            items = recipes,
            key   = { it.id },
        ) { recipe ->
            SwipeToDismissFavoriteItem(
                recipe        = recipe,
                onRecipeClick = onRecipeClick,
                onDismiss     = { onRemove(recipe) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDismissFavoriteItem(
    recipe: Recipe,
    onRecipeClick: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var isVisible by remember { mutableStateOf(true) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                isVisible = false
                true
            } else false
        }
    )

    LaunchedEffect(isVisible) {
        if (!isVisible) {
            // Let the shrink animation play before calling onDismiss
            kotlinx.coroutines.delay(300L)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        exit    = shrinkVertically(tween(300)) + fadeOut(tween(200)),
    ) {
        SwipeToDismissBox(
            state            = dismissState,
            enableDismissFromStartToEnd = false,
            backgroundContent = {
                // Red swipe background
                Box(
                    contentAlignment = Alignment.CenterEnd,
                    modifier         = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(end = 20.dp),
                ) {
                    Icon(
                        imageVector        = Icons.Filled.Delete,
                        contentDescription = "Remove from favorites",
                        tint               = MaterialTheme.colorScheme.onErrorContainer,
                        modifier           = Modifier.size(24.dp),
                    )
                }
            },
        ) {
            FavoriteRecipeRow(
                recipe   = recipe,
                onClick  = { onRecipeClick(recipe.id) },
            )
        }
    }
}

@Composable
private fun FavoriteRecipeRow(
    recipe: Recipe,
    onClick: () -> Unit,
) {
    Card(
        onClick   = onClick,
        modifier  = Modifier.fillMaxWidth(),
        shape     = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier          = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Thumbnail
            AsyncImage(
                model              = recipe.imageUrl,
                contentDescription = recipe.title,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier
                    .size(72.dp)
                    .clip(MaterialTheme.shapes.small),
            )

            // Text info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            ) {
                Text(
                    text     = recipe.title,
                    style    = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    DifficultyBadge(difficulty = recipe.difficulty)
                    Text(
                        text  = recipe.cookTimeMinutes.formatCookTime(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // Favorite heart indicator
            Icon(
                imageVector        = Icons.Outlined.Favorite,
                contentDescription = null,
                tint               = HeartRed,
                modifier           = Modifier.size(20.dp),
            )
        }
    }
}

// ─── Empty state ──────────────────────────────────────────────────────────────

@Composable
private fun FavoritesEmptyState(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier            = modifier
            .fillMaxSize()
            .padding(32.dp),
    ) {
        Icon(
            imageVector        = Icons.Outlined.Favorite,
            contentDescription = null,
            tint               = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier           = Modifier.size(80.dp),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text  = stringResource(R.string.no_favorites),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text  = "Tap the heart icon on any recipe to save it here.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        )
    }
}

// ─── Preview ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun FavoritesEmptyPreview() {
    RecipeFinderTheme {
        FavoritesEmptyState()
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoriteRowPreview() {
    RecipeFinderTheme {
        FavoriteRecipeRow(
            recipe = Recipe(
                id = 1, title = "Creamy Tuscan Chicken", imageUrl = "",
                cookTimeMinutes = 30, servings = 4, likes = 234,
                difficulty = Difficulty.EASY, category = "chicken",
                ingredients = emptyList(), instructions = emptyList(),
                isFavorite = true,
            ),
            onClick = {},
        )
    }
}

package com.recipefinder.app.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.recipefinder.app.R
import com.recipefinder.app.domain.model.Recipe
import com.recipefinder.app.domain.model.RecipeFilter
import com.recipefinder.app.presentation.components.EmptyState
import com.recipefinder.app.presentation.components.FilterChipsRow
import com.recipefinder.app.presentation.components.LoadingIndicator
import com.recipefinder.app.presentation.components.RecipeCard
import com.recipefinder.app.presentation.components.RecipeSearchBar
import com.recipefinder.app.ui.theme.RecipeFinderTheme
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalContext
import com.recipefinder.app.core.util.formatCookTime
import android.net.Uri
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.content.FileProvider
import coil.imageLoader
import coil.request.ImageRequest
import java.io.File
import kotlinx.coroutines.launch
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onRecipeClick: (Int) -> Unit,
    onAddRecipeClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.loadRecipes()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val context = LocalContext.current
    val scope   = rememberCoroutineScope()

    uiState.recipeToDelete?.let { recipe ->
        AlertDialog(
            onDismissRequest = viewModel::onDeleteCancelled,
            title   = { Text("Delete Recipe") },
            text    = { Text("Remove \"${recipe.title}\" from your recipes? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = viewModel::onDeleteConfirmed) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDeleteCancelled) { Text("Cancel") }
            },
        )
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            snackbarState.showSnackbar(message = msg, duration = SnackbarDuration.Short)
            viewModel.onErrorDismissed()
        }
    }

    Scaffold(
        modifier            = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost        = { SnackbarHost(snackbarState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text  = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
                colors        = TopAppBarDefaults.topAppBarColors(
                    containerColor          = MaterialTheme.colorScheme.background,
                    scrolledContainerColor  = MaterialTheme.colorScheme.background,
                ),
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = onAddRecipeClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor   = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add recipe")
            }
        },
    ) { innerPadding ->
        when {
            uiState.isLoading && uiState.allRecipes.isEmpty() -> {
                LoadingIndicator(modifier = Modifier.fillMaxSize().padding(innerPadding))
            }

            uiState.isEmpty && uiState.searchQuery.isBlank() && uiState.filter == RecipeFilter() -> {
                EmptyState(
                    message  = stringResource(R.string.no_recipes_found),
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                )
            }

            else -> {
                RecipeGrid(
                    uiState                 = uiState,
                    innerPadding            = innerPadding,
                    onRecipeClick           = onRecipeClick,
                    onFavClick              = viewModel::onToggleFavorite,
                    onDeleteClick           = viewModel::onDeleteRequest,
                    onShareClick            = { recipe -> scope.launch { shareRecipeWithImage(context, recipe) } },
                    onQueryChange           = viewModel::onSearchQueryChange,
                    onFilterChange          = viewModel::onFilterChange,
                    onLoadMore              = viewModel::loadMore,
                    sharedTransitionScope   = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                )
            }
        }
    }
}

@Composable
private fun RecipeGrid(
    uiState: HomeUiState,
    innerPadding: PaddingValues,
    onRecipeClick: (Int) -> Unit,
    onFavClick: (Recipe) -> Unit,
    onDeleteClick: (Recipe) -> Unit,
    onShareClick: (Recipe) -> Unit,
    onQueryChange: (String) -> Unit,
    onFilterChange: (RecipeFilter) -> Unit,
    onLoadMore: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    LazyVerticalGrid(
        columns               = GridCells.Adaptive(minSize = 160.dp),
        contentPadding        = PaddingValues(
            start  = innerPadding.calculateStartPadding(LayoutDirection.Ltr) + 16.dp,
            end    = innerPadding.calculateEndPadding(LayoutDirection.Ltr) + 16.dp,
            top    = innerPadding.calculateTopPadding() + 8.dp,
            bottom = innerPadding.calculateBottomPadding() + 16.dp,
        ),
        verticalArrangement   = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier              = Modifier.fillMaxSize(),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            RecipeSearchBar(
                query         = uiState.searchQuery,
                onQueryChange = onQueryChange,
                modifier      = Modifier.fillMaxWidth(),
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            FilterChipsRow(
                filter         = uiState.filter,
                onFilterChange = onFilterChange,
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Column {
                Text(
                    text  = "Discover Recipes",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text  = "${uiState.recipes.size} recipes found",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(4.dp))
            }
        }

        items(
            items = uiState.visibleRecipes,
            key   = { it.id },
        ) { recipe ->
            AnimatedVisibility(
                visible = true,
                enter   = fadeIn(),
                exit    = fadeOut(),
            ) {
                RecipeCard(
                    recipe                  = recipe,
                    onCardClick             = onRecipeClick,
                    onFavoriteClick         = onFavClick,
                    onDeleteClick           = onDeleteClick,
                    onShareClick            = onShareClick,
                    sharedTransitionScope   = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                )
            }
        }

        if (uiState.recipes.isEmpty() && uiState.searchQuery.isNotBlank()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                EmptyState(
                    message  = "No recipes found for \"${uiState.searchQuery}\"",
                    modifier = Modifier.height(200.dp),
                )
            }
        }

        // Load-more trigger: when this item scrolls into view, fetch next page.
        if (uiState.canLoadMore) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                LaunchedEffect(uiState.displayedCount) { onLoadMore() }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier         = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        strokeWidth = 2.dp,
                    )
                }
            }
        }
    }
}


private suspend fun shareRecipeWithImage(context: android.content.Context, recipe: Recipe) {
    val text = buildString {
        appendLine("🍽️ ${recipe.title}")
        appendLine()
        appendLine("⏱ ${recipe.cookTimeMinutes.formatCookTime()}  •  👥 ${recipe.servings} servings  •  ${recipe.difficulty.label}")
        appendLine()
        appendLine("INGREDIENTS")
        recipe.ingredients.forEach { appendLine("• $it") }
        appendLine()
        appendLine("INSTRUCTIONS")
        recipe.instructions.forEachIndexed { i, step -> appendLine("${i + 1}. $step") }
        appendLine()
        appendLine("Shared via RecipeDiscover")
    }

    val imageUri: Uri? = if (recipe.imageUrl.isNotBlank()) {
        try {
            if (recipe.imageUrl.startsWith("content://")) {
                // User-added local image — share the URI directly
                Uri.parse(recipe.imageUrl)
            } else {
                // Remote HTTPS image — download via Coil and write to cache
                val result = context.imageLoader.execute(
                    ImageRequest.Builder(context)
                        .data(recipe.imageUrl)
                        .allowHardware(false)   // must be software bitmap to compress
                        .build()
                )
                val bitmap = (result.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
                if (bitmap != null) {
                    val dir  = File(context.cacheDir, "share").also { it.mkdirs() }
                    val file = File(dir, "recipe_${recipe.id}.jpg")
                    file.outputStream().use { bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, it) }
                    FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                } else null
            }
        } catch (_: Exception) { null }
    } else null

    val intent = if (imageUri != null) {
        Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_STREAM, imageUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    } else {
        // Fallback: text only if image could not be loaded
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, recipe.title)
            putExtra(Intent.EXTRA_TEXT, text)
        }
    }

    context.startActivity(Intent.createChooser(intent, "Share \"${recipe.title}\""))
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    RecipeFinderTheme {
        androidx.compose.animation.SharedTransitionLayout {
            androidx.compose.animation.AnimatedVisibility(visible = true) {
                HomeScreen(
                    onRecipeClick           = {},
                    onAddRecipeClick = {},
                    animatedVisibilityScope = this,
                    sharedTransitionScope   = this@SharedTransitionLayout,
                )
            }
        }
    }
}


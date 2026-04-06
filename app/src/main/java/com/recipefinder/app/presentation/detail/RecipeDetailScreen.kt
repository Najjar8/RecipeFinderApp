package com.recipefinder.app.presentation.detail

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.recipefinder.app.R
import com.recipefinder.app.core.util.formatCookTime
import com.recipefinder.app.domain.model.Recipe
import com.recipefinder.app.presentation.components.DifficultyBadge
import com.recipefinder.app.presentation.components.ErrorMessage
import com.recipefinder.app.presentation.components.LoadingIndicator
import com.recipefinder.app.ui.theme.HeartRed
import com.recipefinder.app.ui.theme.RecipeFinderTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: RecipeDetailViewModel = hiltViewModel(),
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    with(sharedTransitionScope) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        FilledTonalIconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    actions = {
                        uiState.recipe?.let { recipe ->
                            FilledTonalIconButton(
                                onClick = viewModel::onToggleFavorite,
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = if (recipe.isFavorite)
                                        HeartRed.copy(alpha = 0.15f)
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant,
                                ),
                            ) {
                                Icon(
                                    imageVector = if (recipe.isFavorite)
                                        Icons.Filled.Favorite
                                    else
                                        Icons.Outlined.FavoriteBorder,
                                    contentDescription = stringResource(
                                        if (recipe.isFavorite) R.string.remove_from_favorites
                                        else R.string.add_to_favorites
                                    ),
                                    tint = if (recipe.isFavorite) HeartRed
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                )
            }
        ) { innerPadding ->
            when {
                uiState.isLoading -> LoadingIndicator(Modifier.padding(innerPadding))
                uiState.errorMessage != null -> ErrorMessage(
                    message = uiState.errorMessage!!,
                    onRetry = viewModel::loadRecipe,
                    modifier = Modifier.padding(innerPadding),
                )

                uiState.recipe != null -> RecipeDetailContent(
                    recipe = uiState.recipe!!,
                    topPadding = innerPadding.calculateTopPadding(),
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                )
            }
        }
    }
}

@Composable
private fun RecipeDetailContent(
    recipe: Recipe,
    topPadding: androidx.compose.ui.unit.Dp,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    with(sharedTransitionScope) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {

        // ── Hero image ─────────────────────────────────────────────────────
        item {
            Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                AsyncImage(
                    model              = recipe.imageUrl,
                    contentDescription = recipe.title,
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier.fillMaxSize()
                        .sharedElement(
                        sharedContentState = rememberSharedContentState(key = "recipe-image-${recipe.id}"),
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                )
                // Gradient scrim for readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f))
                            )
                        )
                )
                // Difficulty badge anchored bottom-left over image
                DifficultyBadge(
                    difficulty = recipe.difficulty,
                    modifier   = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                )
            }
        }

        // ── Title + author ─────────────────────────────────────────────────
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text  = recipe.title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(16.dp)
                        .sharedElement( // 👈 same key as in the card
                            sharedContentState = rememberSharedContentState(key = "recipe-title-${recipe.id}"),
                            animatedVisibilityScope = animatedVisibilityScope,
                        )
                )
                if (recipe.authorName.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text  = "by ${recipe.authorName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        // ── Stats row ──────────────────────────────────────────────────────
        item {
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                StatCard(
                    icon    = Icons.Outlined.Timer,
                    value   = recipe.cookTimeMinutes.formatCookTime(),
                    label   = stringResource(R.string.cook_time),
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    icon    = Icons.Outlined.People,
                    value   = recipe.servings.toString(),
                    label   = stringResource(R.string.servings),
                    modifier = Modifier.weight(1f),
                )
                if (recipe.calories > 0) {
                    StatCard(
                        icon    = Icons.Outlined.LocalFireDepartment,
                        value   = "${recipe.calories}",
                        label   = "Calories",
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        }

        // ── Ingredients ────────────────────────────────────────────────────
        item {
            SectionHeader(title = stringResource(R.string.ingredients))
        }

        itemsIndexed(recipe.ingredients) { _, ingredient ->
            Row(
                modifier          = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 7.dp)
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text  = ingredient,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        item { Spacer(Modifier.height(8.dp)); HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp)) }

        // ── Instructions ───────────────────────────────────────────────────
        item {
            SectionHeader(title = stringResource(R.string.instructions))
        }

        itemsIndexed(recipe.instructions) { index, step ->
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.Top,
            ) {
                // Numbered circle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier         = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                ) {
                    Text(
                        text  = "${index + 1}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text     = step,
                    style    = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
    } // end with(sharedTransitionScope)
}

@Composable
private fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(vertical = 12.dp, horizontal = 8.dp),
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = MaterialTheme.colorScheme.primary,
            modifier           = Modifier.size(22.dp),
        )
        Spacer(Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.titleSmall)
        Text(
            text  = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text     = title,
        style    = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
    )
}

package com.recipefinder.app.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.recipefinder.app.core.util.formatCookTime
import com.recipefinder.app.domain.model.Difficulty
import com.recipefinder.app.domain.model.Recipe
import com.recipefinder.app.ui.theme.HeartRed
import com.recipefinder.app.ui.theme.RecipeFinderTheme

@Composable
fun RecipeCard(
    recipe: Recipe,
    onCardClick: (Int) -> Unit,
    onFavoriteClick: (Recipe) -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    with(sharedTransitionScope) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick(recipe.id) },
        shape     = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 6.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column {
            // ── Hero image with favourite button overlay ───────────────────
            Box {
                AsyncImage(
                    model             = recipe.imageUrl,
                    contentDescription = recipe.title,
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .sharedElement(
                        sharedContentState = rememberSharedContentState(key = "recipe-image-${recipe.id}"),
                        animatedVisibilityScope = animatedVisibilityScope,
                )
                )

                // Difficulty badge — top-left
                DifficultyBadge(
                    difficulty = recipe.difficulty,
                    modifier   = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                )

                // Favourite button — top-right
                FavoriteButton(
                    isFavorite = recipe.isFavorite,
                    onClick    = { onFavoriteClick(recipe) },
                    modifier   = Modifier.align(Alignment.TopEnd),
                )
            }

            // ── Text content ──────────────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                Text(
                    text     = recipe.title,
                    style    = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .sharedElement( // 👈 optional but looks great
                            sharedContentState = rememberSharedContentState(key = "recipe-title-${recipe.id}"),
                            animatedVisibilityScope = animatedVisibilityScope,
                        )
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically,
                    modifier              = Modifier.fillMaxWidth(),
                ) {
                    RecipeMetaChip(
                        icon  = Icons.Outlined.Timer,
                        label = recipe.cookTimeMinutes.formatCookTime(),
                    )
                    RecipeMetaChip(
                        icon  = Icons.Outlined.People,
                        label = "${recipe.servings} servings",
                    )
                    RecipeMetaChip(
                        icon  = Icons.Filled.Favorite,
                        label = recipe.likes.toString(),
                        tint  = HeartRed,
                    )
                }
            }
        }
    }
    }
}

// ── Sub-components ────────────────────────────────────────────────────────────

@Composable
private fun FavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tint by animateColorAsState(
        targetValue = if (isFavorite) HeartRed else Color.White,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "favTint",
    )
    Surface(
        modifier  = modifier.padding(6.dp),
        shape     = MaterialTheme.shapes.extraLarge,
        color     = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        shadowElevation = 2.dp,
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                tint   = tint,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun RecipeMetaChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier          = modifier,
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = tint,
            modifier           = Modifier.size(14.dp),
        )
        Spacer(Modifier.width(3.dp))
        Text(
            text  = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun RecipeCardPreview() {
    RecipeFinderTheme {
        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                RecipeCard(
                    recipe = Recipe(
                        id = 1, title = "Creamy Tuscan Chicken", imageUrl = "",
                        cookTimeMinutes = 30, servings = 4, likes = 234,
                        difficulty = Difficulty.EASY, category = "chicken",
                        ingredients = emptyList(), instructions = emptyList(),
                        isFavorite = true,
                    ),
                    onCardClick             = {},
                    onFavoriteClick         = {},
                    sharedTransitionScope   = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                )
            }
        }
    }
}

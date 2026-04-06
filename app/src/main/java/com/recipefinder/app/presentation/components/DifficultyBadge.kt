package com.recipefinder.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.recipefinder.app.domain.model.Difficulty
import com.recipefinder.app.ui.theme.Amber40
import com.recipefinder.app.ui.theme.Green40
import com.recipefinder.app.ui.theme.RecipeFinderTheme
import com.recipefinder.app.ui.theme.Red40

@Composable
fun DifficultyBadge(
    difficulty: Difficulty,
    modifier: Modifier = Modifier,
) {
    val (bgColor, textColor) = when (difficulty) {
        Difficulty.EASY   -> Green40 to Color.White
        Difficulty.MEDIUM -> Amber40 to Color.White
        Difficulty.HARD   -> Red40   to Color.White
    }

    Text(
        text  = difficulty.label,
        style = MaterialTheme.typography.labelSmall,
        color = textColor,
        modifier = modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

@Preview
@Composable
private fun DifficultyBadgePreview() {
    RecipeFinderTheme {
        DifficultyBadge(difficulty = Difficulty.MEDIUM)
    }
}

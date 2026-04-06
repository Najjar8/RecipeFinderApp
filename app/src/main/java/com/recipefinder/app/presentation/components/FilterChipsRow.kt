package com.recipefinder.app.presentation.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.recipefinder.app.domain.model.Difficulty
import com.recipefinder.app.domain.model.RecipeFilter
import com.recipefinder.app.domain.model.SortOrder
import com.recipefinder.app.ui.theme.RecipeFinderTheme

@Composable
fun FilterChipsRow(
    filter: RecipeFilter,
    onFilterChange: (RecipeFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier              = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        // ── "All" / Difficulty chips ──────────────────────────────────────
        DifficultyFilterChip(
            label     = "All",
            selected  = filter.difficulty == null,
            onClick   = { onFilterChange(filter.copy(difficulty = null)) },
        )

        Difficulty.entries.forEach { diff ->
            DifficultyFilterChip(
                label    = diff.label,
                selected = filter.difficulty == diff,
                onClick  = {
                    onFilterChange(
                        filter.copy(
                            difficulty = if (filter.difficulty == diff) null else diff
                        )
                    )
                },
            )
        }

        Spacer(Modifier.width(8.dp))

        // ── Sort chips ────────────────────────────────────────────────────
        SortOrder.entries.filter { it != SortOrder.DEFAULT }.forEach { order ->
            SortChip(
                label    = order.label,
                selected = filter.sortOrder == order,
                onClick  = {
                    onFilterChange(
                        filter.copy(
                            sortOrder = if (filter.sortOrder == order) SortOrder.DEFAULT else order
                        )
                    )
                },
            )
        }
    }
}

// ── Private chip components ───────────────────────────────────────────────────

@Composable
private fun DifficultyFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick  = onClick,
        label    = { Text(label, style = MaterialTheme.typography.labelMedium) },
        colors   = FilterChipDefaults.filterChipColors(
            selectedContainerColor    = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor        = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    )
}

@Composable
private fun SortChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick  = onClick,
        label    = { Text(label, style = MaterialTheme.typography.labelMedium) },
        colors   = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedLabelColor     = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
    )
}

@Preview(showBackground = true)
@Composable
private fun FilterChipsRowPreview() {
    RecipeFinderTheme {
        FilterChipsRow(
            filter         = RecipeFilter(difficulty = Difficulty.EASY),
            onFilterChange = {},
        )
    }
}

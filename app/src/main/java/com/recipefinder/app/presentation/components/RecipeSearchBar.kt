package com.recipefinder.app.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.recipefinder.app.R
import com.recipefinder.app.ui.theme.RecipeFinderTheme

@Composable
fun RecipeSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = stringResource(R.string.search_hint),
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value        = query,
        onValueChange = onQueryChange,
        modifier     = modifier
            .fillMaxWidth()
            .height(56.dp),
        placeholder  = {
            Text(placeholder, style = MaterialTheme.typography.bodyMedium)
        },
        leadingIcon  = {
            Icon(Icons.Outlined.Search, contentDescription = null)
        },
        trailingIcon = {
            AnimatedVisibility(visible = query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Filled.Close, contentDescription = "Clear search")
                }
            }
        },
        singleLine        = true,
        keyboardOptions   = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions   = KeyboardActions(onSearch = { focusManager.clearFocus() }),
        shape             = MaterialTheme.shapes.extraLarge,
        colors            = TextFieldDefaults.colors(
            focusedContainerColor   = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            focusedIndicatorColor   = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = Color.Transparent,
        ),
    )
}

@Preview(showBackground = true)
@Composable
private fun RecipeSearchBarPreview() {
    RecipeFinderTheme {
        RecipeSearchBar(query = "Chicken", onQueryChange = {})
    }
}

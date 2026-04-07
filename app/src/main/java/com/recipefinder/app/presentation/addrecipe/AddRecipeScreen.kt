package com.recipefinder.app.presentation.addrecipe

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.recipefinder.app.domain.model.Difficulty

@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddRecipeViewModel = hiltViewModel(),
) {
    val uiState       by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarState = remember { SnackbarHostState() }
    val context       = LocalContext.current

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onNavigateBack()
    }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { snackbarState.showSnackbar(it) }
    }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {}
            viewModel.onImageSelected(it.toString())
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarState) },
        topBar = {
            TopAppBar(
                title = { Text("Add Recipe") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(Modifier.size(24.dp).padding(end = 16.dp), strokeWidth = 2.dp)
                    } else {
                        Button(onClick = viewModel::onSave, modifier = Modifier.padding(end = 8.dp)) {
                            Text("Save")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp,
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 120.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
        ) {

            // Image picker
            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
                        .clickable {
                            imagePicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                ) {
                    if (uiState.imageUri.isBlank()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.AddPhotoAlternate, "Add photo", Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(8.dp))
                            Text("Tap to add a photo", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        AsyncImage(model = uiState.imageUri, contentDescription = "Recipe photo", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        Box(
                            Modifier.align(Alignment.BottomEnd).padding(8.dp)
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Change photo", style = MaterialTheme.typography.labelSmall, color = Color.White)
                        }
                    }
                }
            }

            // Title
            item {
                OutlinedTextField(
                    value = uiState.title, onValueChange = viewModel::onTitleChange,
                    label = { Text("Recipe Title *") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                )
            }

            // Category
            item {
                OutlinedTextField(
                    value = uiState.category, onValueChange = viewModel::onCategoryChange,
                    label = { Text("Category") }, placeholder = { Text("e.g. chicken, pasta, dessert") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                )
            }

            // Difficulty
            item {
                Text("Difficulty", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Difficulty.entries.forEach { diff ->
                        FilterChip(
                            selected = uiState.difficulty == diff,
                            onClick = { viewModel.onDifficultyChange(diff) },
                            label = { Text(diff.label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            ),
                        )
                    }
                }
            }

            // Cook time + Servings
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = uiState.cookTimeMinutes, onValueChange = viewModel::onCookTimeChange,
                        label = { Text("Cook Time (min) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true, modifier = Modifier.weight(1f),
                    )
                    OutlinedTextField(
                        value = uiState.servings, onValueChange = viewModel::onServingsChange,
                        label = { Text("Servings *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true, modifier = Modifier.weight(1f),
                    )
                }
            }

            // Calories
            item {
                OutlinedTextField(
                    value = uiState.calories, onValueChange = viewModel::onCaloriesChange,
                    label = { Text("Calories per serving (optional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true, modifier = Modifier.fillMaxWidth(),
                )
            }

            // Ingredients header
            item {
                HorizontalDivider()
                Spacer(Modifier.height(4.dp))
                Text("Ingredients *", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            }

            itemsIndexed(uiState.ingredients) { index, ingredient ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = ingredient, onValueChange = { viewModel.onIngredientChange(index, it) },
                        label = { Text("Ingredient ${index + 1}") },
                        placeholder = { Text("e.g. 2 cloves garlic, minced") },
                        singleLine = true, modifier = Modifier.weight(1f),
                    )
                    if (uiState.ingredients.size > 1) {
                        IconButton(onClick = { viewModel.onRemoveIngredient(index) }) {
                            Icon(Icons.Filled.Close, "Remove", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        Spacer(Modifier.width(48.dp))
                    }
                }
            }

            item {
                TextButton(onClick = viewModel::onAddIngredient) {
                    Icon(Icons.Filled.Add, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Add Ingredient")
                }
            }

            // Instructions header
            item {
                HorizontalDivider()
                Spacer(Modifier.height(4.dp))
                Text("Instructions *", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            }

            itemsIndexed(uiState.instructions) { index, instruction ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(28.dp).clip(MaterialTheme.shapes.extraLarge)
                            .background(MaterialTheme.colorScheme.primary),
                    ) {
                        Text("${index + 1}", style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(
                        value = instruction, onValueChange = { viewModel.onInstructionChange(index, it) },
                        label = { Text("Step ${index + 1}") },
                        placeholder = { Text("Describe this step…") },
                        singleLine = false, modifier = Modifier.weight(1f),
                    )
                    if (uiState.instructions.size > 1) {
                        IconButton(onClick = { viewModel.onRemoveInstruction(index) }) {
                            Icon(Icons.Filled.Close, "Remove", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        Spacer(Modifier.width(48.dp))
                    }
                }
            }

            item {
                TextButton(onClick = viewModel::onAddInstruction) {
                    Icon(Icons.Filled.Add, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Add Step")
                }
            }
        }
    }
}
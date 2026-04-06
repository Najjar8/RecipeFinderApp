package com.recipefinder.app.presentation.addrecipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipefinder.app.domain.model.Difficulty
import com.recipefinder.app.domain.model.Recipe
import com.recipefinder.app.domain.usecase.AddRecipeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random
import javax.inject.Inject

@HiltViewModel
class AddRecipeViewModel @Inject constructor(
    private val addRecipeUseCase: AddRecipeUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddRecipeUiState())
    val uiState: StateFlow<AddRecipeUiState> = _uiState.asStateFlow()

    fun onTitleChange(value: String)      = _uiState.update { it.copy(title = value, errorMessage = null) }
    fun onImageSelected(uri: String)      = _uiState.update { it.copy(imageUri = uri) }
    fun onCategoryChange(value: String)   = _uiState.update { it.copy(category = value) }
    fun onDifficultyChange(d: Difficulty) = _uiState.update { it.copy(difficulty = d) }
    fun onCookTimeChange(value: String)   = _uiState.update { it.copy(cookTimeMinutes = value.filter { it.isDigit() }) }
    fun onServingsChange(value: String)   = _uiState.update { it.copy(servings = value.filter { it.isDigit() }) }
    fun onCaloriesChange(value: String)   = _uiState.update { it.copy(calories = value.filter { it.isDigit() }) }

    fun onIngredientChange(index: Int, value: String) {
        _uiState.update { state ->
            state.copy(ingredients = state.ingredients.toMutableList().also { it[index] = value })
        }
    }
    fun onAddIngredient() = _uiState.update { it.copy(ingredients = it.ingredients + "") }
    fun onRemoveIngredient(index: Int) {
        _uiState.update { state ->
            state.copy(ingredients = state.ingredients.toMutableList().also { it.removeAt(index) })
        }
    }

    fun onInstructionChange(index: Int, value: String) {
        _uiState.update { state ->
            state.copy(instructions = state.instructions.toMutableList().also { it[index] = value })
        }
    }
    fun onAddInstruction() = _uiState.update { it.copy(instructions = it.instructions + "") }
    fun onRemoveInstruction(index: Int) {
        _uiState.update { state ->
            state.copy(instructions = state.instructions.toMutableList().also { it.removeAt(index) })
        }
    }

    fun onSave() {
        val state = _uiState.value
        if (state.title.isBlank()) { _uiState.update { it.copy(errorMessage = "Recipe title is required") }; return }
        val cookTime = state.cookTimeMinutes.toIntOrNull()
        if (cookTime == null || cookTime <= 0) { _uiState.update { it.copy(errorMessage = "Enter a valid cook time in minutes") }; return }
        val servings = state.servings.toIntOrNull()
        if (servings == null || servings <= 0) { _uiState.update { it.copy(errorMessage = "Enter a valid number of servings") }; return }
        val ingredients = state.ingredients.map { it.trim() }.filter { it.isNotBlank() }
        if (ingredients.isEmpty()) { _uiState.update { it.copy(errorMessage = "Add at least one ingredient") }; return }
        val instructions = state.instructions.map { it.trim() }.filter { it.isNotBlank() }
        if (instructions.isEmpty()) { _uiState.update { it.copy(errorMessage = "Add at least one instruction step") }; return }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                val recipe = Recipe(
                    id              = Random.nextInt(100_000, Int.MAX_VALUE),
                    title           = state.title.trim(),
                    imageUrl        = state.imageUri,
                    cookTimeMinutes = cookTime,
                    servings        = servings,
                    likes           = 0,
                    difficulty      = state.difficulty,
                    category        = state.category.trim().ifBlank { "general" },
                    ingredients     = ingredients,
                    instructions    = instructions,
                    isFavorite      = false,
                    calories        = state.calories.toIntOrNull() ?: 0,
                )
                addRecipeUseCase(recipe)
                _uiState.update { it.copy(isSaving = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, errorMessage = e.message ?: "Failed to save recipe") }
            }
        }
    }
}
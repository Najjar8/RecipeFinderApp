package com.recipefinder.app.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipefinder.app.core.util.Resource
import com.recipefinder.app.domain.model.Recipe
import com.recipefinder.app.domain.usecase.GetRecipeDetailUseCase
import com.recipefinder.app.domain.usecase.ToggleFavoriteUseCase
import com.recipefinder.app.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    savedStateHandle:        SavedStateHandle,
    private val getRecipeDetailUseCase: GetRecipeDetailUseCase,
    private val toggleFavoriteUseCase:  ToggleFavoriteUseCase,
) : ViewModel() {

    private val recipeId: Int = checkNotNull(
        savedStateHandle[Screen.RecipeDetail.ARG_RECIPE_ID]
    )

    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    init {
        loadRecipe()
    }

    fun loadRecipe() {
        getRecipeDetailUseCase(recipeId)
            .onEach { result ->
                when (result) {
                    is Resource.Loading -> _uiState.update {
                        it.copy(isLoading = true, errorMessage = null)
                    }
                    is Resource.Success -> _uiState.update {
                        it.copy(isLoading = false, recipe = result.data)
                    }
                    is Resource.Error   -> _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun onToggleFavorite() {
        val recipe = _uiState.value.recipe ?: return
        viewModelScope.launch {
            toggleFavoriteUseCase(recipe)
            _uiState.update { state ->
                state.copy(
                    recipe = state.recipe?.copy(isFavorite = !recipe.isFavorite)
                )
            }
        }
    }
}

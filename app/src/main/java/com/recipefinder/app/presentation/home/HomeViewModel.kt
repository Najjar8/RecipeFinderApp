package com.recipefinder.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipefinder.app.core.constants.AppConstants
import com.recipefinder.app.core.util.Resource
import com.recipefinder.app.domain.model.Recipe
import com.recipefinder.app.domain.model.RecipeFilter
import com.recipefinder.app.domain.usecase.FilterRecipesUseCase
import com.recipefinder.app.domain.usecase.GetRecipesUseCase
import com.recipefinder.app.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.recipefinder.app.domain.usecase.DeleteRecipeUseCase
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRecipesUseCase:    GetRecipesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val filterRecipesUseCase: FilterRecipesUseCase,
    private val deleteRecipeUseCase:  DeleteRecipeUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadRecipes()
        observeSearchQuery()
    }

    // ─── Load ─────────────────────────────────────────────────────────────────

    fun loadRecipes() {
        getRecipesUseCase()
            .onEach { result ->
                when (result) {
                    is Resource.Loading -> _uiState.update {
                        it.copy(isLoading = true, errorMessage = null)
                    }
                    is Resource.Success -> {
                        val filtered = applyFilter(result.data, _uiState.value.filter)
                        _uiState.update {
                            it.copy(
                                isLoading  = false,
                                allRecipes = result.data,
                                recipes    = filtered,
                                errorMessage = null,
                            )
                        }
                    }
                    is Resource.Error   -> _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    // ─── Search ───────────────────────────────────────────────────────────────

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        _uiState
            .map { it.searchQuery }
            .distinctUntilChanged()
            .debounce(AppConstants.DEBOUNCE_MILLIS)
            .onEach { query -> reapplyFilters(query) }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    // ─── Filter ───────────────────────────────────────────────────────────────

    fun onFilterChange(filter: RecipeFilter) {
        _uiState.update { state ->
            val filtered = applyFilter(state.allRecipes, filter, state.searchQuery)
            state.copy(filter = filter, recipes = filtered)
        }
    }

    private fun reapplyFilters(query: String) {
        _uiState.update { state ->
            val filtered = applyFilter(state.allRecipes, state.filter, query)
            state.copy(recipes = filtered)
        }
    }

    private fun applyFilter(
        all: List<Recipe>,
        filter: RecipeFilter,
        query: String = _uiState.value.searchQuery,
    ): List<Recipe> {
        val searched = if (query.isBlank()) all
        else all.filter { recipe ->
            recipe.title.contains(query, ignoreCase = true) ||
            recipe.ingredients.any { it.contains(query, ignoreCase = true) } ||
            recipe.category.contains(query, ignoreCase = true)
        }
        return filterRecipesUseCase(searched, filter)
    }

    // ─── Favourite toggle ─────────────────────────────────────────────────────

    fun onToggleFavorite(recipe: Recipe) {
        viewModelScope.launch {
            toggleFavoriteUseCase(recipe)
            // Optimistically update UI without waiting for DB round-trip
            _uiState.update { state ->
                val updated = state.allRecipes.map {
                    if (it.id == recipe.id) it.copy(isFavorite = !it.isFavorite) else it
                }
                state.copy(
                    allRecipes = updated,
                    recipes    = applyFilter(updated, state.filter),
                )
            }
        }
    }

    // ─── Error dismiss ────────────────────────────────────────────────────────

    fun onDeleteRequest(recipe: Recipe) {
        _uiState.update { it.copy(recipeToDelete = recipe) }
    }

    fun onDeleteConfirmed() {
        val recipe = _uiState.value.recipeToDelete ?: return
        viewModelScope.launch {
            deleteRecipeUseCase(recipe.id)
            _uiState.update { state ->
                val updated = state.allRecipes.filter { it.id != recipe.id }
                state.copy(
                    allRecipes     = updated,
                    recipes        = applyFilter(updated, state.filter),
                    recipeToDelete = null,
                )
            }
        }
    }

    fun onDeleteCancelled() {
        _uiState.update { it.copy(recipeToDelete = null) }
    }

    fun onErrorDismissed() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

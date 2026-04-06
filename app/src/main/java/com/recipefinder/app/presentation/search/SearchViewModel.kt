package com.recipefinder.app.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipefinder.app.core.constants.AppConstants
import com.recipefinder.app.core.util.Resource
import com.recipefinder.app.domain.model.Recipe
import com.recipefinder.app.domain.usecase.SearchRecipesUseCase
import com.recipefinder.app.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRecipesUseCase:  SearchRecipesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    // In-memory search history (max 8 items)
    private val searchHistory = ArrayDeque<String>(8)

    private var searchJob: Job? = null

    init {
        observeQueryDebounced()
    }

    // ─── Query change ─────────────────────────────────────────────────────────

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query, hasSearched = query.isNotBlank()) }
        if (query.isBlank()) {
            _uiState.update {
                it.copy(results = emptyList(), isLoading = false, hasSearched = false)
            }
        }
    }

    fun onSearchSubmit() {
        val query = _uiState.value.query.trim()
        if (query.isBlank()) return
        saveToHistory(query)
        executeSearch(query)
    }

    fun onSuggestionClick(suggestion: String) {
        _uiState.update { it.copy(query = suggestion, hasSearched = true) }
        executeSearch(suggestion)
    }

    fun onClearQuery() {
        _uiState.update {
            it.copy(
                query       = "",
                results     = emptyList(),
                isLoading   = false,
                hasSearched = false,
                errorMessage = null,
            )
        }
    }

    // ─── Debounced auto-search ────────────────────────────────────────────────

    @OptIn(FlowPreview::class)
    private fun observeQueryDebounced() {
        _uiState
            .map { it.query }
            .distinctUntilChanged()
            .debounce(AppConstants.DEBOUNCE_MILLIS)
            .filter { it.length >= 2 }
            .onEach { query -> executeSearch(query) }
            .launchIn(viewModelScope)
    }

    private fun executeSearch(query: String) {
        searchJob?.cancel()
        searchJob = searchRecipesUseCase(query)
            .onEach { result ->
                when (result) {
                    is Resource.Loading -> _uiState.update {
                        it.copy(isLoading = true, errorMessage = null)
                    }
                    is Resource.Success -> _uiState.update {
                        it.copy(isLoading = false, results = result.data)
                    }
                    is Resource.Error   -> _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    // ─── Favorite toggle ──────────────────────────────────────────────────────

    fun onToggleFavorite(recipe: Recipe) {
        viewModelScope.launch {
            toggleFavoriteUseCase(recipe)
            _uiState.update { state ->
                state.copy(
                    results = state.results.map {
                        if (it.id == recipe.id) it.copy(isFavorite = !it.isFavorite) else it
                    }
                )
            }
        }
    }

    // ─── History helpers ──────────────────────────────────────────────────────

    private fun saveToHistory(query: String) {
        searchHistory.remove(query)          // remove duplicate if present
        searchHistory.addFirst(query)
        if (searchHistory.size > 8) searchHistory.removeLast()
        _uiState.update { it.copy(suggestions = searchHistory.toList()) }
    }
}

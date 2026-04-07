package com.recipefinder.app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipefinder.app.domain.usecase.GetFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getFavoritesUseCase: GetFavoritesUseCase,
) : ViewModel() {

    /** Total number of favorited recipes, used as the nav-bar badge count. */
    val favoriteBadgeCount: StateFlow<Int> = getFavoritesUseCase()
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
}

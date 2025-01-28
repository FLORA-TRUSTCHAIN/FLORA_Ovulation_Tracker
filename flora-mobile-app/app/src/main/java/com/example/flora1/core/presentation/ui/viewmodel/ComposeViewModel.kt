package com.example.flora1.core.presentation.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel

@Stable
abstract class ComposeViewModel<UiState, UiEvent> : ViewModel() {

    @Composable
    abstract fun uiState() : UiState

    abstract fun onEvent(event : UiEvent)
}

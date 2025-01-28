package com.example.flora1.features.onboarding.height

import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flora1.domain.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HeightViewModel @Inject constructor(
    private val preferences: Preferences,
) : ViewModel() {

    private var _height = MutableStateFlow(TextFieldValue("150"))
    val height: StateFlow<TextFieldValue> = _height
        .filter {height ->
            height.text.isDigitsOnly()
                    && !height.text.startsWith('0')
                    && height.text.length < 4
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            _height.value,
        )

    val enabled = height.map { it.text.isNotBlank() && it.text.length > 1}
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            false,
        )

    fun onHeightChanged(height: TextFieldValue) {
            _height.value = height
    }

    fun onSaveHeight(height: Float) {
        preferences.saveHeight(height)
    }

    companion object {
        const val MAX_USERNAME_CHARS = 15
    }
}


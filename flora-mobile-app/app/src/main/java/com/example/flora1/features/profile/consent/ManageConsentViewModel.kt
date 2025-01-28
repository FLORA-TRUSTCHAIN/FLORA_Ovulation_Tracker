package com.example.flora1.features.profile.consent

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.flora1.domain.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ManageConsentViewModel @Inject constructor(
    private val preferences: Preferences,
) : ViewModel() {

    var isDataConsentGiven by mutableStateOf(preferences.hasGivenDataConsent)
        private set

    fun onToggleDataConsent(newValue: Boolean) {
        preferences.saveHasGivenDataConsent(hasGivenDataConsent = newValue)
        isDataConsentGiven = newValue
    }
}

package com.example.flora1.features.onboarding.born

import androidx.lifecycle.ViewModel
import com.example.flora1.domain.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BornScreenViewModel @Inject constructor(
    private val preferences: Preferences,
) : ViewModel() {

    fun onSaveDateOfBirth(dateOfBirth: Long) {
        preferences.saveDateOfBirth(dateOfBirth)
    }

    companion object {
        const val MIN_ELIGIBLE_AGE_TO_USE_FLORA = 13
    }
}


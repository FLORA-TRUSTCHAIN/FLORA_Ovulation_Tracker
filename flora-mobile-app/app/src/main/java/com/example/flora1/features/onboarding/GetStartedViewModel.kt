package com.example.flora1.features.onboarding

import androidx.lifecycle.ViewModel
import com.example.flora1.domain.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GetStartedViewModel @Inject constructor(
    private val preferences: Preferences,
) : ViewModel()

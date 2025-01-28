package com.example.flora1.features.onboarding.averagecycle

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.flora1.domain.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AverageCycleViewModel @Inject constructor(
    private val preferences: Preferences,
) : ViewModel() {

    var selectedNumber by mutableStateOf<Int?>(null)
        private set

    fun onSaveAverageCycleDays(averageCycleDays: Int) {
        preferences.saveAverageCycle(averageCycleDays)
    }

    fun onSelectedNumberChange(number : Int?){
        selectedNumber = number
    }
}


package com.example.flora1.features.onboarding.stresstilllastperiod

import androidx.lifecycle.ViewModel
import com.example.flora1.domain.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class StressTillLastPeriodViewModel @Inject constructor(
    private val preferences: Preferences,
) : ViewModel() {

    private var _selectedStressLevel : MutableStateFlow<StressLevelTillLastPeriod> =
        MutableStateFlow(StressLevelTillLastPeriod.MEDIUM)
    val selectedStressLevel : StateFlow<StressLevelTillLastPeriod> = _selectedStressLevel

    fun onSelectedStressLevelChanged(stressLevel :StressLevelTillLastPeriod) {
        _selectedStressLevel.value = stressLevel
    }

    fun onSaveStressLevel(stressLevel: StressLevelTillLastPeriod){
        preferences.saveStressLevelTillLastPeriod(stressLevel)
    }

}

enum class StressLevelTillLastPeriod(val text: String) {
    LOW("Low"),
    LOW_MEDIUM("Low Medium"),
    MEDIUM("Medium"),
    HIGH_MEDIUM("High Medium"),
    HIGH("High"),
    DONT_REMEMBER("Don't remember");

    companion object {
        fun fromString(text: String): StressLevelTillLastPeriod {
            return when (text) {
                LOW.text -> LOW
                LOW_MEDIUM.text -> LOW_MEDIUM
                MEDIUM.text -> MEDIUM
                HIGH_MEDIUM.text -> HIGH_MEDIUM
                HIGH.text -> HIGH
                DONT_REMEMBER.text -> DONT_REMEMBER
                else -> DONT_REMEMBER // Default to "Don't remember" if no match
            }
        }
    }
}


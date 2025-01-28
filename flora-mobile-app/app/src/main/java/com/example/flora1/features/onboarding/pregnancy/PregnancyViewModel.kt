package com.example.flora1.features.onboarding.weight

import androidx.lifecycle.ViewModel
import com.example.flora1.domain.Preferences
import com.example.flora1.features.onboarding.weight.PregnancyViewModel.Companion.DEFAULT_NOT_PREGNANT_VALUE
import com.example.flora1.features.onboarding.weight.PregnancyViewModel.Companion.DEFAULT_NO_COMMENT_VALUE
import com.example.flora1.features.onboarding.weight.PregnancyViewModel.Companion.DEFAULT_PREGNANT_VALUE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PregnancyViewModel @Inject constructor(
    private val preferences: Preferences,
) : ViewModel() {

    private var _pregnancyStatus = MutableStateFlow(PregnancyStatus.NOT_PREGNANT)
    val pregnancyStatus: StateFlow<PregnancyStatus> = _pregnancyStatus

    fun onPregnancyStatusChanged(pregnancyStatus: PregnancyStatus) {
        _pregnancyStatus.value = pregnancyStatus
    }

    fun onSavePregnancyStatus(pregnancyStatus: PregnancyStatus) {
        preferences.savePregnancyStatus(pregnancyStatus)
    }

    companion object {
        const val MAX_WEIGHT_CHARS = 5
        const val DEFAULT_PREGNANT_VALUE = "Yes"
        const val DEFAULT_NOT_PREGNANT_VALUE = "No"
        const val DEFAULT_NO_COMMENT_VALUE = "No comment"
    }
}

enum class PregnancyStatus(val value: String) {
    PREGNANT(DEFAULT_PREGNANT_VALUE),
    NOT_PREGNANT(DEFAULT_NOT_PREGNANT_VALUE),
    NO_COMMENT(DEFAULT_NO_COMMENT_VALUE);

    companion object {
        fun fromString(value: String): PregnancyStatus =
            when (value) {
                DEFAULT_PREGNANT_VALUE -> PREGNANT
                DEFAULT_NOT_PREGNANT_VALUE -> NOT_PREGNANT
                DEFAULT_NO_COMMENT_VALUE -> NO_COMMENT
                else -> NO_COMMENT
            }
    }

}


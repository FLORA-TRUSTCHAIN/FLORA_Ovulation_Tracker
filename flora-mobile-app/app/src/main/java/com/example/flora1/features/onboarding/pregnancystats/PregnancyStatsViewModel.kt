package com.example.flora1.features.onboarding.weight

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.flora1.core.presentation.ui.viewmodel.ComposeViewModel
import com.example.flora1.domain.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class PregnancyStatsViewState(
    val pregnancies: NumericalOptions,
    val miscarriages: NumericalOptions,
    val abortions: NumericalOptions,
    val isBreastfeeding: Boolean,
)

sealed interface PregnancyStatsViewEvent {

    data class OnPregnanciesClicked(val value: NumericalOptions) : PregnancyStatsViewEvent
    data class OnMiscarriagesClicked(val value: NumericalOptions) : PregnancyStatsViewEvent
    data class OnAbortionsClicked(val value: NumericalOptions) : PregnancyStatsViewEvent
    data class OnIsBreastfeedingClicked(val value: Boolean) : PregnancyStatsViewEvent
    data object OnNextClicked : PregnancyStatsViewEvent
}

@HiltViewModel
class PregnancyStatsViewModel @Inject constructor(
    private val preferences: Preferences,
) : ComposeViewModel<PregnancyStatsViewState, PregnancyStatsViewEvent>() {
    private var pregnancies by mutableStateOf(NumericalOptions.ZERO)
    private var miscarriages by mutableStateOf(NumericalOptions.ZERO)
    private var abortions by mutableStateOf(NumericalOptions.ZERO)
    private var isBreastfeeding by mutableStateOf(false)

    @Composable
    override fun uiState(): PregnancyStatsViewState = PregnancyStatsViewState(
        pregnancies = pregnancies,
        miscarriages = miscarriages,
        abortions = abortions,
        isBreastfeeding = isBreastfeeding,
    )


    override fun onEvent(event: PregnancyStatsViewEvent) {
        when (event) {
            is PregnancyStatsViewEvent.OnPregnanciesClicked -> {
                pregnancies = event.value
            }

            is PregnancyStatsViewEvent.OnMiscarriagesClicked -> {
                miscarriages = event.value
            }

            is PregnancyStatsViewEvent.OnAbortionsClicked -> {
                abortions = event.value
            }

            is PregnancyStatsViewEvent.OnIsBreastfeedingClicked -> {
                isBreastfeeding = event.value
            }

            is PregnancyStatsViewEvent.OnNextClicked -> {
                handleNext()
            }

            else -> throw IllegalArgumentException("Don't know $event")
        }
    }


    private fun handleNext() {
        preferences.saveTotalPregnancies(pregnancies)
        preferences.saveTotalMiscarriages(miscarriages)
        preferences.saveTotalAbortions(abortions)
        preferences.saveIsBreastfeeding(isBreastfeeding)
    }

    companion object {
        const val MAX_WEIGHT_CHARS = 5
        const val DEFAULT_PREGNANT_VALUE = "Yes"
        const val DEFAULT_NOT_PREGNANT_VALUE = "No"
        const val DEFAULT_NO_COMMENT_VALUE = "No comment"
    }
}

enum class NumericalOptions(val text: String) {
    ZERO("0"),
    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    MORE_THAN_SIX("6+");

    companion object {
        fun fromString(text: String) =
            when (text) {
                "0" -> ZERO
                "1" -> ZERO
                "2" -> ZERO
                "3" -> ZERO
                "4" -> ZERO
                "5" -> ZERO
                "6+" -> ZERO
                else -> null
            }
    }
}


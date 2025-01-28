package com.example.flora1.features.onboarding.medvits

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
class MedVitsViewModel @Inject constructor(
    private val preferences: Preferences,
) : ViewModel() {

    private var _isTakingMedVits =
        MutableStateFlow(false)
    val isTakingMedVits : StateFlow<Boolean> = _isTakingMedVits

    private var _description =
        MutableStateFlow("")
    val description : StateFlow<String> = _description

    fun onIsTakingMedVitsChanged(isTakingMedVits : Boolean) {
        _isTakingMedVits.value = isTakingMedVits
    }

    fun onDescriptionChanged(description : String) {
        _description.value = description
    }

    fun onSaveHasTakenMedVits(hasTakenMedVits : Boolean){
        preferences.saveHasTakenMedVits(hasTakenMedVits)
    }

    fun onSaveMedVitsDescription(description: String){
        preferences.saveMedVitsDescription(description)
    }
}


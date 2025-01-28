package com.example.flora1.features.onboarding.gynosurgery

import androidx.lifecycle.ViewModel
import com.example.flora1.domain.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class GynosurgeryViewModel @Inject constructor(
    private val preferences: Preferences,
) : ViewModel() {

    private var _hasDoneGynosurgery =
        MutableStateFlow(false)
    val hasDoneGynosurgery : StateFlow<Boolean> = _hasDoneGynosurgery

    private var _description =
        MutableStateFlow("")
    val description : StateFlow<String> = _description

    fun onIsTakingMedVitsChanged(hasDoneGynosurgery : Boolean) {
        _hasDoneGynosurgery.value = hasDoneGynosurgery
    }

    fun onDescriptionChanged(description : String) {
        _description.value = description
    }

    fun onSaveHasDoneGynecosurgery(hasDoneGynosurgery: Boolean){
        preferences.saveHasDoneGynecosurgery(hasDoneGynosurgery)
    }

    fun onSaveGyncosurgeryDescription(description: String){
        preferences.saveGyncosurgeryDescription(description)
    }
}


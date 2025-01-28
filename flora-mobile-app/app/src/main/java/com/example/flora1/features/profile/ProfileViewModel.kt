package com.example.flora1.features.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flora1.domain.Preferences
import com.example.flora1.features.profile.consent.ProfileEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val preferences: Preferences,
) : ViewModel() {

    var state by mutableStateOf(
        ProfileState(
            isLoggedIn = preferences.isLoggedIn,

            )
    )

    private val _events = Channel<ProfileEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: ProfileAction) {

        viewModelScope.launch {
            when (action) {
                ProfileAction.OnBackClicked -> {
                    _events.send(ProfileEvent.NavigateBack)
                }

                ProfileAction.OnManageConsentClicked -> {
                    _events.send(ProfileEvent.NavigateToManageConsent)
                }

                ProfileAction.OnMyDoctorsClicked -> {
                    if (preferences.hasGivenDataConsent)
                        _events.send(ProfileEvent.NavigateToMyDoctors)
                    else
                        _events.send(ProfileEvent.ShowMessage("You have to give consent in Manage Data Consent section."))
                }
            }
        }


    }

}

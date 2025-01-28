package com.example.flora1.features.profile.consent

sealed interface ProfileEvent {
    data object NavigateBack : ProfileEvent
    data object NavigateToManageConsent : ProfileEvent
    data object NavigateToMyDoctors : ProfileEvent
    data class ShowMessage(val message: String) : ProfileEvent
}

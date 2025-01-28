package com.example.flora1.features.profile

sealed interface ProfileAction {
    data object OnManageConsentClicked : ProfileAction
    data object OnBackClicked : ProfileAction
    data object OnMyDoctorsClicked : ProfileAction

}

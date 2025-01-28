package com.example.flora1.features.profile

data class ProfileState(
    val username: String? = null,
    val displayMode: DisplayMode = DisplayMode.SYSTEM,
    val isDidRoomEnabled: Boolean = false,
    val isLoggedIn: Boolean = false,
)

enum class DisplayMode {
    DARK, LIGHT, SYSTEM,
}


package com.example.flora1.features.onboarding.usernameage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flora1.domain.Preferences
import com.example.flora1.domain.auth.LoginUseCase
import com.example.flora1.domain.auth.RegisterUseCase
import com.example.flora1.domain.util.DataError
import com.example.flora1.domain.util.Result
import com.example.flora1.domain.util.getOrNull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsernameAgeViewModel @Inject constructor(
    private val preferences: Preferences,
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase,
) : ViewModel() {

    private var _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private val _events = Channel<UsernameAgeEvent>()
    val events = _events.receiveAsFlow()

    fun onRegister(
        username: String,
        email: String,
        password: String,
        isConsentGranted: Boolean,
    ) {
        viewModelScope.launch {
            _isRunning.update { true }
            val result = registerUseCase.register(
                username = username,
                email = email,
                password = password
            )

            when (result) {
                is Result.Error -> {
                    _isRunning.update { false }
                    _events.send(
                        UsernameAgeEvent.RegistrationFailed(
                            message =
                            if (result.error == DataError.Network.BAD_REQUEST)
                                "User is already taken"
                            else
                                "Registration failed"
                        )
                    )
                }

                is Result.Success -> {
                    loginUseCase.login(username, password).getOrNull()?.let { token ->
                        preferences.saveToken(token = token)
                    }
                    _isRunning.update { false }
                    preferences.saveHasGivenDataConsent(isConsentGranted)
                    _events.send(UsernameAgeEvent.RegistrationSuccessful)
                }
            }
        }
    }

    fun onLogin(
        username: String,
        password: String,
    ) {
        viewModelScope.launch {
            _isRunning.update { true }
            val result = loginUseCase.login(
                username = username,
                password = password,
            )
            _isRunning.update { false }

            when (result) {
                is Result.Error -> _events.send(
                    UsernameAgeEvent.LoginFailed(
                        message =
                        when (result.error) {
                            DataError.Network.BAD_REQUEST -> "User is already taken"
                            DataError.Network.UNAUTHORIZED -> "Wrong credentials"
                            else -> "Login failed"
                        }
                    )
                )

                is Result.Success -> {
                    preferences.saveToken(token = result.data)
                    _events.send(UsernameAgeEvent.LoginSuccessful)
                }
            }
        }
    }

    companion object {
        const val MAX_USERNAME_CHARS = 15
    }
}

sealed interface UsernameAgeEvent {
    data object LoginSuccessful : UsernameAgeEvent
    data object RegistrationSuccessful : UsernameAgeEvent
    data class RegistrationFailed(val message: String) : UsernameAgeEvent
    data class LoginFailed(val message: String) : UsernameAgeEvent
}


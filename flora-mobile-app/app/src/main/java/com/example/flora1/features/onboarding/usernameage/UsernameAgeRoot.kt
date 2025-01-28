package com.example.flora1.features.onboarding.usernameage

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flora1.R
import com.example.flora1.core.presentation.ui.components.LoginRoot
import com.example.flora1.core.presentation.ui.observers.ObserveAsEvents
import com.example.flora1.core.presentation.ui.toast.showSingleToast
import com.example.flora1.features.onboarding.OnBoardingScreen
import com.example.flora1.features.onboarding.components.OnBoardingScaffold

@Composable
fun UsernameAgeRoot(
    onNext: () -> Unit,
    viewModel: UsernameAgeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val isRunning by viewModel.isRunning.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = viewModel.events) { event ->
        when (event) {
            UsernameAgeEvent.RegistrationSuccessful -> {
                context.showSingleToast(context.getString(R.string.registration_was_successful))
                onNext()
            }

            is UsernameAgeEvent.RegistrationFailed -> context.showSingleToast(event.message)
            is UsernameAgeEvent.LoginFailed -> context.showSingleToast(event.message)
            UsernameAgeEvent.LoginSuccessful -> {
                context.showSingleToast(context.getString(R.string.login_was_successful))
                onNext()
            }
        }
    }

    UsernameAgeRoot(
        onNext = onNext,
        onRegisterClicked = { username, email, password, isConsentGranted ->
            viewModel.onRegister(
                username = username,
                email = email,
                password = password,
                isConsentGranted = isConsentGranted,
            )
        },
        onLoginClicked = { username, password ->
            viewModel.onLogin(
                username = username,
                password = password,
            )
        },
        isRunning = isRunning,
    )
}

@Composable
fun UsernameAgeRoot(
    onNext: () -> Unit = {},
    onRegisterClicked: (String, String, String, Boolean) -> Unit,
    onLoginClicked: (String, String) -> Unit,
    isRunning: Boolean,
) {
    OnBoardingScaffold(
        title = stringResource(R.string.welcome_and_login_or_register_screen_title),
        shouldShowNext = false,
        isNextEnabled = false,
        selectedScreen = OnBoardingScreen.USERNAME_AGE,
    ) {
        LoginRoot(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            onRegisterClicked = onRegisterClicked,
            onLoginClicked = onLoginClicked,
            onContinueAsAnonymous = onNext,
            isRunning = isRunning,
        )
    }
}


@PreviewLightDark
@Composable
private fun Preview1() {

}


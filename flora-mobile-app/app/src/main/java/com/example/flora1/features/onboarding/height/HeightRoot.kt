package com.example.flora1.features.onboarding.usernameage

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flora1.core.presentation.ui.uikit.textfields.UnitTextField
import com.example.flora1.features.onboarding.OnBoardingScreen
import com.example.flora1.features.onboarding.components.OnBoardingScaffold
import com.example.flora1.features.onboarding.height.HeightViewModel

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun HeightRoot(
    onNext: () -> Unit,
    viewModel: HeightViewModel = hiltViewModel(),
) {
    val height by viewModel.height.collectAsStateWithLifecycle()
    val enabled by viewModel.enabled.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
        viewModel.onHeightChanged(
            height = height.copy(
                selection = TextRange(height.text.length) // Set cursor to the end
            )
        )
    }

    OnBoardingScaffold(
        title = "What is your height?",
        isNextEnabled = enabled,
        selectedScreen = OnBoardingScreen.HEIGHT,
        onNextClick = {
            viewModel.onSaveHeight(height.text.toFloat())
            onNext()
        },
    ) {
        UnitTextField(
            modifier = Modifier.focusRequester(focusRequester),
            value = height,
            onValueChange = {
                viewModel.onHeightChanged(it)
            },
            unit = "cm"
        )
    }

}

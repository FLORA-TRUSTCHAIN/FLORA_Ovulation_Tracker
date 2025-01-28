package com.example.flora1.features.onboarding.pregnancy

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flora1.core.presentation.ui.uikit.buttons.MultipleOptionsButton
import com.example.flora1.features.onboarding.OnBoardingScreen
import com.example.flora1.features.onboarding.components.OnBoardingScaffold
import com.example.flora1.features.onboarding.weight.PregnancyStatus
import com.example.flora1.features.onboarding.weight.PregnancyViewModel

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun PregnancyRoot(
    onNext: (hasBeenPregnant: Boolean) -> Unit,
    viewModel: PregnancyViewModel = hiltViewModel(),
) {
    val selectedPregnancyStatus by viewModel.pregnancyStatus.collectAsStateWithLifecycle()
    OnBoardingScaffold(
        title = "Have you ever been pregnant (or are you right now) ?",
        onNextClick = {
            viewModel.onSavePregnancyStatus(selectedPregnancyStatus)

            onNext(viewModel.pregnancyStatus.value == PregnancyStatus.PREGNANT)
        },
        selectedScreen = OnBoardingScreen.PREGNANCY,
    ) {
        MultipleOptionsButton(
            selectedOption = selectedPregnancyStatus,
            options = PregnancyStatus.entries,
            onSelectedOption = { pregnancyStatus: PregnancyStatus ->
                viewModel.onPregnancyStatusChanged(pregnancyStatus)
            },
            text = PregnancyStatus::value,
        )
    }
}

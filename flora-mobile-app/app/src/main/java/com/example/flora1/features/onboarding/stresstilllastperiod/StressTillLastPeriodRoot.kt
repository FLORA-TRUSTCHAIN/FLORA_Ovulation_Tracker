package com.example.flora1.features.onboarding.stresstilllastperiod

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flora1.R
import com.example.flora1.core.presentation.ui.uikit.radio.RadioGroup
import com.example.flora1.features.onboarding.OnBoardingScreen
import com.example.flora1.features.onboarding.components.OnBoardingScaffold

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun StressTillLastPeriodRoot(
    onNext: () -> Unit,
    viewModel: StressTillLastPeriodViewModel = hiltViewModel(),
) {
    val selectedStressLevel by viewModel.selectedStressLevel.collectAsStateWithLifecycle()
    OnBoardingScaffold(
        modifier = Modifier.verticalScroll(state = rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        selectedScreen = OnBoardingScreen.SLEEP_QUALITY_TILL_LAST_PERIOD,
        title = "What were your stress levels until your last or current period?",
        onNextClick = {
            viewModel.onSaveStressLevel(selectedStressLevel)
            onNext()
        },
    ) {
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.onboarding_space_between_title_and_radio_groupds)))
        RadioGroup(
            radioButtons = StressLevelTillLastPeriod.entries.toTypedArray(),
            selectedRadioButton = selectedStressLevel,
            radioButtonLabel = {
                it.text
            },
            onRadioButtonSelected = { viewModel.onSelectedStressLevelChanged(it) }
        )
    }
}

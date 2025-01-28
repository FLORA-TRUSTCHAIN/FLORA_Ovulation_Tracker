package com.example.flora1.features.onboarding.race

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flora1.R
import com.example.flora1.core.presentation.ui.uikit.dropdown.DropdownWithBorderWithInlineLabel
import com.example.flora1.features.onboarding.OnBoardingScreen
import com.example.flora1.features.onboarding.components.OnBoardingScaffold

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun RaceRoot(
    onNext: () -> Unit,
    viewModel: RaceViewModel = hiltViewModel(),
) {
    val selectedRace by viewModel.selectedRace.collectAsStateWithLifecycle()

    OnBoardingScaffold(
        verticalArrangement = Arrangement.Center,
        title = "What is your race?",
        selectedScreen = OnBoardingScreen.RACE,
        onNextClick = {
            viewModel.onSaveRace(selectedRace)
            onNext()
        },
    ) {
        DropdownWithBorderWithInlineLabel(
            selectedItem = selectedRace,
            itemText = { it.text },
            items = Race.entries.toTypedArray(),
            onItemSelected = {
                viewModel.onSelectedRaceChanged(it)
            },
            label = "Race",
            testTag = R.string.dropdown_race_test_tag,
        )
    }
}

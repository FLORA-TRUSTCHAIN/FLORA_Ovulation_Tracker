package com.example.flora1.features.onboarding.pregnancystats

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flora1.R
import com.example.flora1.core.presentation.ui.uikit.buttons.MultipleOptionsButton
import com.example.flora1.core.presentation.ui.uikit.dropdown.DropdownWithBorderWithInlineLabel
import com.example.flora1.features.onboarding.OnBoardingScreen
import com.example.flora1.features.onboarding.components.OnBoardingScaffold
import com.example.flora1.features.onboarding.weight.NumericalOptions
import com.example.flora1.features.onboarding.weight.PregnancyStatsViewEvent
import com.example.flora1.features.onboarding.weight.PregnancyStatsViewModel

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun PregnancyStatsRoot(
    onNext: () -> Unit,
    onBack: () -> Unit,
    viewModel: PregnancyStatsViewModel = hiltViewModel(),
) {

    val booleanChoices = listOf(true, false)
    val uiState = viewModel.uiState()

    OnBoardingScaffold(
        verticalArrangement = Arrangement.Center,
        selectedScreen = OnBoardingScreen.PREGNANCY_STATS,
        title = "Tell us some things about your pregnancy background",
        description = "This will help us better understand you and modify Flora according to your needs",
        onNextClick = {
            viewModel.onEvent(PregnancyStatsViewEvent.OnNextClicked)
            onNext()
        },
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(30.dp),
        ) {
            Column {
                Text(
                    text = "Are you currently breastfeeding?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )
                MultipleOptionsButton(
                    selectedOption = uiState.isBreastfeeding,
                    options = booleanChoices,
                    onSelectedOption = { isBreastfeeding: Boolean ->
                        viewModel.onEvent(
                            PregnancyStatsViewEvent.OnIsBreastfeedingClicked(
                                isBreastfeeding
                            )
                        )
                    },
                    text = {
                        if (this) "Yes" else "No"
                    },
                )
            }

            DropdownWithBorderWithInlineLabel(
                selectedItem = uiState.pregnancies,
                itemText = { it.text },
                items = NumericalOptions.entries.toTypedArray(),
                onItemSelected = {
                    viewModel.onEvent(PregnancyStatsViewEvent.OnPregnanciesClicked(it))
                },
                label = "Pregnancies",
                testTag = R.string.dropdown_pregnancy_test_tag,
            )

            DropdownWithBorderWithInlineLabel(
                selectedItem = uiState.miscarriages,
                itemText = { it.text },
                items = NumericalOptions.entries.toTypedArray(),
                onItemSelected = {
                    viewModel.onEvent(PregnancyStatsViewEvent.OnMiscarriagesClicked(it))
                },
                label = "Miscarriages",
                testTag = R.string.dropdown_miscarriages_test_tag,
            )

            DropdownWithBorderWithInlineLabel(
                selectedItem = uiState.abortions,
                itemText = { it.text },
                items = NumericalOptions.entries.toTypedArray(),
                onItemSelected = {
                    viewModel.onEvent(PregnancyStatsViewEvent.OnAbortionsClicked(it))
                },
                label = "Abortions",
                testTag = R.string.dropdown_abortions_test_tag,
            )
        }
    }
}



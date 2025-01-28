@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.flora1.features.onboarding.born

import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flora1.R
import com.example.flora1.core.presentation.designsystem.Flora1Theme
import com.example.flora1.core.presentation.ui.uikit.datepickers.BornDatePicker
import com.example.flora1.core.presentation.ui.uikit.datepickers.rememberFloraDatePickerState
import com.example.flora1.features.onboarding.OnBoardingScreen
import com.example.flora1.features.onboarding.born.BornScreenViewModel.Companion.MIN_ELIGIBLE_AGE_TO_USE_FLORA
import com.example.flora1.features.onboarding.components.OnBoardingScaffold

@Composable
fun BornRoot(
    onNext: (isEligible: Boolean) -> Unit,
    viewModel: BornScreenViewModel = hiltViewModel(),
) {
    val datePickerState = rememberFloraDatePickerState()

    BornRoot(
        datePickerState = datePickerState,
        onNextClick = {
            if (isCurrentDateLessThanYears(
                    dateSelected = datePickerState.selectedDateMillis!!,
                    years = MIN_ELIGIBLE_AGE_TO_USE_FLORA
                )
            )
                onNext(false)
            else {
                viewModel.onSaveDateOfBirth(datePickerState.selectedDateMillis!!)
                onNext(true)
            }
        },
    )
}

@Composable
private fun BornRoot(
    onNextClick: () -> Unit = {},
    datePickerState: DatePickerState = rememberFloraDatePickerState()
) {
    OnBoardingScaffold(
        title = stringResource(R.string.born_screen_title),
        description = stringResource(R.string.born_screen_description),
        onNextClick = onNextClick,
        isNextEnabled = if (datePickerState.selectedDateMillis == null)
            false
        else
            datePickerState.selectedDateMillis!! < System.currentTimeMillis(),
        selectedScreen = OnBoardingScreen.BORN,
    ) {
        BornRootContent(datePickerState = datePickerState)
    }
}

@Composable
private fun BornRootContent(
    datePickerState: DatePickerState = rememberFloraDatePickerState()
) {
    BornDatePicker(datePickerState = datePickerState)
}


fun isCurrentDateLessThanYears(dateSelected: Long, years: Int) =
    System.currentTimeMillis() - dateSelected <= yearsToMillis(years = years)


fun yearsToMillis(years: Int): Long {
    val daysInYear = 365.25 // Average days in a year, accounting for leap years
    val millisInDay = 24 * 60 * 60 * 1000L // Hours * Minutes * Seconds * Milliseconds
    return (years * daysInYear * millisInDay).toLong()
}

@PreviewLightDark
@Composable
fun Preview1(modifier: Modifier = Modifier) {
    Flora1Theme {
        BornRoot()
    }
}

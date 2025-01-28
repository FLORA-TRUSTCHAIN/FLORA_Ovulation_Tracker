package com.example.flora1.features.onboarding.lastperiod

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flora1.core.presentation.ui.uikit.datepickers.rememberFloraRangeDatePickerState
import com.example.flora1.features.onboarding.OnBoardingScreen
import com.example.flora1.features.onboarding.components.OnBoardingScaffold

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LastPeriodRoot(
    onNext: () -> Unit,
    viewModel: LastPeriodViewModel = hiltViewModel(),
) {

    val datePickerState = rememberFloraRangeDatePickerState()
    OnBoardingScaffold(
        verticalArrangement = Arrangement.Top,
        isNextEnabled =
        if (datePickerState.selectedStartDateMillis == null)
            false
        else
            datePickerState.selectedStartDateMillis!! < System.currentTimeMillis(),
        selectedScreen = OnBoardingScreen.LAST_PERIOD,
        title = "When did your last period start?",
        description = "We can then predict your next period.",
        onNextClick = {
            viewModel.onSavePeriodForSelectedDates(datePickerState = datePickerState)
            onNext()
        },
    ) {

        DateRangePicker(
            modifier = Modifier.fillMaxSize(),
            state = datePickerState,
            title = null,
            headline = null,
            showModeToggle = false,
            colors = DatePickerDefaults.colors(
                subheadContentColor = MaterialTheme.colorScheme.onBackground,
                navigationContentColor = MaterialTheme.colorScheme.onBackground,
            )
        )
    }

}

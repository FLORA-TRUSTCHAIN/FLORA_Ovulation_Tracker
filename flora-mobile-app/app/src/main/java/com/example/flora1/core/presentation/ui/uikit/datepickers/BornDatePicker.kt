package com.example.flora1.core.presentation.ui.uikit.datepickers

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BornDatePicker(
    modifier: Modifier = Modifier,
    datePickerState: DatePickerState = rememberDatePickerState()
) {
    DatePicker(
        modifier = modifier,
        title = null,
        state = datePickerState,
        colors = DatePickerDefaults.colors(
            headlineContentColor = MaterialTheme.colorScheme.onBackground,
            navigationContentColor = MaterialTheme.colorScheme.onBackground,
        )
    )
}


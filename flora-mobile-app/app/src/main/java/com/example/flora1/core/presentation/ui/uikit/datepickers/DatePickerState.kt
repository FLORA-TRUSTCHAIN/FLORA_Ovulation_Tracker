package com.example.flora1.core.presentation.ui.uikit.datepickers

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberFloraDatePickerState() = rememberDatePickerState(
    selectableDates = floraSelectableDates,
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberFloraRangeDatePickerState(
    initialSelectedStartDateMillis : Long? = null,
    initialSelectedEndDateMillis : Long? = null,

) = rememberDateRangePickerState(
    initialSelectedStartDateMillis = initialSelectedStartDateMillis,
    initialSelectedEndDateMillis = initialSelectedEndDateMillis,
    selectableDates = floraSelectableDates,
)

@OptIn(ExperimentalMaterial3Api::class)
private val floraSelectableDates = object : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean =
        utcTimeMillis < System.currentTimeMillis()

    override fun isSelectableYear(year: Int): Boolean =
        year > 1951 && year < LocalDate.now().year + 1
}

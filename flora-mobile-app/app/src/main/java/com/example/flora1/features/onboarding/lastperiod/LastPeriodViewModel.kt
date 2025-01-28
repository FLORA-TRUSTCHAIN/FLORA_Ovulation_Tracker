package com.example.flora1.features.onboarding.lastperiod

import android.annotation.SuppressLint
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flora1.core.presentation.ui.date.performActionBetweenTwoDates
import com.example.flora1.core.presentation.ui.date.toDate
import com.example.flora1.data.db.PeriodDatabase
import com.example.flora1.data.db.PeriodEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@HiltViewModel
class LastPeriodViewModel @Inject constructor(
    private val db: PeriodDatabase,
) : ViewModel() {

    private fun onSaveLastPeriod(
        periodEntity: PeriodEntity,
        callback: (Result<Boolean>) -> Unit = {}
    ) {
        /*
        Αυτό θα αλλάξει όταν βάλω και Repository.

        Θα βάλω και Resource sealed class αντί για Result να έχω το
        isRunning.
         */

        viewModelScope.launch {
            val result = try {
                withContext(Dispatchers.IO) {
                    db.dao().savePeriodEntry(periodEntity)
                }
                Result.success(true)
            } catch (e: Exception) {
                Result.failure(e)
            }

            callback(result)
        }
    }

    @SuppressLint("VisibleForTests")
    fun onSavePeriodForSelectedDates(
        datePickerState: DateRangePickerState,
    ) {
        val startingDate = datePickerState.selectedStartDateMillis!!.toDate()
        val endingDate = datePickerState.selectedEndDateMillis?.toDate()

        performActionBetweenTwoDates(
            startingDate = startingDate, endingDate = endingDate,
        ) { dateIterated ->
            onSaveLastPeriod(
                PeriodEntity(
                    flow = "heavy",
                    day = dateIterated.dayOfMonth,
                    month = dateIterated.monthValue,
                    year = dateIterated.year,
                )
            )
        }
    }
}


package com.example.flora1.core.presentation.ui.date

import android.content.Context
import com.example.flora1.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Long.toDate(): LocalDate = Instant.ofEpochMilli(this).atZone(
    ZoneId.systemDefault()
).toLocalDate()

fun performActionBetweenTwoDates(
    startingDateInLong: Long,
    endingDateInLong: Long? = null,
    includeLastDay: Boolean = true,
    action: (currentDate: LocalDate) -> Unit,
) {
    val startingDate = startingDateInLong.toDate()
    val endingDate = endingDateInLong?.toDate()

    com.example.flora1.core.presentation.ui.date.performActionBetweenTwoDates(
        startingDate = startingDate,
        endingDate = endingDate,
        includeLastDay = includeLastDay,
        action = action,
    )
}

fun performActionBetweenTwoDates(
    startingDate: LocalDate,
    endingDate: LocalDate? = null,
    includeLastDay: Boolean = true,
    action: (currentDate: LocalDate) -> Unit,
) {
    if (endingDate == null) {
        action(startingDate)
        return
    }

    if (!startingDate.isBefore(endingDate)) {
        throw IllegalArgumentException("Start date should not be bigger than ending date")
    }

    var currentDate = startingDate
    do {
        action(currentDate)
        currentDate = currentDate.plusDays(1)
    } while (currentDate.isBefore(
            if (includeLastDay)
                endingDate.plusDays(1)
            else
                endingDate
        )
    )
}


fun LocalDate.toFloraText(context: Context): String {
    val today = LocalDate.now()

    return when (this) {
        today -> context.getString(R.string.today)
        today.plusDays(1) -> context.getString(R.string.tomorrow)
        today.minusDays(1) -> context.getString(R.string.yesterday)
        else -> DateTimeFormatter.ofPattern("dd LLL, yyyy").withLocale(Locale.getDefault())
            .format(this)
    }
}


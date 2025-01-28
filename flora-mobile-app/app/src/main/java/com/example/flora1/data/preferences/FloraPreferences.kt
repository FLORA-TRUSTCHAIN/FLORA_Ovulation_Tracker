package com.example.flora1.data.preferences

import android.content.Context
import android.content.Context.MODE_PRIVATE

private const val FLORA_PREFERENCES = "FloraPreferences"

private const val KEY_SHOULD_SHOW_ONBOARDING = "shouldShowOnBoarding"
private val Context.floraPreferences get() = getSharedPreferences(FLORA_PREFERENCES, MODE_PRIVATE)


var Context.shouldShowOnBoarding
    get() = floraPreferences.getBoolean(
        KEY_SHOULD_SHOW_ONBOARDING,
        true
    )
    set(value) = floraPreferences.edit()
        .putBoolean(KEY_SHOULD_SHOW_ONBOARDING, value).apply()

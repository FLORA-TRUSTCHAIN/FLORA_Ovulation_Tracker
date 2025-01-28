package com.example.flora1.android_test

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry

private val context: Context
    get() = InstrumentationRegistry.getInstrumentation().targetContext

fun hasTestTag(
    id : Int,
) = androidx.compose.ui.test.hasTestTag(testTag = context.getString(id))

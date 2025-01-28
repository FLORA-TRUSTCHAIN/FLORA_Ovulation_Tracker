package com.example.flora1.core.presentation.ui.localization

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

sealed interface UiText {
    data class DynamicString(val value: String) : UiText

    class StringResource(
        @StringRes val id: Int,
        val args: Array<Any> = arrayOf()
    ) : UiText

    @Composable
    fun asString() = when (this) {
        is DynamicString -> value
        is StringResource -> LocalContext.current.getString(id, *args)
    }

    fun asString(context: Context) = when (this) {
        is DynamicString -> value
        is StringResource -> context.getString(id, *args)
    }
}

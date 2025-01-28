package com.example.flora1.core.presentation.ui.uikit.buttons

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

sealed class ButtonMode(val enabled: Boolean) {
    object Active : ButtonMode(enabled = true)
    object Inactive : ButtonMode(enabled = false)
    object InProgress : ButtonMode(enabled = false)
}

internal fun Boolean.toButtonMode(): ButtonMode =
    when (this) {
        true  -> ButtonMode.Active
        false -> ButtonMode.Inactive
    }

@Composable
internal fun ButtonMode.buttonColors(
    containerColor: Color,
    contentColor: Color,
): ButtonColors =
    when {
        enabled -> ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        )

        else    -> ButtonDefaults.buttonColors(
            disabledContainerColor = containerColor,
            disabledContentColor = contentColor,
        )
    }

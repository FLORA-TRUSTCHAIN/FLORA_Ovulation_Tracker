package com.example.flora1.core.presentation.ui.modifier

import androidx.compose.ui.Modifier


fun Modifier.applyIf(
    enabled: Boolean,
    otherModifier: Modifier.() -> Modifier
): Modifier {
    return if (enabled) {
        this.otherModifier()
    } else {
        this
    }
}

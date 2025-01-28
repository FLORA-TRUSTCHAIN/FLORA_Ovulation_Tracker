package com.example.flora1.core.presentation.ui.modifier

import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density

fun Modifier.width(width: Int?, density: Density): Modifier =
    if (width != null)
        with(density) {
            width(width.toDp())
        }
    else
        this

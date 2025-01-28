package com.example.flora1.core.presentation.ui.uikit.buttons


data class ButtonParameters(
    val text: String,
    val onClick: () -> Unit,
    val mode: ButtonMode = ButtonMode.Active,
    val testTagId: Int? = null,
)

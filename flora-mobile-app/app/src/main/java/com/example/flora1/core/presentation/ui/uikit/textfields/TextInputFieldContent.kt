package com.example.flora1.core.presentation.ui.uikit.textfields

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
internal fun TextInputFieldContent(
    modifier: Modifier = Modifier,
    inputText: String,
    placeholder: String?,
    leadingContent: @Composable (RowScope.() -> Unit)? = null,
    trailingContent: @Composable (RowScope.() -> Unit)? = null,
    innerTextField: @Composable () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingContent != null)
            leadingContent()

        Box(
            modifier = Modifier
                .weight(1f),
        ) {
            if (inputText.isEmpty() && !placeholder.isNullOrEmpty()) {
                TextFieldPlaceholder(placeholder = placeholder)
            }
            innerTextField()
        }

        if (trailingContent != null)
            trailingContent()
    }
}

@Composable
internal fun TextFieldPlaceholder(placeholder: String) {
    Text(
        text = placeholder,
        style = MaterialTheme.typography.bodyMedium,
    )
}


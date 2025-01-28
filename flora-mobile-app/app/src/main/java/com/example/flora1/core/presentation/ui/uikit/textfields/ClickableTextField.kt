package com.example.flora1.core.presentation.ui.uikit.textfields

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.flora1.R

@Composable
fun ClickableTextField(
    text: String? = null,
    placeholder: String? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit,
    leadingIcon: @Composable (RowScope.() -> Unit)? = null,
    trailingIcon: @Composable (RowScope.() -> Unit)? = null,
    @StringRes testTag: Int = R.string.clickable_text_field_test_tag,
) {
    val shape = textInputFieldBorderShape()

    TextInputFieldContent(
        modifier = Modifier
            .testTag(stringResource(id = testTag))
            .clip(shape)
            .border(
                shape = shape,
                width = textInputFieldBorderWidth(),
                color = textInputFieldBorderColor(
                    isFocused = false,
                    enabled = enabled,
                    isError = isError,
                ),
            )
            .background(textInputFieldContainerColor(isFocused = false, enabled = enabled))
            .clickable(
                enabled = enabled,
                onClick = onClick,
            )
            .heightIn(min = 48.dp)
            .padding(
                vertical = 12.dp,
                horizontal = 8.dp,
            )
            .fillMaxWidth(),
        inputText = text ?: "",
        placeholder = placeholder,
        leadingContent = leadingIcon ?: {},
        trailingContent = trailingIcon ?: {},
    ) {
        Text(
            text = text ?: "",
            fontFamily = FontFamily(Font(R.font.raleway_regular)),
            color = if (enabled) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.secondary,
        )
    }
}

@Composable
internal fun textInputFieldBorderWidth(
    @Suppress("UNUSED_PARAMETER")
    isFocused: Boolean = true,
): Dp = 1.dp

@Composable
internal fun textInputFieldBorderShape(): Shape =
    RoundedCornerShape(4.dp)

@Composable
internal fun textInputFieldBorderColor(
    isFocused: Boolean,
    enabled: Boolean,
    isError: Boolean,
): Color =
    when {
        isError && !isFocused -> MaterialTheme.colorScheme.error
        !isFocused || !enabled -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }

@Composable
internal fun textInputFieldContainerColor(
    @Suppress("UNUSED_PARAMETER")
    isFocused: Boolean,
    enabled: Boolean,
): Color =
    when {
        enabled -> MaterialTheme.colorScheme.surface
        else -> MaterialTheme.colorScheme.secondary
    }

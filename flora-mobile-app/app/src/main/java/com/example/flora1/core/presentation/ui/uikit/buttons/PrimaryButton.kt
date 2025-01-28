package com.example.flora1.core.presentation.ui.uikit.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.flora1.R
import com.example.flora1.core.presentation.designsystem.DisabledAlphaText
import com.example.flora1.core.presentation.designsystem.getPrimaryHorizontalBrush

@Composable
fun PrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    shouldFillMaxWidth: Boolean = true,
    onClick: () -> Unit,
    leadingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
    shape: Shape = CircleShape,
    paddingValues: PaddingValues = PaddingValues(
        horizontal = 0.dp,
        vertical = 0.dp,
    ),
    brush: Brush = getPrimaryHorizontalBrush(isEnabled = enabled),

    border: BorderStroke? = null,
) {

    Button(
        modifier = modifier
            .then(if (border != null) Modifier.border(border, shape) else Modifier)
            .testTag(stringResource(R.string.primary_button_test_tag)),
        shouldFillMaxWidth = shouldFillMaxWidth,
        text = text,
        enabled = enabled,
        onClick = onClick,
        textColor = if (enabled)
            MaterialTheme.colorScheme.onPrimary
        else
            MaterialTheme.colorScheme.onPrimary.copy(alpha = DisabledAlphaText),
        leadingIcon = leadingIcon,
        textStyle = textStyle,
        shape = shape,
        brush = brush,
        paddingValues = paddingValues,
    )
}

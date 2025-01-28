package com.example.flora1.core.presentation.ui.uikit.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.flora1.core.presentation.ui.modifier.applyIf

@Composable
fun Button(
    text: String,
    modifier: Modifier = Modifier,
    shouldFillMaxWidth: Boolean = true,
    onClick: () -> Unit,
    textColor: Color,
    textStyle: TextStyle,
    leadingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    color: Color,
    shape: Shape = CircleShape,
    paddingValues: PaddingValues = PaddingValues(
        horizontal = 0.dp,
        vertical = 0.dp,
    ),
) {
    Button(
        text = text,
        modifier = modifier,
        shouldFillMaxWidth = shouldFillMaxWidth,
        onClick = onClick,
        textColor = textColor,
        textStyle = textStyle,
        leadingIcon = leadingIcon,
        enabled = enabled,
        brush = Brush.horizontalGradient(listOf(color, color)),
        shape = shape,
        paddingValues = paddingValues,
    )

}

@Composable
fun Button(
    text: String,
    modifier: Modifier = Modifier,
    shouldFillMaxWidth: Boolean = true,
    onClick: () -> Unit,
    textColor: Color,
    textStyle: TextStyle,
    leadingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    brush: Brush,
    shape: Shape = CircleShape,
    paddingValues: PaddingValues = PaddingValues(
        horizontal = 0.dp,
        vertical = 0.dp,
    ),
) {

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = brush,
                shape = shape
            )
            .clickable(
                enabled = enabled,
                onClick = onClick,
            )
            .padding(
                horizontal = 20.dp,
                vertical = 12.dp
            )

    ) {
        Row(
            modifier = Modifier.padding(paddingValues = paddingValues),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {

            if (leadingIcon != null) {
                leadingIcon.invoke()

                Spacer(modifier = Modifier.width(10.dp))
            }

            Text(
                modifier = Modifier
                    .applyIf(
                        enabled = shouldFillMaxWidth,
                        otherModifier = {
                            Modifier.fillMaxWidth()
                        }
                    )
                    .align(Alignment.CenterVertically),
                text = text,
                textAlign = TextAlign.Center,
                color = textColor,
                style = textStyle,
            )
        }
    }
}

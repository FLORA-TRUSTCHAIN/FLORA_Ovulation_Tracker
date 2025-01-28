package com.example.flora1.core.presentation.ui.uikit.radio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.flora1.core.presentation.designsystem.Flora1Theme
import com.example.flora1.core.presentation.designsystem.getPrimaryHorizontalBrush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioButtonWithLabel(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    labelStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    enforceMinTouchTarget: Boolean = false,
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides enforceMinTouchTarget) {
        DefaultRadioButtonWithLabel(
            modifier = modifier,
            selected = selected,
            onClick = onClick,
            label = label,
            labelStyle = labelStyle,
        )
    }
}

@Composable
private fun DefaultRadioButtonWithLabel(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    containerPaddingValues: PaddingValues = PaddingValues(
        top = 25.dp,
        bottom = 25.dp,
        start = 15.dp,
        end = 25.dp,
    ),
    labelStyle: TextStyle = MaterialTheme.typography.bodyLarge,
) {
    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color =
                    if (selected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.tertiary,
                ),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable {
                onClick()
                focusManager.clearFocus()
            }
            .background(
                if (selected)
                    getPrimaryHorizontalBrush(true)
                else
                    Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
            )
            .padding(containerPaddingValues)
            .fillMaxWidth()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(end = 15.dp),
            text = label,
            style = labelStyle,
            color = if (selected) MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onBackground
        )

        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.colorScheme.tertiary,
            )
        )
    }
}

@PreviewLightDark
@Composable
fun RadioButtonSelectedPreview() {
    Flora1Theme {
        Surface(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            var selected by remember { mutableStateOf(true) }
            RadioButtonWithLabel(
                selected = selected,
                onClick = { selected = !selected },
                label = "d",
            )
        }
    }
}

@PreviewLightDark
@Composable
fun RadioButtonUnselectedPreview() {
    Flora1Theme {
        Surface(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            var selected by remember { mutableStateOf(false) }
            RadioButtonWithLabel(
                selected = selected,
                onClick = { selected = !selected },
                label = "d",
            )
        }
    }
}

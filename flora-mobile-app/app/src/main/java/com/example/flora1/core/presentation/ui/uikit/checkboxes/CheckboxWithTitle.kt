package com.example.flora1.core.presentation.ui.uikit.checkboxes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.flora1.core.presentation.designsystem.Flora1Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckboxWithTitle(
    modifier: Modifier = Modifier,
    checked: Boolean = true,
    onClick: () -> Unit = {},
    text: String = "",
    textAlign: TextAlign = TextAlign.Justify,
    style: TextStyle = LocalTextStyle.current,
) {
    Row(
        modifier = modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
            Checkbox(
                modifier = Modifier.padding(end = 10.dp),
                checked = checked,
                onCheckedChange = { onClick() },
            )
        }

        Text(
            text = text,
            textAlign = textAlign,
            style = style,
        )
    }
}

@Composable
@PreviewLightDark
private fun Preview() {
    Flora1Theme {
        Surface(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            CheckboxWithTitle(
                text = "Hello Hello Hello Hello Hello Hello Hello Hello Hello Hello"
            )
        }

    }
}

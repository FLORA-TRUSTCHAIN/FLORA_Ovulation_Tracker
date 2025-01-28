package com.example.flora1.core.presentation.ui.uikit.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flora1.core.presentation.designsystem.Flora1Theme
import gr.xe.android.uikitv2.buttons.multipleoptions.ToggleButton

@JvmName("PreselectedMultipleOptionsButton")
@Composable
fun <T : Any> MultipleOptionsButton(
    modifier: Modifier = Modifier,
    selectedOption: T,
    options: Iterable<T>,
    onSelectedOption: (T) -> Unit,
    text: T.() -> String,
) {
    MultipleOptionsButton(
        modifier = modifier,
        selectedOption = selectedOption as T?,
        options = options,
        onSelectedOption = {
            if (it != null)
                onSelectedOption(it)
        },
        text = text,
    )
}

@Composable
fun <T> MultipleOptionsButton(
    modifier: Modifier = Modifier,
    selectedOption: T?,
    options: Iterable<T>,
    onSelectedOption: (T?) -> Unit,
    text: T.() -> String,
) {
    val focusManager = LocalFocusManager.current
    val border = BorderStroke(
        width = 1.dp,
        color = MaterialTheme.colorScheme.tertiary,
    )
    val borderShape =
        RoundedCornerShape(size = 4.dp)
    Row(
        modifier = modifier
            .border(border, borderShape)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        options.forEach { option ->
            ToggleButton(
                text = text(option),
                onClick = {
                    focusManager.clearFocus()
                    onSelectedOption(if (option == selectedOption) null else option)
                },
                isSelected = selectedOption == option,
            )
        }
    }
}

private enum class Dummy {
    Dummy1,
    Dummy2,
    Dummy3,
    Dummy4,
}

@Preview(
    showBackground = true,
    name = "Preview - MultipleOptionsButton non preselected scenario",
)
@Composable
private fun Preview1() {
    Flora1Theme {
        Surface(
            modifier = Modifier
                .padding(5.dp),
        ) {
            var selectedOption: Dummy? by remember { mutableStateOf(null) }
            MultipleOptionsButton(
                modifier = Modifier
                    .fillMaxWidth(),
                selectedOption = selectedOption,
                options = Dummy.values().asIterable(),
                onSelectedOption = {
                    selectedOption = it
                },
                text = Dummy::name,
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "Preview - MultipleOptionsButton pre-selected scenario",
)
@Composable
private fun Preview2() {
    Flora1Theme {
        Surface(
            modifier = Modifier
                .padding(5.dp),
        ) {
            var selectedOption: Dummy by remember { mutableStateOf(Dummy.Dummy1) }
            MultipleOptionsButton(
                modifier = Modifier
                    .fillMaxWidth(),
                selectedOption = selectedOption,
                options = Dummy.values().asIterable(),
                onSelectedOption = { option: Dummy ->
                    selectedOption = option
                },
                text = Dummy::name,
            )
        }
    }
}

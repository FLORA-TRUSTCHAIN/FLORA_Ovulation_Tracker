package com.example.flora1.core.presentation.ui.uikit.textfields

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flora1.R

@Composable
fun UnitTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    unit: String,
    textStyle: TextStyle = TextStyle(
        fontSize = 70.sp,
    )
) {
    val textFieldValue by remember { mutableStateOf(TextFieldValue(value)) }


    UnitTextField(
        modifier = modifier,
        value = textFieldValue,
        onValueChange = { onValueChange(it.text) },
        unit = unit,
        textStyle = textStyle,

    )
}

@Composable
fun UnitTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    unit: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(
        fontSize = 70.sp,
        fontFamily = FontFamily(Font(R.font.opensans_regular)),
        color = MaterialTheme.colorScheme.onBackground,
    )
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        BasicTextField(
            value = value,
            textStyle = textStyle,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .alignBy(LastBaseline)
        )
        Spacer(modifier = Modifier.width(5.dp))


        Text(
            text = unit,
            modifier = Modifier.alignBy(LastBaseline)
        )
    }
}

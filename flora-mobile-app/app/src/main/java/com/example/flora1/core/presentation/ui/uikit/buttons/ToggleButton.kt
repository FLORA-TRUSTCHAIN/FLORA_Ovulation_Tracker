package gr.xe.android.uikitv2.buttons.multipleoptions

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flora1.core.presentation.designsystem.Flora1Theme
import androidx.compose.material3.Button as Material3Button

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun RowScope.ToggleButton(
    text: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
) {
    CompositionLocalProvider(
        LocalMinimumInteractiveComponentEnforcement provides false,
        LocalRippleTheme provides NoRipple,
    ) {
        val containerColor by animateColorAsState(
            targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            label = "container color",
        )
        val contentColor =
            if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.tertiary
        val colors =
            ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor,
            ).toMaterial3()

        Material3Button(
            modifier = Modifier
                .weight(1f),
            colors = colors,
            shape = RoundedCornerShape(size = 5.dp),
            onClick = onClick,
            contentPadding = PaddingValues(
                horizontal = 8.dp,
                vertical = 12.dp,
            ),
        ) {
            Text(
                modifier = Modifier.basicMarquee(),
                text = text,
                overflow = Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

private object NoRipple : RippleTheme {
    @Composable
    override fun defaultColor(): Color =
        Color.Transparent

    @Composable
    override fun rippleAlpha(): RippleAlpha =
        RippleAlpha(0f, 0f, 0f, 0f)
}

@Preview(
    showBackground = true,
    name = "Selected toggle button preview",
    widthDp = 200,
)
@Composable
private fun Preview1() {
    Flora1Theme {
        Row {
            ToggleButton(
                text = "Selected",
                isSelected = true,
                onClick = {},
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "Unselected toggle button preview",
    widthDp = 200,
)
@Composable
private fun Preview2() {
    Flora1Theme {
        Row {
            ToggleButton(
                text = "Unselected",
                isSelected = false,
                onClick = {},
            )
        }

    }
}

internal object ButtonDefaults {
    fun buttonColors(
        containerColor: Color = Color.Unspecified,
        contentColor: Color = Color.Unspecified,
        disabledContainerColor: Color = Color.Unspecified,
        disabledContentColor: Color = Color.Unspecified,
    ) = ButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
    )
}

@Composable
internal fun ButtonColors.toMaterial3(): ButtonColors =
    androidx.compose.material3.ButtonDefaults.buttonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
    )

package com.example.flora1.core.presentation.ui.uikit.dropdown

import androidx.annotation.StringRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.flora1.R
import com.example.flora1.core.presentation.designsystem.Flora1Theme
import com.example.flora1.core.presentation.ui.modifier.width


const val MAX_ITEMS_WE_WANT_TO_BE_DISPLAYED = 7  // 6 + the "No preference" item
const val DROPDOWN_MENU_ITEM_MIN_HEIGHT = 48    // Min height set to a DropdownMenuItem by Google
const val DROPDOWN_MENU_VERTICAL_PADDING = 8   // Vertical padding added to a DropdownMenu by Google

//Max height we want our Dropdowns to be
const val DROPDOWN_MENU_MAX_HEIGHT =
    (MAX_ITEMS_WE_WANT_TO_BE_DISPLAYED * DROPDOWN_MENU_ITEM_MIN_HEIGHT) + (2 * DROPDOWN_MENU_VERTICAL_PADDING) + (DROPDOWN_MENU_ITEM_MIN_HEIGHT / 2 + 4)

@Composable
fun <T : Enum<T>> DropdownWithBorderWithInlineLabel(
    modifier: Modifier = Modifier,
    selectedItem: T?,
    itemText: @Composable (T) -> String = { it.name },
    items: Array<T>,
    onItemSelected: (T) -> Unit,
    label: String,
    labelStyle: TextStyle = MaterialTheme.typography.bodySmall,
    @StringRes testTag : Int = R.string.dropdown_with_border_test_tag,
) {

    var expanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier.testTag(stringResource(testTag)),
    ) {
        var buttonWidth by remember { mutableStateOf<Int?>(null) }
        val density = LocalDensity.current

        DropdownWithInlineLabelButton(
            modifier = Modifier
                .onSizeChanged { buttonWidth = it.width },
            expanded = expanded,
            selectedItem = selectedItem,
            itemText = itemText,
            onClick = {
                focusManager.clearFocus()
                expanded = !expanded
            },
            label = label,
            labelStyle = labelStyle,
        )

        DropdownMenu(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .width(buttonWidth, density)
                .heightIn(max = DROPDOWN_MENU_MAX_HEIGHT.dp),
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(x = 0.dp, y = 4.dp),
        ) {
            items.forEach {
                DropdownMenuItem(
                    onClick = {
                        onItemSelected(it)
                        expanded = false
                    },
                    text = {
                        Text(
                            text = itemText(it),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                )
            }

        }

    }
}

@Composable
private fun <T : Enum<T>> DropdownWithInlineLabelButton(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    selectedItem: T?,
    itemText: @Composable (T) -> String,
    onClick: () -> Unit,
    label: String,
    labelStyle: TextStyle = MaterialTheme.typography.bodySmall,
) {
    val dropdownButtonShape = RoundedCornerShape(4.dp)
    Box(
        modifier = Modifier
            .padding(top = 8.dp)
            .background(MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = modifier
                .clip(dropdownButtonShape)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = dropdownButtonShape,
                )
                .clickable(onClick = onClick)
                .padding(
                    horizontal = 8.dp,
                    vertical = 12.dp,
                )
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (selectedItem != null) itemText(selectedItem) else "No comment",
                color = if (selectedItem != null) MaterialTheme.colorScheme.onBackground else
                    MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.bodyLarge,
            )

            val iconRotation by animateFloatAsState(
                targetValue = if (expanded) 180f else 0f, label = "",
            )
            Icon(
                modifier = Modifier
                    .rotate(iconRotation),
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "",
            )
        }
        Text(
            text = label,
            modifier = Modifier
                .offset(x = 4.dp, y = (-8).dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 4.dp)
                .align(alignment = Alignment.TopStart),
            style = labelStyle.copy(
                color = MaterialTheme.colorScheme.onBackground,
            ),
        )
    }
}

private enum class Options(val text: String) {
    ONE("1"),
    TWO("2"),
    THREE("3"),
}

@Preview(showBackground = true)
@Composable
fun Preview1() {
    var expanded by remember { mutableStateOf(false) }

    Flora1Theme {
        Surface(
            modifier = Modifier.padding(16.dp),
        ) {
            DropdownWithInlineLabelButton(
                expanded = expanded,
                selectedItem = Options.ONE,
                itemText = { it.text },
                onClick = { expanded = !expanded },
                label = "This is a test label",
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DropdownWithBorderWithInlineLabelPreview2() {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem: Options? by remember {
        mutableStateOf(null)
    }

    Flora1Theme {
        Surface(
            modifier = Modifier.padding(16.dp),
        ) {
            DropdownWithBorderWithInlineLabel(
                items = Options.entries.toTypedArray(),
                onItemSelected = {
                    selectedItem = it
                },
                selectedItem = selectedItem,
                itemText = { it.text },
                label = "This is a test label",
            )
        }
    }
}











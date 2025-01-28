package com.example.flora1.core.presentation.ui.uikit.bottomsheets

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.flora1.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ModalBottomSheet(
    modifier: Modifier = Modifier,
    title: String,
    sheetShape: Shape = RoundedCornerShape(
        topStart = 4.dp,
        topEnd = 4.dp,
    ),
    sheetElevation: Dp = 0.dp,
    sheetBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    paddingValues: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 24.dp,
    ),
    modalSheetState: SheetState = rememberModalBottomSheetState(),
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
    @StringRes testTag : Int = R.string.bottom_sheet_test_tag,
    ) {
    val coroutineScope = rememberCoroutineScope()

    BackHandler(modalSheetState.isVisible) {
        coroutineScope.launch { modalSheetState.hide() }
    }

    androidx.compose.material3.ModalBottomSheet(
        modifier = modifier.testTag(stringResource(id = testTag)),
        sheetState = modalSheetState,
        shape = sheetShape,
        tonalElevation = sheetElevation,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = sheetContentColor,
        scrimColor = scrimColor,
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier.verticalScroll(state = rememberScrollState())
        ) {
            BaseModalBottomSheetContent(
                title = title,
                onCloseButtonClicked = onDismissRequest,
                paddingValues = paddingValues,
                content = content,
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BaseModalBottomSheetContent(
    title: String,
    onCloseButtonClicked: () -> Unit,
    paddingValues: PaddingValues,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(paddingValues),
    ) {
        BottomSheetTitleWithCloseButton(
            title = title,
            onCloseButtonClicked = onCloseButtonClicked,
        )

        Spacer(modifier = Modifier.height(32.dp))

        CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
            content()
        }
    }
}

@Composable
private fun BottomSheetTitleWithCloseButton(
    modifier: Modifier = Modifier,
    title: String,
    onCloseButtonClicked: () -> Unit,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Icon(
            modifier = modifier
                .testTag(tag = stringResource(id = R.string.close_icon_test_tag))
                .size(size = 26.dp)
                .clickable(
                    enabled = true,
                    onClick = onCloseButtonClicked,
                ),
            imageVector = Icons.Default.Close,
            contentDescription = "",
            tint = iconTint,
        )
    }
}

package com.example.flora1.core.presentation.ui.uikit.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun CloseButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    CircleButton(
        modifier = modifier,
        imageVector = Icons.Default.Close,
        contentDescription = "next",
        enabled = enabled,
        onClick = onClick,
    )
}

@Composable
fun NextButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    CircleButton(
        modifier = modifier,
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = "next",
        enabled = enabled,
        onClick = onClick,
    )
}

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    CircleButton(
        modifier = modifier,
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
        contentDescription = "close",
        enabled = enabled,
        onClick = onClick,
    )
}

@Composable
fun CircleButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    enabled: Boolean = true,
    contentDescription: String = "icon",
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    borderColor: Color = MaterialTheme.colorScheme.tertiary,
    tint: Color? = MaterialTheme.colorScheme.onBackground,
    onClick: () -> Unit,
) {
    Icon(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor, CircleShape)
            .border(1.dp, borderColor, CircleShape)
            .clickable(enabled = enabled, onClick = onClick) // enlarge click area
            .padding(8.dp),
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = tint ?: Color.Unspecified,
    )
}

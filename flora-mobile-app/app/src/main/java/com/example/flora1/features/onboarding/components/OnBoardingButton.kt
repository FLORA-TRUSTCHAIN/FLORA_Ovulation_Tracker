package com.example.flora1.features.onboarding.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.flora1.R
import com.example.flora1.core.presentation.designsystem.DisabledAlphaText
import com.example.flora1.core.presentation.designsystem.getPrimaryHorizontalBrush

@Composable
fun OnBoardingButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    shape: Shape = CircleShape,
) {

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = getPrimaryHorizontalBrush(enabled),
                shape = CircleShape
            )
            .clickable(
                enabled = enabled,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,

        ) {
        Text(
            modifier =
            Modifier.padding(vertical = 16.dp),
            text = text,
            textAlign = TextAlign.Center,
            color = if (enabled)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onPrimary.copy(alpha = DisabledAlphaText),
            style = MaterialTheme.typography.labelMedium,
        )


        if (enabled) {
            Image(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(vertical = 8.dp)
                    .padding(end = 24.dp),
                painter = painterResource(id = R.drawable.ic_onboarding_next_arrow),
                contentDescription = "iconStart"
            )
        }
    }
}

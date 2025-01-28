package com.example.flora1.features.onboarding.averagecycle

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flora1.core.presentation.designsystem.DisabledAlphaText
import com.example.flora1.core.presentation.designsystem.getPrimaryHorizontalBrush
import com.example.flora1.features.onboarding.OnBoardingScreen
import com.example.flora1.features.onboarding.components.OnBoardingScaffold

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun AverageCycleRoot(
    onNext: () -> Unit,
    viewModel: AverageCycleViewModel = hiltViewModel(),
) {

    val selectedNumber = viewModel.selectedNumber

    OnBoardingScaffold(
        modifier = Modifier
            .verticalScroll(state = rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        selectedScreen = OnBoardingScreen.AVERAGE_CYCLE,
        title = "How long is your average cycle?",
        description = "A little hint - cycles usually last 24-35 days.",
        onNextClick = {
            selectedNumber?.let {
                viewModel.onSaveAverageCycleDays(it)
            }
            onNext()
        },
    ) {

        NumbersFlowRow(
            enabled = {
                selectedNumber == it
            },
            onClick = {
                if (it == selectedNumber)
                    viewModel.onSelectedNumberChange(number = null)
                else
                    viewModel.onSelectedNumberChange(number = it)
            }
        )
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NumbersFlowRow(
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit,
    enabled: (Int) -> Boolean,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        (MIN_CYCLE_DAYS..MAX_CYCLE_DAYS).forEach {
            PickNumber(
                enabled = enabled(it),
                num = it,
                onClick = { numClicked ->
                    onClick(numClicked)
                },
            )
        }
    }
}

@Composable
private fun PickNumber(
    size: Dp = 55.dp,
    num: Int,
    enabled: Boolean,
    onClick: (Int) -> Unit,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(brush = getPrimaryHorizontalBrush(isEnabled = enabled))
            .clickable(onClick = {
                onClick(num)
            }),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = num.toString(),
            color = if (enabled)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onPrimary.copy(alpha = DisabledAlphaText),
            style = MaterialTheme.typography.titleMedium,
        )

    }
}

private const val MIN_CYCLE_DAYS = 15
private const val MAX_CYCLE_DAYS = 55

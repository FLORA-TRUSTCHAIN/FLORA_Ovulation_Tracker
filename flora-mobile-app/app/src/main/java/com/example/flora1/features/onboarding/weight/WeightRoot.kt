package com.example.flora1.features.onboarding.weight

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flora1.core.presentation.ui.uikit.textfields.UnitTextField
import com.example.flora1.features.onboarding.OnBoardingScreen
import com.example.flora1.features.onboarding.components.OnBoardingScaffold

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun WeightRoot(
    onNext: () -> Unit,
    viewModel: WeightViewModel = hiltViewModel(),
) {
    val weight by viewModel.weight.collectAsStateWithLifecycle()
    val enabled by viewModel.enabled.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }


    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
        viewModel.onWeightChanged(
            weight.copy(
                selection = TextRange(weight.text.length) // Set cursor to the end
            )
        )
    }
    OnBoardingScaffold(
        title = "What is your weight?",
        isNextEnabled = enabled,
        selectedScreen = OnBoardingScreen.WEIGHT,
        onNextClick = {
            viewModel.onSaveWeight(weight.text.toFloat())
            onNext()
        },
    ) {
        UnitTextField(
            modifier = Modifier.focusRequester(focusRequester),
            value = weight,
            onValueChange = {
                viewModel.onWeightChanged(it)
            },
            unit = "kg"
        )
    }
}

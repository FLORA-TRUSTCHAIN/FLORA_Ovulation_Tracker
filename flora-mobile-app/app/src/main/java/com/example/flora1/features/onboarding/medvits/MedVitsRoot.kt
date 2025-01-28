package com.example.flora1.features.onboarding.medvits

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flora1.core.presentation.ui.uikit.buttons.MultipleOptionsButton
import com.example.flora1.features.onboarding.OnBoardingScreen
import com.example.flora1.features.onboarding.components.OnBoardingScaffold

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun MedVitsRoot(
    onNext: () -> Unit,
    viewModel: MedVitsViewModel = hiltViewModel(),
) {
    val isTakingMedVits by viewModel.isTakingMedVits.collectAsStateWithLifecycle()
    val description by viewModel.description.collectAsStateWithLifecycle()
    val booleanChoices = listOf(true, false)
    var hasFocusedOnce by remember { mutableStateOf(false) }
    val focusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(key1 = isTakingMedVits) {
        if (isTakingMedVits && !hasFocusedOnce) {
            focusRequester.requestFocus()
            hasFocusedOnce = true
        }
    }
    OnBoardingScaffold(
        verticalArrangement = Arrangement.Top,
        selectedScreen = OnBoardingScreen.MED_VITS,
        title = "Have you ever received any medication or vitamins?",
        onNextClick = {
            viewModel.onSaveHasTakenMedVits(isTakingMedVits)
            viewModel.onSaveMedVitsDescription(
                if (isTakingMedVits)
                    description
                else
                    ""
            )
            onNext()
        },
    ) {
        MultipleOptionsButton(
            selectedOption = isTakingMedVits,
            options = booleanChoices,
            onSelectedOption = { isTakingMedVits: Boolean ->
                viewModel.onIsTakingMedVitsChanged(isTakingMedVits)
            },
            text = {
                if (this) "Yes" else "No"
            },
        )
        Spacer(modifier = Modifier.height(35.dp))

        AnimatedVisibility(visible = isTakingMedVits) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = description,
                textStyle = MaterialTheme.typography.bodyLarge,
                onValueChange = { viewModel.onDescriptionChanged(it) },
                label = {
                    Text(
                        text = "MedVits description",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                placeholder = {
                    Text(
                        text = "Tell us what you've received in the past...",
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                minLines = 10,
                maxLines = 10,
            )

        }
    }

}

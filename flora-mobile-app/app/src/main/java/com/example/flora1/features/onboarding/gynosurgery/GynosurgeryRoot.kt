package com.example.flora1.features.onboarding.gynosurgery

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
fun GynosurgeryRoot(
    onNext: () -> Unit,
    viewModel: GynosurgeryViewModel = hiltViewModel(),
) {
    val hasDoneGynosurgery by viewModel.hasDoneGynosurgery.collectAsStateWithLifecycle()
    val description by viewModel.description.collectAsStateWithLifecycle()
    val booleanChoices = listOf(true, false)
    var hasFocusedOnce by remember { mutableStateOf(false) }
    val focusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(key1 = hasDoneGynosurgery) {
        if (hasDoneGynosurgery && !hasFocusedOnce) {
            focusRequester.requestFocus()
            hasFocusedOnce = true
        }
    }
    OnBoardingScaffold(
        verticalArrangement = Arrangement.Top,
        selectedScreen = OnBoardingScreen.GYNECOSURGERY,
        title = "Have you ever performed gynecology surgery?",
        onNextClick = {
            viewModel.onSaveHasDoneGynecosurgery(hasDoneGynosurgery)
            viewModel.onSaveGyncosurgeryDescription(
                if (hasDoneGynosurgery)
                    description
                else
                    ""
            )
            onNext()
        },
    ) {
        MultipleOptionsButton(
            selectedOption = hasDoneGynosurgery,
            options = booleanChoices,
            onSelectedOption = { hasDoneGynosurgery: Boolean ->
                viewModel.onIsTakingMedVitsChanged(hasDoneGynosurgery)
            },
            text = {
                if (this) "Yes" else "No"
            },
        )
        Spacer(modifier = Modifier.height(35.dp))

        AnimatedVisibility(visible = hasDoneGynosurgery) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = description,
                textStyle = MaterialTheme.typography.bodyLarge,
                onValueChange = { viewModel.onDescriptionChanged(it) },
                label = {
                    Text(
                        text = "Gynecology description",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                placeholder = {
                    Text(
                        text = "Give a brief description about your gynecology surgery...",
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

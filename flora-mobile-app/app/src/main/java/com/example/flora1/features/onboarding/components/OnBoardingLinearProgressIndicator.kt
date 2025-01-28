package com.example.flora1.features.onboarding.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.flora1.features.onboarding.OnBoardingScreen


@Composable
fun OnBoardingLinearProgressIndicator(
    selectedScreen : OnBoardingScreen,
){
    LinearProgressIndicator(
        modifier = Modifier.fillMaxWidth(),
        progress = {
            (selectedScreen.ordinal.toFloat())/OnBoardingScreen.entries.size
        },

    )
}

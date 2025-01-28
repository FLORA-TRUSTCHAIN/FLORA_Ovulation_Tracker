package com.example.flora1.features.onboarding

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.flora1.R
import com.example.flora1.features.onboarding.born.BornScreenViewModel.Companion.MIN_ELIGIBLE_AGE_TO_USE_FLORA
import com.example.flora1.features.onboarding.components.OnBoardingScaffold

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun MinorAgeRoot(
    onBack: () -> Unit,
) {
    OnBoardingScaffold(
        verticalArrangement = Arrangement.Top,
        isNextEnabled = false,
        isBackEnabled = true,
        onBackClick = onBack,
        selectedScreen = OnBoardingScreen.MINOR_AGE,
        title = stringResource(id = R.string.uh_oh),

        ) {
        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = stringResource(R.string.must_be_less_than_eligible_age_description, MIN_ELIGIBLE_AGE_TO_USE_FLORA),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Justify,
        )
    }
}

package com.example.flora1.core.presentation.ui.uikit.dialogs

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.flora1.R

@Composable
fun PredictionDialog(
    modifier: Modifier = Modifier,
    onAccept: () -> Unit,
    onDismiss: () -> Unit,
) {
    FloraDialog(
        modifier = modifier,
        onAccept = onAccept,
        onDismiss = onDismiss,
        title = "Enable Prediction Mode",
        desc = buildAnnotatedString {
            append("Would you like to enable Flora's ")

            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            ) {
                append("Prediction Mode")
            }

            append(
                "? This will allow us to predict your" +
                        " next period and fertile days based on the information you've presented us with. " +
                        "You can always enable "
            )

            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            ) {
                append("Prediction Mode")
            }

            append(" in Settings whenever you like.")
        },
        headerImage = {
            HeaderImage(
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.TopCenter)
            )
        },
        testTag = R.string.prediction_dialog_test_tag,
    )

}


@Composable
private fun HeaderImage(modifier: Modifier) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(
            R.raw.clever_woman
        )
    )
    val progress = animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )

    LottieAnimation(
        composition = composition,
        progress = { progress.value },
        modifier = modifier
    )
}

package com.example.flora1.features.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flora1.R
import com.example.flora1.core.presentation.designsystem.getPrimaryLinearBrush

@Composable
fun SplashScreenRoot(
    onFinishedAnimation: () -> Unit,
) {
    var floraSize by remember {
        mutableStateOf(0.dp)
    }

    var alphaText by remember {
        mutableFloatStateOf(0f)
    }

    val catchyLine = remember {
        DEFAULT_FLORA_CATCHY_LINES.random()
    }
    val animateToDp by animateDpAsState(
        targetValue = floraSize,
        label = "",
        animationSpec = tween(
            durationMillis = DEFAULT_FLORA_LOGO_DURATION_ANIMATION,
        ),
        finishedListener = {
            alphaText = 1f
        },
    )

    val alphaAsAnimation by animateFloatAsState(
        targetValue = alphaText,
        label = "",
        animationSpec = tween(
            durationMillis = DEFAULT_FLORA_TEXT_DURATION_ANIMATION,
        ),
        finishedListener = {
            onFinishedAnimation()
        },
    )

    BackHandler {}


    LaunchedEffect(key1 = Unit) {
        floraSize = DEFAULT_FLORA_LOGO_SIZE
    }

    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .background(brush = getPrimaryLinearBrush())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier.size(animateToDp),
            painter = painterResource(id = R.drawable.flora_logo_new),
            contentDescription = ""
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            modifier = Modifier.alpha(alphaAsAnimation),
            text = catchyLine,
            fontFamily = FontFamily(Font(R.font.raleway_regular)),
            fontSize = 24.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
        )
    }
}

private const val DEFAULT_FLORA_LOGO_DURATION_ANIMATION = 1000
private const val DEFAULT_FLORA_TEXT_DURATION_ANIMATION = 2000
private val DEFAULT_FLORA_LOGO_SIZE = 250.dp

private val DEFAULT_FLORA_CATCHY_LINES = listOf(
    "Flora: Your #1 Private Period Tracker with Cutting-Edge Predictions",
    "Stay Private, Stay Accurate with Flora's Federated Learning",
    "Flora: Predicting Periods Privately",
    "Your Cycle, Your Privacy: Trust Flora",
    "Flora: Empowering Women with Secure, Smart Predictions",
    "Flora: The Period Tracker That Values Your Privacy",
    "Flora: Advanced Predictions, Absolute Privacy",
    "Predict and Protect: Flora's Promise of Privacy",
    "Your Privacy, Our Priority: Flora Period Tracker",
    "Flora: Where Privacy Meets Precision in Period Tracking"
)

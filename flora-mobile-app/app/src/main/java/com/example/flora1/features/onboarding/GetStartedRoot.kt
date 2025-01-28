package com.example.flora1.features.onboarding

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.flora1.R
import com.example.flora1.core.presentation.designsystem.getPrimaryVerticalBrush
import com.example.flora1.core.presentation.ui.uikit.buttons.PrimaryButton
import com.example.flora1.core.presentation.ui.uikit.buttons.SecondaryButton
import com.example.flora1.data.preferences.shouldShowOnBoarding

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun GetStartedRoot(
    onPrimaryClicked: () -> Unit,
    onSecondaryClicked: () -> Unit,
    viewModel: GetStartedViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    val buttonBorder = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.onPrimary)

    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.girl_walking_lottie))

    val progress = animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
    var hasClickedGetStarted by remember {
        mutableStateOf(false)
    }

    var sizeGirl by remember {
        mutableStateOf(250.dp)
    }

    val sizeAsAnimation by animateDpAsState(
        targetValue = sizeGirl,
        animationSpec = tween(
            durationMillis = 3000,
        ),
        label = "",
        finishedListener = {
        }
    )

    var alpha by remember {
        mutableStateOf(1f)
    }

    val alphaAsAnimations by animateFloatAsState(
        targetValue = alpha,
        animationSpec = tween(
            durationMillis = 4000,
        ),
        label = "",
        finishedListener = {
            onPrimaryClicked()
        }
    )

    BackHandler {}

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alphaAsAnimations)
            .background(brush = getPrimaryVerticalBrush()),
    ) {
        val (image, middleButtons, infoTexts) = createRefs()


        if (!hasClickedGetStarted)
            Column(
                modifier = Modifier
                    .constrainAs(middleButtons) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {

                val horizontalPadding = 40.dp
                val verticalPadding = 10.dp
                PrimaryButton(
                    modifier = Modifier
                        .padding(horizontal = horizontalPadding),
                    border = buttonBorder,
                    onClick = {
                        context.shouldShowOnBoarding = false
                        alpha = 0f
                        hasClickedGetStarted = true
                        sizeGirl = 2000.dp
                    },
                    paddingValues = PaddingValues(vertical = verticalPadding),
                    text = "Get Started!"
                )

                Spacer(modifier = Modifier.height(15.dp))
                SecondaryButton(
                    onClick = onSecondaryClicked,
                    border = buttonBorder,
                    paddingValues = PaddingValues(vertical = verticalPadding),
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                    text = "Delete and Re-OnBoard",
                )
            }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(image) {
                    top.linkTo(parent.top)
                },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Image(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .size(75.dp),
                painter = painterResource(id = R.drawable.flora_logo_new),
                contentDescription = ""
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(infoTexts) {
                    top.linkTo(image.bottom, margin = 15.dp)
                },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "You're done!",
                fontFamily = FontFamily(Font(R.font.raleway_bold)),
                fontSize = 24.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "We are now ready to explore the world of Flora ",
                fontFamily = FontFamily(Font(R.font.raleway_regular)),
                fontSize = 14.sp,
                color = Color.White,
                textAlign = TextAlign.Start,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        )
        {
            if (!progress.isPlaying)
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(WindowInsets.navigationBars.asPaddingValues())
                        .size(60.dp)
                        .align(Alignment.BottomCenter),
                    color = MaterialTheme.colorScheme.primary
                )
            else
                LottieAnimation(
                    modifier = Modifier
                        .testTag(stringResource(id = R.string.lottie_elegant_woman_test_tag))
                        .size(sizeAsAnimation)
                        .align(Alignment.BottomCenter),
                    composition = composition,
                    progress = { progress.value },
                )
        }
    }
}

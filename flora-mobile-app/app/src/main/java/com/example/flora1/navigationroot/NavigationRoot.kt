package com.example.flora1.navigationroot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.flora1.data.preferences.shouldShowOnBoarding
import com.example.flora1.features.onboarding.SplashScreenRoot
import com.example.flora1.navigationroot.main.mainNavigationRoot
import com.example.flora1.navigationroot.onboarding.onBoardingNavigationRoot

@Composable
fun NavigationRoot(
    navController: NavHostController,
) {
    val context = LocalContext.current
    NavHost(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        navController = navController,
        startDestination = Screen.startDestination,
    ) {

        composable<Screen.Splash> {
            SplashScreenRoot(
                onFinishedAnimation = {
                    navController.popBackStack()
                    navController.navigate(
                        if (context.shouldShowOnBoarding)
                            Screen.UsernameAge
                        else
                            Screen.Main
                    )
                }
            )
        }

        onBoardingNavigationRoot(navController = navController)
        mainNavigationRoot(navController = navController)

    }
}


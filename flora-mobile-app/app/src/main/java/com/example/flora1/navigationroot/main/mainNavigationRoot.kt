package com.example.flora1.navigationroot.main

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.flora1.features.calendar.CalendarRoot
import com.example.flora1.features.main.MainRoot
import com.example.flora1.features.profile.ProfileRoot
import com.example.flora1.features.profile.consent.ManageConsentRoot
import com.example.flora1.features.profile.mydoctors.MyDoctorsRoot
import com.example.flora1.navigationroot.Screen

internal fun NavGraphBuilder.mainNavigationRoot(navController: NavController) {

    composable<Screen.Main> {
        MainRoot(
            onCalendarClick = { navController.navigate(Screen.Calendar) },
            onProfileClick = { navController.navigate(Screen.Settings) }
        )
    }

    composable<Screen.Calendar>(
        enterTransition = { enterToRight() },
        exitTransition = { exitFromRight() },
    ) {
        CalendarRoot()
    }

    composable<Screen.Settings>(
        enterTransition = { enterToLeft() },
        exitTransition = { exitFromLeft() },
    ) {
        ProfileRoot(
            onBack = { navController.popBackStack() },
            onNavigateToManageConsent = { navController.navigate(Screen.ManageConsent) },
            onNavigateToMyDoctors = { navController.navigate(Screen.MyDoctors) },
        )
    }

    composable<Screen.ManageConsent>(
        enterTransition = { enterToRight() },
        exitTransition = { exitFromRight() },
    ) {
        ManageConsentRoot(
            onBack = { navController.popBackStack() }
        )
    }

    composable<Screen.MyDoctors>(
        enterTransition = { enterToRight() },
        exitTransition = { exitFromRight() },
    ) {
        MyDoctorsRoot()
    }
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.enterToRight() = slideIntoContainer(
    animationSpec = tween(300, easing = EaseIn),
    towards = AnimatedContentTransitionScope.SlideDirection.Start
)

private fun AnimatedContentTransitionScope<NavBackStackEntry>.exitFromRight() = slideOutOfContainer(
    animationSpec = tween(300, easing = EaseOut),
    towards = AnimatedContentTransitionScope.SlideDirection.End
)

private fun AnimatedContentTransitionScope<NavBackStackEntry>.enterToLeft() = slideIntoContainer(
    animationSpec = tween(300, easing = EaseIn),
    towards = AnimatedContentTransitionScope.SlideDirection.End
)

private fun AnimatedContentTransitionScope<NavBackStackEntry>.exitFromLeft() =
    slideOutOfContainer(
        animationSpec = tween(300, easing = EaseOut),
        towards = AnimatedContentTransitionScope.SlideDirection.Start
    )


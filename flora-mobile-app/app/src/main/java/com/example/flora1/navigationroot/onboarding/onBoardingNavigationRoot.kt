package com.example.flora1.navigationroot.onboarding

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.flora1.features.onboarding.GetStartedRoot
import com.example.flora1.features.onboarding.MinorAgeRoot
import com.example.flora1.features.onboarding.averagecycle.AverageCycleRoot
import com.example.flora1.features.onboarding.born.BornRoot
import com.example.flora1.features.onboarding.contraceptives.ContraceptivesRoot
import com.example.flora1.features.onboarding.gynosurgery.GynosurgeryRoot
import com.example.flora1.features.onboarding.lastperiod.LastPeriodRoot
import com.example.flora1.features.onboarding.medvits.MedVitsRoot
import com.example.flora1.features.onboarding.pregnancy.PregnancyRoot
import com.example.flora1.features.onboarding.pregnancystats.PregnancyStatsRoot
import com.example.flora1.features.onboarding.race.RaceRoot
import com.example.flora1.features.onboarding.sleepqualitytilllastperiod.SleepQualityTillLastPeriodRoot
import com.example.flora1.features.onboarding.stresstilllastperiod.StressTillLastPeriodRoot
import com.example.flora1.features.onboarding.usernameage.HeightRoot
import com.example.flora1.features.onboarding.usernameage.UsernameAgeRoot
import com.example.flora1.features.onboarding.weight.WeightRoot
import com.example.flora1.navigationroot.Screen

fun NavGraphBuilder.onBoardingNavigationRoot(
    navController: NavController,
) {

    composable<Screen.UsernameAge> {
        UsernameAgeRoot(
            onNext = {
                navController.navigate(Screen.Born)
            }
        )
    }

    composable<Screen.Born> {
        BornRoot(
            onNext = { isEligibleForFlora ->
                if (isEligibleForFlora)
                    navController.navigate(Screen.Height)
                else
                    navController.navigate(Screen.MinorAge)
            }
        )
    }

    composable<Screen.MinorAge> {
        MinorAgeRoot(onBack = {
            navController.navigateUp()
        })
    }

    composable<Screen.Height> {
        HeightRoot(
            onNext = {
                navController.navigate(Screen.Weight)
            }
        )
    }

    composable<Screen.Weight> {
        WeightRoot(
            onNext = {
                navController.navigate(Screen.Pregnancy)
            }
        )
    }

    composable<Screen.Pregnancy> {
        PregnancyRoot(
            onNext = { hasBeenPregnant ->
                if (hasBeenPregnant)
                    navController.navigate(Screen.PregnancyStats)
                else
                    navController.navigate(Screen.Race)
            }
        )
    }

    composable<Screen.PregnancyStats> {
        PregnancyStatsRoot(
            onNext = {
                navController.navigate(Screen.Race)
            },
            onBack = navController::navigateUp,
        )
    }

    composable<Screen.Race> {
        RaceRoot(
            onNext = {
                navController.navigate(Screen.MedVits)
            },
        )
    }

    composable<Screen.MedVits> {
        MedVitsRoot(
            onNext = {
                navController.navigate(Screen.Gynecosurgery)
            },
        )
    }

    composable<Screen.Gynecosurgery> {
        GynosurgeryRoot(
            onNext = {
                navController.navigate(Screen.Contraceptives)
            },
        )
    }

    composable<Screen.Contraceptives> {
        ContraceptivesRoot(
            onNext = {
                navController.navigate(Screen.StressLevelTillLastPeriod)
            },
        )
    }

    composable<Screen.StressLevelTillLastPeriod> {
        StressTillLastPeriodRoot(
            onNext = {
                navController.navigate(Screen.SleepQualityTillLastPeriod)
            },
        )
    }

    composable<Screen.SleepQualityTillLastPeriod> {
        SleepQualityTillLastPeriodRoot(
            onNext = {
                navController.navigate(Screen.AverageCycle)
            },
            onBack = navController::navigateUp,
        )
    }

    composable<Screen.AverageCycle> {
        AverageCycleRoot(
            onNext = { navController.navigate(Screen.LastPeriod) },
        )
    }

    composable<Screen.LastPeriod> {
        LastPeriodRoot(
            onNext = { navController.navigate(Screen.GetStarted) },
        )
    }

    composable<Screen.GetStarted> {
        GetStartedRoot(
            onPrimaryClicked = {
                /*
                Will have to test if popBackStack overloads work
                 */
                navController.popBackStack(Screen.UsernameAge, inclusive = true)
                navController.navigate(Screen.Main)
            },
            onSecondaryClicked = {
                navController.popBackStack(Screen.UsernameAge, inclusive = false)
            }
        )
    }
}

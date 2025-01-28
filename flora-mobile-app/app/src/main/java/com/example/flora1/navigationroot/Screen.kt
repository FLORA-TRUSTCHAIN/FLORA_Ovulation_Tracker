package com.example.flora1.navigationroot

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Splash : Screen

    @Serializable
    data object UsernameAge : Screen

    @Serializable
    data object Height : Screen

    @Serializable
    data object Weight : Screen

    @Serializable
    data object Pregnancy : Screen

    @Serializable
    data object PregnancyStats : Screen

    @Serializable
    data object Race : Screen

    @Serializable
    data object MedVits : Screen

    @Serializable
    data object Gynecosurgery : Screen

    @Serializable
    data object Contraceptives : Screen

    @Serializable
    data object StressLevelTillLastPeriod : Screen

    @Serializable
    data object SleepQualityTillLastPeriod : Screen

    @Serializable
    data object Born : Screen

    @Serializable
    data object MinorAge : Screen

    @Serializable
    data object AverageCycle : Screen

    @Serializable
    data object LastPeriod : Screen

    @Serializable
    data object GetStarted : Screen

    @Serializable
    data object Calendar : Screen

    @Serializable
    data object Main : Screen

    @Serializable
    data object Settings : Screen

    @Serializable
    data object ManageConsent : Screen

    @Serializable
    data object MyDoctors : Screen

    companion object {
        val startDestination = Splash
    }
}

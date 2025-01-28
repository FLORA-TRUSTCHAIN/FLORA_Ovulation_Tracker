package com.example.flora1.domain

import com.example.flora1.features.onboarding.contraceptives.ContraceptiveMethod
import com.example.flora1.features.onboarding.race.Race
import com.example.flora1.features.onboarding.sleepqualitytilllastperiod.SleepQuality
import com.example.flora1.features.onboarding.stresstilllastperiod.StressLevelTillLastPeriod
import com.example.flora1.features.onboarding.weight.NumericalOptions
import com.example.flora1.features.onboarding.weight.PregnancyStatus

interface Preferences {

    fun saveAverageCycle(averageCycleDays: Int)
    fun saveDateOfBirth(dateOfBirth: Long)
    fun saveToken(token: String)
    fun saveHeight(height: Float)
    fun saveWeight(weight: Float)
    fun savePregnancyStatus(pregnancyStatus: PregnancyStatus)
    fun saveTotalPregnancies(number: NumericalOptions)
    fun saveTotalMiscarriages(number: NumericalOptions)
    fun saveTotalAbortions(number: NumericalOptions)
    fun saveRace(race: Race)
    fun saveHasTakenMedVits(hasTakenMedVits: Boolean)
    fun saveMedVitsDescription(description: String)
    fun saveHasDoneGynecosurgery(hasDoneGynecosurgery: Boolean)
    fun saveGyncosurgeryDescription(description: String)
    fun saveIsBreastfeeding(isBreastfeeding: Boolean)
    fun saveHasGivenDataConsent(hasGivenDataConsent: Boolean)
    fun saveContraceptiveMethods(contraceptiveMethods: List<ContraceptiveMethod>)
    fun saveStressLevelTillLastPeriod(stressLevel: StressLevelTillLastPeriod)
    fun saveSleepQualityTillLastPeriod(sleepQuality: SleepQuality)

    fun saveShouldShowPredictionDialog(shouldShowPredictionDialog: Boolean)
    fun saveShouldShowPredictions(shouldShowPredictions: Boolean)

    val token: String
    val isLoggedIn: Boolean
    val height: Float
    val weight: Float
    val pregnancyStatus: PregnancyStatus
    val totalPregnancies: NumericalOptions?
    val totalMiscarriages: NumericalOptions?
    val totalAbortions: NumericalOptions?
    val race: Race
    val hasTakenMedVits: Boolean
    val hasGivenDataConsent: Boolean
    val medVitsDescription: String
    val hasDoneGynecosurgery: Boolean
    val gyncosurgeryDescription: String
    val isBreastfeeding: Boolean
    val contraceptiveMethods: List<ContraceptiveMethod>
    val stressLevelTillLastPeriod: StressLevelTillLastPeriod
    val sleepQualityTillLastPeriod: SleepQuality
    val averageCycleDays: Int
    val dateOfBirth: String

    val shouldShowPredictionDialog: Boolean
    val shouldShowPredictions: Boolean
}

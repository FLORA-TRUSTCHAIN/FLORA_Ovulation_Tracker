package com.example.flora1.navigationroot

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isNotFocused
import androidx.compose.ui.test.isNotSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.example.flora1.HiltComponentActivity
import com.example.flora1.R
import com.example.flora1.android_test.hasTestTag
import com.example.flora1.android_test.onAllUnmergedNodes
import com.example.flora1.android_test.onUnmergedNode
import com.example.flora1.assertCurrentRouteName
import com.example.flora1.data.db.PeriodDatabase
import com.example.flora1.data.preferences.shouldShowOnBoarding
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkObject
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class NavigationRootUiTest {
    private lateinit var navController: TestNavHostController

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<HiltComponentActivity>()

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            mockkStatic(Context::shouldShowOnBoarding)
            mockkObject(Screen)
            every { Screen.startDestination } returns Screen.Splash.name
        }

        @AfterClass
        @JvmStatic
        fun afterClass() {
            unmockkStatic(Context::shouldShowOnBoarding)
            unmockkObject(Screen)
        }
    }

    @Inject
    lateinit var fakeDb: PeriodDatabase

    @Before
    fun init() {
        hiltRule.inject()
        composeRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            NavigationRoot(navController = navController)
        }
    }

    @After
    fun close(){
        fakeDb.close()
    }

    @Test
    fun verify_correct_start_destination() {
        // given
        every { any<Context>().shouldShowOnBoarding } returns true

        // then
        navController.assertCurrentRouteName(Screen.UsernameAge.name)
    }

    @Test
    fun when_should_showOnBoarding_completes_successfully() {
        // given
        every { any<Context>().shouldShowOnBoarding } returns true

        // Username
        composeRule.onNode(hasSetTextAction()).performTextInput("Christinaaa")
        composeRule.onNode(hasTestTag(R.string.primary_button_test_tag)).performClick()

        // Born
        navController.assertCurrentRouteName(Screen.Born.name)
        composeRule.onNode(hasContentDescription("Switch to text input mode")).performClick()
        composeRule.onNode(hasSetTextAction()).performTextInput("10102000")
        composeRule.onNode(hasTestTag(R.string.primary_button_test_tag)).performClick()

        // Height
        navController.assertCurrentRouteName(Screen.Height.name)
        composeRule.onNode(hasTestTag(R.string.primary_button_test_tag)).performClick()

        // Weight
        navController.assertCurrentRouteName(Screen.Weight.name)
        composeRule.onNode(hasTestTag(R.string.primary_button_test_tag)).performClick()

        // Pregnancy
        navController.assertCurrentRouteName(Screen.Pregnancy.name)
        composeRule.onNode(hasText("Yes")).performClick()
        composeRule.onNode(hasTestTag(R.string.primary_button_test_tag)).performClick()

        // Pregnancy Stats
        navController.assertCurrentRouteName(Screen.PregnancyStats.name)
        composeRule.onUnmergedNode(hasTestTag(R.string.dropdown_pregnancy_test_tag)).performClick()
        composeRule.onUnmergedNode(hasText("1")).performClick()
        composeRule.onUnmergedNode(
            hasTestTag(
                R.string.dropdown_miscarriages_test_tag
            )
        )
            .performClick()
        composeRule.onUnmergedNode(hasText("2")).performClick()
        composeRule.onUnmergedNode(hasTestTag(R.string.dropdown_abortions_test_tag)).performClick()
        composeRule.onUnmergedNode(hasText("3")).performClick()
        composeRule.onNode(hasTestTag(R.string.primary_button_test_tag)).performClick()

        // Race
        navController.assertCurrentRouteName(Screen.Race.name)
        composeRule.onUnmergedNode(hasTestTag(R.string.dropdown_race_test_tag)).performClick()
        composeRule.onUnmergedNode(hasText("White")).performClick()
        composeRule.onNode(hasTestTag(R.string.primary_button_test_tag)).performClick()

        // MedVits
        navController.assertCurrentRouteName(Screen.MedVits.name)
        composeRule.onNode(hasText("Yes")).performClick()
        composeRule.onNode(hasSetTextAction()).performTextInput("Yes, I have")
        composeRule.onNode(hasTestTag(R.string.icon_next_test_tag)).performClick()

        // Gynecosurgery
        navController.assertCurrentRouteName(Screen.Gynecosurgery.name)
        composeRule.onNode(hasText("Yes")).performClick()
        composeRule.onNode(hasSetTextAction()).performTextInput("Yes, I have")
        composeRule.onNode(hasTestTag(R.string.icon_next_test_tag)).performClick()

        // Contraceptives
        navController.assertCurrentRouteName(Screen.Contraceptives.name)
        composeRule.onNode(hasTestTag(R.string.clickable_text_field_test_tag)).performClick()
        composeRule.onNode(hasText("Pill")).performClick()
        composeRule.onNode(hasTestTag(R.string.close_icon_test_tag)).performClick()
        composeRule.onNode(hasTestTag(R.string.icon_next_test_tag)).performClick()

        // Stress Levels
        navController.assertCurrentRouteName(Screen.StressLevelTillLastPeriod.name)
        composeRule.onNode(hasText("Low")).performClick()
        composeRule.onNode(hasTestTag(R.string.icon_next_test_tag)).performClick()

        // Sleep Quality
        navController.assertCurrentRouteName(Screen.SleepQualityTillLastPeriod.name)
        composeRule.onNode(hasText("Unsatisfactory")).performClick()
        composeRule.onNode(hasTestTag(R.string.icon_next_test_tag)).performClick()

        // Average Cycle
        navController.assertCurrentRouteName(Screen.AverageCycle.name)
        composeRule.onNode(hasText("30")).performClick()
        composeRule.onNode(hasTestTag(R.string.icon_next_test_tag)).performClick()

        // Last Period
        navController.assertCurrentRouteName(Screen.LastPeriod.name)

        /*
        Clicks 1st unselected and enabled date
         */
        val selectableDates =
            composeRule.onAllUnmergedNodes(hasClickAction() and isNotSelected() and isNotFocused())

        if (selectableDates.fetchSemanticsNodes().isNotEmpty())
            selectableDates[0].performClick()

        composeRule.onNode(hasTestTag(R.string.icon_next_test_tag)).performClick()

        // Get Started!
        navController.assertCurrentRouteName(Screen.GetStarted.name)
        composeRule.onNode(hasText("Get Started!")).performClick()

        // Main!
        composeRule.waitForIdle()
        navController.assertCurrentRouteName(Screen.Main.name)


    }

    @Test
    fun when_should_showOnBoarding_navigates_to_main_screen() {
        // given
        every { any<Context>().shouldShowOnBoarding } returns false

        // then
        composeRule.onNode(hasTestTag(R.string.prediction_dialog_test_tag)).assertIsDisplayed()

    }
}

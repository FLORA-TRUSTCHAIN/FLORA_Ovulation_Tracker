package com.example.flora1.features.onboarding.usernameage

import com.example.flora1.MainDispatcherRule
import com.example.flora1.R
import com.example.flora1.domain.Preferences
import com.example.flora1.testFirst
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UsernameAgeViewModelTest {

    @get:Rule
    val rule = MainDispatcherRule()

    lateinit var viewModel: UsernameAgeViewModel
    private var preferences: Preferences = mockk(relaxed = true)


    @Before
    fun e() {
        viewModel = UsernameAgeViewModel(
            preferences = preferences,
        )
    }


    @Test
    fun `user types more chars than max, shows error message`() {
        // given
        val onShowMessage: (Int) -> Unit = mockk(relaxed = true)

        // when
        viewModel.onUsernameChange(
            username = "1".repeat(UsernameAgeViewModel.MAX_USERNAME_CHARS + 1),
            onShowMessage = onShowMessage,
        )

        // then
        verify(exactly = 1) {
            onShowMessage(R.string.max_username_chars)
        }
    }

    @Test
    fun `user types less chars than max, does not show error message`() {
        // given
        val onShowMessage: (Int) -> Unit = mockk(relaxed = true)

        // when
        viewModel.onUsernameChange(
            username = "1".repeat(UsernameAgeViewModel.MAX_USERNAME_CHARS - 1),
            onShowMessage = onShowMessage,
        )

        // then
        verify(exactly = 0) {
            onShowMessage(R.string.max_username_chars)
        }
    }

    @Test
    fun `onUsernameChange - changes value`() {
        // given
        val onShowMessage: (Int) -> Unit = mockk(relaxed = true)

        // when
        viewModel.onUsernameChange(
            username = "asd",
            onShowMessage = onShowMessage,
        )

        // then
        viewModel.username.value shouldBe "asd"
    }

    @Test
    fun `username is empty, enabled is set to false`() = runTest {
        // given
        val username = " "

        // when
        viewModel.onUsernameChange(
            username = username,
            onShowMessage = {},
        )

        // then
        viewModel.enabled.testFirst() shouldBe false
    }

    @Test
    fun `username is blank, enabled is set to false`() = runTest {
        // given
        val username = " "


        // when
        viewModel.onUsernameChange(
            username = username,
            onShowMessage = {},
        )

        // then
        viewModel.enabled.testFirst() shouldBe false
    }

    @Test
    fun `username is valid, enabled is set to true`() = runTest {
        // given
        val username = "username"

        // when
        viewModel.onUsernameChange(
            username = username,
            onShowMessage = {},
        )

        // then
        viewModel.enabled.testFirst() shouldBe true
    }

    /*
     If we want to use Turbine while other event is being consumed

       @Test
    fun `username is empty, enabled is false`() = runTest {
        // given
        val username = ""

        // when
        viewModel.enabled.test {
            awaitItem() shouldBe false
            viewModel.onUsernameChange(
                username = username,
                onShowMessage = {},
            )

            expectNoEvents()
        }
    }

    Else, we could have another awaitItem() call

     */

    @Test
    fun `onSaveUsername - correctly updates preferences`() = runTest {
        // given
        val username = "username"

        // when
        viewModel.onSaveUsername(
            username = username,
        )

        // then
        verify {
            preferences.saveToken(username)
        }
    }
}

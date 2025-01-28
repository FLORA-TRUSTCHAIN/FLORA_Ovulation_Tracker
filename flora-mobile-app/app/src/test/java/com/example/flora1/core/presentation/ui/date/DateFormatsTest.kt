package com.example.flora1.core.presentation.ui.date

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import java.time.LocalDate

class DateFormatsTest {

    @Test
    fun `ending date is bigger than starting date`() {
        // given
        val startingDate = LocalDate.now()
        val endingDate = LocalDate.now().minusDays(1)
        val action: (LocalDate) -> Unit = mockk(relaxed = true)

        // when
        val throwable = shouldThrow<Throwable> {
            performActionBetweenTwoDates(startingDate, endingDate, action = action)
        }

        // then
        throwable.shouldBeInstanceOf<IllegalArgumentException>()
        throwable.message shouldBe "Start date should not be bigger than ending date"
    }

    @Test
    fun `ending date is same as starting date`() {
        // given
        val startingDate: LocalDate = LocalDate.now()
        val endingDate = LocalDate.now()
        val action: (LocalDate) -> Unit = mockk(relaxed = true)

        // when
        val throwable = shouldThrow<Throwable> {
            performActionBetweenTwoDates(startingDate, endingDate, action = action)
        }

        // then
        throwable.shouldBeInstanceOf<IllegalArgumentException>()
        throwable.message shouldBe "Start date should not be bigger than ending date"
    }

    @Test
    fun `include last day`() {
        // given
        val daysToAdd: Long = 5
        val startingDate = LocalDate.now()
        val endingDate = LocalDate.now().plusDays(daysToAdd)
        val action: (LocalDate) -> Unit = mockk(relaxed = true)

        // when
        performActionBetweenTwoDates(
            startingDate = startingDate,
            endingDate = endingDate,
            includeLastDay = true,
            action = action
        )

        // then
        repeat(daysToAdd.toInt()) {
            verify {
                action(LocalDate.now().plusDays(daysToAdd))
            }
            /*
            1) Function1(#1).invoke(2024-10-19)
            2) Function1(#1).invoke(2024-10-20)
            3) Function1(#1).invoke(2024-10-21)
            4) Function1(#1).invoke(2024-10-22)
            5) Function1(#1).invoke(2024-10-23)
            6) Function1(#1).invoke(2024-10-24)
             */
        }
    }

    @Test
    fun `don't include last day`() {
        // given
        val daysToAdd: Long = 5
        val startingDate = LocalDate.now()
        val endingDate = LocalDate.now().plusDays(daysToAdd)
        val action: (LocalDate) -> Unit = mockk(relaxed = true)

        // when
        performActionBetweenTwoDates(
            startingDate = startingDate,
            endingDate = endingDate,
            includeLastDay = false,
            action = action
        )

        // then
        repeat(daysToAdd.toInt()) {
            verify {
                action(LocalDate.now().plusDays(daysToAdd - 1))
            }
            /*
            1) Function1(#1).invoke(2024-10-19)
            2) Function1(#1).invoke(2024-10-20)
            3) Function1(#1).invoke(2024-10-21)
            4) Function1(#1).invoke(2024-10-22)
            5) Function1(#1).invoke(2024-10-23)
             */
        }
    }
}

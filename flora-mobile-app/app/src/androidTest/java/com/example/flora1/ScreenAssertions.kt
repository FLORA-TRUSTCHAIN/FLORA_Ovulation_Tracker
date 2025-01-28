package com.example.flora1

import androidx.navigation.NavController
import io.kotest.matchers.shouldBe
import org.junit.Assert

fun NavController.assertCurrentRouteName(expectedRouteName: String) {
    currentBackStackEntry?.destination?.route shouldBe expectedRouteName
}

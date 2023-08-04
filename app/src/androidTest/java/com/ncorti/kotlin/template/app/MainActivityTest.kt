package com.ncorti.kotlin.template.app

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun useAppContext() {
        rule.onNodeWithTag("Input").performClick().performTextInput("5")
        rule.onNodeWithText("COMPUTE").performClick()
        rule.onNodeWithTag("FactorialResult")
            .assertIsDisplayed()
            .assert(androidx.compose.ui.test.hasText("120"))
    }
}

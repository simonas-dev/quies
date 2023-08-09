package dev.simonas.quies

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class AppMainTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsHello() {
        composeTestRule.setContent {
            AppMain(
                state = MainViewModel.State(
                    "Hello!",
                )
            )
        }

        composeTestRule.onNodeWithText("Hello!")
            .assertIsDisplayed()
    }
}

package dev.simonas.quies.router

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import dagger.hilt.android.testing.HiltAndroidTest
import dev.simonas.quies.ComponentTest
import dev.simonas.quies.card.CardScreen
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class RouterScreenTest : ComponentTest() {

    val cardScreen = @Composable {
        Text(text = "Card screen")
    }

    @Test
    fun init() {
        setContent {
            RouterScreen(
                cardScreen = cardScreen,
            )
        }

        cardScreenDisplayed()
    }

    private fun cardScreenDisplayed() {
        onNodeWithText("Card screen")
            .assertIsDisplayed()
    }
}

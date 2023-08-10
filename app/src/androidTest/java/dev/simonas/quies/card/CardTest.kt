package dev.simonas.quies.card

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import dagger.hilt.android.testing.HiltAndroidTest
import dev.simonas.quies.ComponentTest
import org.junit.Test

@HiltAndroidTest
class CardTest : ComponentTest() {

    @Test
    fun init() {
        setContent {
            Card(
                text = "Could you bring me some coffee?",
            )
        }

        showsInitialQuestion()
    }

    @Test
    fun proceedToNextQuestion() {
        setContent {
            Card(text = "Could you bring me some coffee?")
        }

        showsInitialQuestion()
    }

    private fun showsInitialQuestion() {
        onNodeWithText("Could you bring me some coffee?")
            .assertIsDisplayed()
    }
}
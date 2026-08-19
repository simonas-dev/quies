package dev.simonas.quies.card

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import dev.simonas.quies.ComponentTest
import org.junit.Ignore
import org.junit.Test

@Ignore("broken due to LocalUiGuides")
class CardTest : ComponentTest() {

    @Test
    fun init() {
        setContent {
            Card(
                centerText = "Could you bring me some coffee?",
            )
        }

        showsInitialQuestion()
    }

    @Test
    fun proceedToNextQuestion() {
        setContent {
            Card(centerText = "Could you bring me some coffee?")
        }

        showsInitialQuestion()
    }

    private fun showsInitialQuestion() {
        onNodeWithText("Could you bring me some coffee?")
            .assertIsDisplayed()
    }
}
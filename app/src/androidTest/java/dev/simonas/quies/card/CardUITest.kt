package dev.simonas.quies.card

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import dev.simonas.quies.UITest
import dev.simonas.quies.gamesets.GameSetsScreen
import org.junit.Test

@HiltAndroidTest
class CardUITest : UITest() {

    @Test
    fun init() {
        onNodeWithText("DATING")
            .performClick()

        showsCardScreen()
    }

    @Test
    fun clickOnEasyQuestionCard() {
        onNodeWithText("FRIENDSHIP")
            .performClick()
        onNodeWithText("LEVEL 1")
            .performClick()

        showsEasyQuestion()
    }

    @Test
    fun clickOnMediumQuestionCard() {
        onNodeWithText("DATING")
            .performClick()
        onNodeWithText("LEVEL 2")
            .performClick()

        showsMediumQuestion()
    }

    @Test
    fun clickOnHardQuestionCard() {
        onNodeWithText("DEBATE")
            .performClick()
        onNodeWithText("LEVEL 3")
            .performClick()

        showsHardQuestion()
    }

    @Test
    fun closeQuestion() {
        onNodeWithText("DEBATE")
            .performClick()
        onNodeWithText("LEVEL 3")
            .performClick()
        onNodeWithTag(CardScreen.TAG_CLOSE_CARD)
            .performClick()

        showsNextLevelCardAsEasy()
        showsNextCardAsHard()
        showsExit()
    }

    @Test
    fun changeLevel() {
        onNodeWithText("DEBATE")
            .performClick()
        onNodeWithText("LEVEL 3")
            .performClick()
        onNodeWithTag(CardScreen.TAG_CLOSE_CARD)
            .performClick()
        onNodeWithText("LEVEL 1")
            .performClick()

        currentLevelIsEasy()
    }

    @Test
    fun nextQuestion() {
        onNodeWithText("DEBATE")
            .performClick()
        onNodeWithText("LEVEL 3")
            .performClick()
        onNodeWithTag(CardScreen.TAG_CLOSE_CARD)
            .performClick()
        onNodeWithText("LEVEL 3")
            .performClick()

        showsNextHardQuestion()
    }

    @Test
    fun exit() {
        onNodeWithText("DEBATE")
            .performClick()
        onNodeWithText("LEVEL 3")
            .performClick()
        onNodeWithTag(CardScreen.TAG_CLOSE_CARD)
            .performClick()
        onNodeWithTag(CardScreen.TAG_EXIT)
            .performClick()

        showsGameSetScreen()
    }

    private fun showsGameSetScreen() {
        onNodeWithTag(GameSetsScreen.TAG_SCREEN)
            .assertIsDisplayed()
    }

    private fun showsCardScreen() {
        onNodeWithTag(CardScreen.TAG_SCREEN)
            .assertIsDisplayed()
    }

    private fun showsEasyQuestion() {
        onNodeWithText("What's one thing you love about your hometown?")
            .assertIsDisplayed()
    }

    private fun showsMediumQuestion() {
        onNodeWithText("What qualities are you looking for in a friend?")
            .assertIsDisplayed()
    }

    private fun showsHardQuestion() {
        onNodeWithText("Does the influence of technology on social interactions creates social pressures to conform that in turn reduces our individuality and leads to more psychological issues?")
            .assertIsDisplayed()
    }

    private fun showsNextLevelCardAsEasy() {
        onNodeWithText("LEVEL 1")
            .assertIsDisplayed()
    }

    private fun showsNextCardAsHard() {
        onNodeWithText("LEVEL 3")
            .assertIsDisplayed()
    }

    private fun showsExit() {
        onNodeWithTag(CardScreen.TAG_EXIT)
            .assertIsDisplayed()
    }

    private fun currentLevelIsEasy() {
        onNodeWithText("LEVEL 1")
            .assertIsDisplayed()
        onNodeWithText("LEVEL 2")
            .assertIsDisplayed()
    }

    private fun showsNextHardQuestion() {
        onNodeWithText("Is it ethical to introduce human-like consciousness into AI systems?")
            .assertIsDisplayed()
    }
}
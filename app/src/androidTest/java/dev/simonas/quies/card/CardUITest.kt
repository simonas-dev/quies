package dev.simonas.quies.card

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import dev.simonas.quies.UITest
import dev.simonas.quies.gamesets.GameSet
import dev.simonas.quies.gamesets.GameSets
import dev.simonas.quies.gamesets.GameSetsScreen
import javax.inject.Inject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

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

        showsNextLevelCard()
        showsNextCard()
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
        onNodeWithTag(CardScreen.TAG_NEXT_LEVEL)
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
        onNodeWithTag(CardScreen.TAG_NEXT_CARD)
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
        onNodeWithText("When I was a kid, what do you think I wanted to become?")
            .assertIsDisplayed()
    }

    private fun showsMediumQuestion() {
        onNodeWithText("What could bring us closer together?")
            .assertIsDisplayed()
    }

    private fun showsHardQuestion() {
        onNodeWithText("Should the father have a say in the pregnancy?")
            .assertIsDisplayed()
    }

    private fun showsNextLevelCard() {
        onNodeWithTag(CardScreen.TAG_NEXT_LEVEL)
            .assertIsDisplayed()
    }

    private fun showsNextCard() {
        onNodeWithTag(CardScreen.TAG_NEXT_CARD)
            .assertIsDisplayed()
    }

    private fun showsExit() {
        onNodeWithTag(CardScreen.TAG_NEXT_CARD)
            .assertIsDisplayed()
    }

    private fun currentLevelIsEasy() {
        onNodeWithText("LEVEL 1")
            .assertIsDisplayed()
        onNodeWithText("LEVEL 2")
            .assertIsDisplayed()
    }

    private fun showsNextHardQuestion() {
        onNodeWithText("Should laws focus on individual rights or the greater good?")
            .assertIsDisplayed()
    }
}
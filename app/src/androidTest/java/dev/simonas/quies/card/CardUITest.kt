package dev.simonas.quies.card

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import dagger.hilt.android.testing.HiltAndroidTest
import dev.simonas.quies.UITest
import dev.simonas.quies.card.CardScreen2.questionState
import dev.simonas.quies.gamesets.GameSetsScreen
import dev.simonas.quies.utils.manual
import kotlinx.coroutines.delay
import org.junit.Test
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@HiltAndroidTest
internal class CardUITest : UITest() {

    private fun onNodeWithState(state: QuestionComponent.State) =
        onNode(SemanticsMatcher.expectValue(questionState, state))

    @Test
    fun init() {
        onNodeWithText("DATING")
            .performClick()

        showsCardScreen()
        doesNotShowSkipLevelNotice()
    }

    @Test
    fun clickOnEasyQuestionCard() {
        onNodeWithText("FRIENDS")
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
            .performTouchInput {
                click(topCenter)
            }

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
    fun nextQuestion() {
        onNodeWithText("DEBATE")
            .performClick()
        onNodeWithText("LEVEL 3")
            .performClick()
        onNodeWithState(QuestionComponent.State.NextHidden)
            .performClick()
        onNodeWithState(QuestionComponent.State.PrimaryHidden)
            .performClick()

        showsNextHardQuestion()
    }

    @Test
    fun togglesMenu() {
        onNodeWithText("DEBATE")
            .performClick()
        onNodeWithTag(Menu.TAG_MENU)
            .performClick()

        showsExit()
    }

    @Test
    fun exit() {
        onNodeWithText("DEBATE")
            .performClick()
        onNodeWithTag(Menu.TAG_MENU)
            .performClick()
        onNodeWithTag(CardScreen2.TAG_EXIT)
            .performClick()

        showsGameSetScreen()
    }

    @Test
    fun answersSomeQuestions() {
        onNodeWithText("DATING")
            .performClick()
        onNodeWithText("LEVEL 1")
            .performClick()
        mainClock.manual {
            repeat(3) {
                advanceTimeBy(2.minutes.inWholeMilliseconds)
                onNodeWithState(QuestionComponent.State.NextHidden)
                    .performClick()
                advanceTimeBy(1.seconds.inWholeMilliseconds)
                onNodeWithState(QuestionComponent.State.PrimaryHidden)
                    .performClick()
            }

            advanceTimeBy(1.seconds.inWholeMilliseconds)

            showsSkipLevelNotice()
        }
    }

    private fun showsSkipLevelNotice() {
        onNode(SemanticsMatcher.keyIsDefined(Menu.isShowingMessage))
            .assert(SemanticsMatcher.expectValue(Menu.isShowingMessage, true))
    }

    private fun doesNotShowSkipLevelNotice() {
        onNode(SemanticsMatcher.keyIsDefined(Menu.isShowingMessage))
            .assert(SemanticsMatcher.expectValue(Menu.isShowingMessage, false))
    }

    private fun showsGameSetScreen() {
        onNodeWithTag(GameSetsScreen.TAG_SCREEN)
            .assertIsDisplayed()
    }

    private fun showsCardScreen() {
        onNodeWithTag(CardScreen2.TAG_SCREEN)
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

    private fun showsExit() {
        onNodeWithTag(CardScreen2.TAG_EXIT)
            .assertIsDisplayed()
    }

    private fun showsNextHardQuestion() {
        onNodeWithText("Is it ethical to introduce human-like consciousness into AI systems?")
            .assertIsDisplayed()
    }
}

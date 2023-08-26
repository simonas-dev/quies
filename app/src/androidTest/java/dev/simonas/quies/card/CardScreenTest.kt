package dev.simonas.quies.card

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import dev.simonas.quies.ComponentTest
import dev.simonas.quies.data.Question
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@HiltAndroidTest
internal class CardScreenTest : ComponentTest() {

    val questionClosed: (Question) -> Unit = mock()
    val changeLevel: (Question.Level) -> Unit = mock()
    val nextQuestion: (Question.Level) -> Unit = mock()
    val back: () -> Unit = mock()

    val pickingContent: @Composable () -> Unit = {
        CardScreen(
            state = CardViewModel.State.Picking(
                currentLevel = Question.Level.Easy,
                nextLevel = Question.Level.Medium,
            ),
            onNextQuestion = nextQuestion,
            onQuestionClosed = questionClosed,
            onChangeLevel = changeLevel,
            onBack = back,
            prevQuestions = prevQuestions,
        )
    }

    val easyCoffeeQuestion = Question(
        text = "Could you bring me some coffee?",
        level = Question.Level.Easy,
        gameSetIds = emptyList(),
    )

    val showingContent: @Composable () -> Unit = {
        CardScreen(
            state = CardViewModel.State.Showing(
                question = easyCoffeeQuestion,
            ),
            onNextQuestion = nextQuestion,
            onQuestionClosed = questionClosed,
            onChangeLevel = changeLevel,
            onBack = back,
            prevQuestions = prevQuestions,
        )
    }

    val landingContent: @Composable () -> Unit = {
        CardScreen(
            state = CardViewModel.State.Landing,
            onNextQuestion = nextQuestion,
            onQuestionClosed = questionClosed,
            onChangeLevel = changeLevel,
            onBack = back,
            prevQuestions = prevQuestions,
        )
    }

    @Test
    fun init() {
        setContent { landingContent() }
        showsLevels()
    }

    @Test
    fun clicksOnEasyLevel() {
        setContent { landingContent() }
        onNodeWithText("LEVEL 1")
            .performClick()
        mainClock.advanceTimeBy(800)

        requestsNextEasyQuestion()
    }

    @Test
    fun clicksOnMediumLevel() {
        setContent { landingContent() }
        onNodeWithText("LEVEL 2")
            .performClick()
        mainClock.advanceTimeBy(800)

        requestsNextMediumQuestion()
    }

    @Test
    fun clicksOnHardLevel() {
        setContent { landingContent() }
        onNodeWithText("LEVEL 3")
            .performClick()
        mainClock.advanceTimeBy(800)

        requestsNextHardQuestion()
    }

    @Test
    fun clicksOnExit() {
        setContent { pickingContent() }
        onNodeWithTag(CardScreen.TAG_EXIT)
            .performClick()

        requestsBack()
    }

    @Test
    fun clicksOnChangeLevel() {
        setContent { pickingContent() }
        onNodeWithText("LEVEL 2")
            .performClick()

        requestsChangeLevel()
    }

    @Test
    fun clicksOnNextEasyQuestion() {
        setContent { pickingContent() }
        onNodeWithText("LEVEL 1")
            .performClick()
        mainClock.advanceTimeBy(500)

        requestsNextEasyQuestion()
    }

    @Test
    fun closesQuestion() {
        setContent { showingContent() }
        onNodeWithTag(CardScreen.TAG_CLOSE_CARD)
            .performClick()
        mainClock.advanceTimeBy(500)

        requestsCloseQuestion()
    }

    private fun showsLevels() {
        onNodeWithText("LEVEL 1")
            .assertIsDisplayed()
        onNodeWithText("LEVEL 2")
            .assertIsDisplayed()
        onNodeWithText("LEVEL 3")
            .assertIsDisplayed()
    }

    private fun requestsNextEasyQuestion() {
        verify(nextQuestion).invoke(Question.Level.Easy)
    }

    private fun requestsNextMediumQuestion() {
        verify(nextQuestion).invoke(Question.Level.Medium)
    }

    private fun requestsNextHardQuestion() {
        verify(nextQuestion).invoke(Question.Level.Hard)
    }

    private fun requestsBack() {
        verify(back).invoke()
    }

    private fun requestsChangeLevel() {
        verify(changeLevel).invoke(Question.Level.Medium)
    }

    private fun requestsCloseQuestion() {
        verify(questionClosed).invoke(easyCoffeeQuestion)
    }
}
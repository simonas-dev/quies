package dev.simonas.quies.card

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import dev.simonas.quies.ComponentTest
import dev.simonas.quies.questions.Question
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@HiltAndroidTest
internal class CardScreenTest : ComponentTest() {

    val nextQuestion: (Question.Level) -> Unit = mock()
    val back: () -> Unit = mock()

    val content: @Composable () -> Unit = {
        CardScreen(
            state = CardViewModel.State(
                question = Question(
                    id = "",
                    text = "Could you bring me some coffee?",
                    level = Question.Level.Easy,
                    gameSetIds = emptyList(),
                )
            ),
            onNextQuestion = nextQuestion,
            onBack = back,
        )
    }

    @Test
    fun init() {
        setContent { content() }
        showsInitialQuestion()
    }

    @Test
    fun clicksOnEasyLevel() {
        setContent { content() }
        onNodeWithText("1")
            .performClick()

        requestsNextEasyQuestion()
    }

    @Test
    fun clicksOnMediumLevel() {
        setContent { content() }
        onNodeWithText("2")
            .performClick()

        requestsNextMediumQuestion()
    }

    @Test
    fun clicksOtHardLevel() {
        setContent { content() }
        onNodeWithText("3")
            .performClick()

        requestsNextHardQuestion()
    }

    @Test
    fun clicksOnExit() {
        setContent { content() }
        onNodeWithText("exit")
            .performClick()

        requestsBack()
    }

    private fun showsInitialQuestion() {
        onNodeWithText("Could you bring me some coffee?")
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
}
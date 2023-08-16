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
class CardScreenTest : ComponentTest() {

    val nextQuestion: () -> Unit = mock()
    val back: () -> Unit = mock()

    val content: @Composable () -> Unit = {
        CardScreen(
            state = CardViewModel.State(
                question = Question(
                    id = "",
                    text = "Could you bring me some coffee?",
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
    fun clicksOnCard() {
        setContent { content() }
        onNodeWithText("Could you bring me some coffee?")
            .performClick()

        requestsNextQuestion()
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

    private fun requestsNextQuestion() {
        verify(nextQuestion).invoke()
    }

    private fun requestsBack() {
        verify(back).invoke()
    }
}
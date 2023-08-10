package dev.simonas.quies.card

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

    @Test
    fun init() {
        setContent {
            CardScreen(
                state = CardViewModel.State(
                    question = Question(
                        text = "Could you bring me some coffee?",
                    )
                ),
                onNextQuestion = nextQuestion,
            )
        }

        showsInitialQuestion()
    }

    @Test
    fun clicksOnCard() {
        setContent {
            CardScreen(
                state = CardViewModel.State(
                    question = Question(
                        text = "Could you bring me some coffee?",
                    )
                ),
                onNextQuestion = nextQuestion,
            )
        }
        onNodeWithText("Could you bring me some coffee?")
            .performClick()

        requestsNextQuestion()
    }

    private fun showsInitialQuestion() {
        onNodeWithText("Could you bring me some coffee?")
            .assertIsDisplayed()
    }

    private fun requestsNextQuestion() {
        verify(nextQuestion).invoke()
    }
}
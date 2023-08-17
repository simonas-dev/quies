package dev.simonas.quies.card

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import dev.simonas.quies.questions.Question
import dev.simonas.quies.router.NavRoutes
import dev.simonas.quies.utils.testLast
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class CardViewModelTest {

    val gameSetId = "gameSetId"
    val level = Question.Level.Easy
    val nextQuestion = Question(
        id = "",
        text = "Random question?",
        level = Question.Level.Easy,
        gameSetIds = listOf(gameSetId),
    )
    val getNextQuestion: GetNextQuestion = mock {
        on { invoke(gameSetId, level) }.thenReturn(nextQuestion)
    }
    val stateHandle = SavedStateHandle(
        mapOf(
            NavRoutes.ARG_GAME_SET to gameSetId,
        )
    )

    val subject = CardViewModel(
        stateHandle = stateHandle,
        getNextQuestion = getNextQuestion,
    )

    @Test
    fun `shows question`() = runTest {
        subject.state.testLast { state ->
            assertThat(state.question)
                .isEqualTo(nextQuestion)
        }
    }

    @Nested
    inner class `next question` {

        val nextRollerQuestion = Question(
            id = "",
            text = "Next Random question?",
            gameSetIds = listOf(gameSetId),
            level = Question.Level.Hard,
        )

        @BeforeEach
        fun setUp() {
            whenever(getNextQuestion.invoke(gameSetId, Question.Level.Hard))
                .thenReturn(nextRollerQuestion)
        }

        @Test
        fun `shows next question`() = runTest {
            subject.state.testLast { state ->
                assertThat(state.question)
                    .isEqualTo(nextRollerQuestion)
            }
        }
    }
}

package dev.simonas.quies.card

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import dev.simonas.quies.data.Question
import dev.simonas.quies.router.NavRoutes
import dev.simonas.quies.utils.az
import dev.simonas.quies.utils.testLast
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class CardViewModelTest {

    val gameSetId = "gameSetId"
    val getNextQuestion: GetNextQuestion = mock()
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
    fun `shows landing`() = runTest {
        subject.state.testLast { state ->
            assertThat(state)
                .isInstanceOf(CardViewModel.State.Landing::class.java)
        }
    }

    @Nested
    inner class `next question` {

        val nextRollerQuestion = Question(
            text = "Next Random question?",
            gameSetIds = listOf(gameSetId),
            level = Question.Level.Hard,
        )

        @BeforeEach
        fun setUp() {
            whenever(getNextQuestion.invoke(gameSetId, Question.Level.Hard))
                .thenReturn(nextRollerQuestion)
            subject.next(Question.Level.Hard)
        }

        @Test
        fun `shows next question`() = runTest {
            subject.state.testLast { state ->
                assertThat(state.az<CardViewModel.State.Showing>().question)
                    .isEqualTo(nextRollerQuestion)
            }
        }
    }

    @Nested
    inner class `change level` {

        @BeforeEach
        fun setUp() {
            subject.changeLevel(Question.Level.Hard)
        }

        @Test
        fun `updates current level`() = runTest {
            subject.state.testLast { state ->
                assertThat(state.az<CardViewModel.State.Picking>().currentLevel)
                    .isEqualTo(Question.Level.Hard)
            }
        }

        @Test
        fun `updates next level`() = runTest {
            subject.state.testLast { state ->
                assertThat(state.az<CardViewModel.State.Picking>().nextLevel)
                    .isEqualTo(Question.Level.Easy)
            }
        }
    }

    @Nested
    inner class `close question` {

        val closedQuestion = Question(
            text = "",
            level = Question.Level.Easy,
            gameSetIds = emptyList(),
        )

        @BeforeEach
        fun setUp() {
            subject.closed(closedQuestion)
        }

        @Test
        fun `matches current level with closed question`() = runTest {
            subject.state.testLast { state ->
                assertThat(state.az<CardViewModel.State.Picking>().currentLevel)
                    .isEqualTo(closedQuestion.level)
            }
        }

        @Test
        fun `shows next level`() = runTest {
            subject.state.testLast { state ->
                assertThat(state.az<CardViewModel.State.Picking>().nextLevel)
                    .isEqualTo(Question.Level.Medium)
            }
        }
    }
}

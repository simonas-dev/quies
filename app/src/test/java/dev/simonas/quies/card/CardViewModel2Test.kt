package dev.simonas.quies.card

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import dev.simonas.quies.data.Question
import dev.simonas.quies.router.NavRoutes
import dev.simonas.quies.utils.testLast
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.mockito.kotlin.mock

internal class CardViewModel2Test {

    val gameSetId = "gameSet1"

    val easyDeck = listOf(
        Question(
            text = "easy1",
            level = Question.Level.Easy,
            levelDescription = "",
            gameSetIds = listOf(gameSetId),
        ),
        Question(
            text = "easy2",
            level = Question.Level.Easy,
            levelDescription = "",
            gameSetIds = listOf(gameSetId),
        ),
    )

    val medDeck = listOf(
        Question(
            text = "med1",
            level = Question.Level.Medium,
            levelDescription = "",
            gameSetIds = listOf(gameSetId),
        ),
        Question(
            text = "med2",
            level = Question.Level.Medium,
            levelDescription = "",
            gameSetIds = listOf(gameSetId),
        ),
    )

    val hardDeck = listOf(
        Question(
            text = "hard1",
            level = Question.Level.Hard,
            levelDescription = "",
            gameSetIds = listOf(gameSetId),
        ),
        Question(
            text = "hard2",
            level = Question.Level.Hard,
            levelDescription = "",
            gameSetIds = listOf(gameSetId),
        ),
    )

    val shuffleQuestionDeck: ShuffleQuestionDeck = mock {
        on { invoke(gameSetId, Question.Level.Easy) }.thenReturn(easyDeck)
        on { invoke(gameSetId, Question.Level.Medium) }.thenReturn(medDeck)
        on { invoke(gameSetId, Question.Level.Hard) }.thenReturn(hardDeck)
    }

    val subject = CardViewModel2(
        stateHandle = SavedStateHandle(
            mapOf(
                NavRoutes.ARG_GAME_SET to gameSetId,
            )
        ),
        shuffleQuestionDeck = shuffleQuestionDeck,
    )

    @Test
    fun `easy level card in landing state`() = runTest {
        subject.questions.testLast { state ->
            val actual = state.components.find { it.text == "easy1" }
            assertThat(actual?.state)
                .isEqualTo(QuestionComponent.State.Landing)
        }
    }

    @Test
    fun `medium level card in landing state`() = runTest {
        subject.questions.testLast { state ->
            val actual = state.components.find { it.text == "med1" }
            assertThat(actual?.state)
                .isEqualTo(QuestionComponent.State.Landing)
        }
    }

    @Test
    fun `hard level card in landing state`() = runTest {
        subject.questions.testLast { state ->
            val actual = state.components.find { it.text == "hard1" }
            assertThat(actual?.state)
                .isEqualTo(QuestionComponent.State.Landing)
        }
    }

    @Nested
    inner class `picks an easy level` {

        @BeforeEach
        fun setUp() {
            val easyCard = subject.questions.value.components.first { it.text == "easy1" }
            subject.trigger(easyCard)
        }

        @Test
        fun `easy level card moves from landing to revealed state`() = runTest {
            subject.questions.testLast { state ->
                val actual = state.components.find { it.text == "easy1" }
                assertThat(actual?.stateVector?.from)
                    .isEqualTo(QuestionComponent.State.Landing)
                assertThat(actual?.stateVector?.to)
                    .isEqualTo(QuestionComponent.State.PrimaryRevealed)
            }
        }

        @Test
        fun `another easy level added that's moving from landing to next hidden state`() = runTest {
            subject.questions.testLast { state ->
                val actual = state.components.find { it.text == "easy2" }
                assertThat(actual?.stateVector?.from)
                    .isEqualTo(QuestionComponent.State.Landing)
                assertThat(actual?.stateVector?.to)
                    .isEqualTo(QuestionComponent.State.NextHidden)
            }
        }

        @Test
        fun `medium level card moves from landing to other card state`() = runTest {
            subject.questions.testLast { state ->
                val actual = state.components.find { it.text == "med1" }
                assertThat(actual?.stateVector?.from)
                    .isEqualTo(QuestionComponent.State.Landing)
                assertThat(actual?.stateVector?.to)
                    .isEqualTo(QuestionComponent.State.OtherCard)
            }
        }

        @Test
        fun `hard level card moves from landing to other card state`() = runTest {
            subject.questions.testLast { state ->
                val actual = state.components.find { it.text == "hard1" }
                assertThat(actual?.stateVector?.from)
                    .isEqualTo(QuestionComponent.State.Landing)
                assertThat(actual?.stateVector?.to)
                    .isEqualTo(QuestionComponent.State.OtherCard)
            }
        }
    }
}

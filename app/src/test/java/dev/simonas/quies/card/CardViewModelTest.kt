package dev.simonas.quies.card

import com.google.common.truth.Truth.assertThat
import dev.simonas.quies.questions.Question
import dev.simonas.quies.questions.QuestionRoller
import dev.simonas.quies.utils.testLast
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class CardViewModelTest {

    val rollerQuestion = Question(
        text = "Random question?"
    )
    val questionRoller: QuestionRoller = mock {
        on { roll() }.thenReturn(rollerQuestion)
    }

    val subject = CardViewModel(
        questionRoller = questionRoller,
    )

    @Test
    fun `shows question`() = runTest {
        subject.state.testLast { state ->
            assertThat(state.question)
                .isEqualTo(rollerQuestion)
        }
    }

    @Nested
    inner class `next question` {

        val nextRollerQuestion = Question(
            text = "Next Random question?"
        )

        @BeforeEach
        fun setUp() {
            whenever(questionRoller.roll())
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

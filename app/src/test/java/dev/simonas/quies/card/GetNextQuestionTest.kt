package dev.simonas.quies.card

import com.google.common.truth.Truth.assertThat
import dev.simonas.quies.questions.Question
import dev.simonas.quies.questions.QuestionRepository
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.random.Random

internal class GetNextQuestionTest {

    val questionRepository: QuestionRepository = mock()
    val random: Random = mock()

    val subject = GetNextQuestion(
        random = random,
        questionRepository = questionRepository,
    )

    @Test
    fun `returns question from expected game set`() {
        val expectedQuestion = Question(
            id = "1",
            text = "hello?",
            level = Question.Level.Easy,
            gameSetIds = listOf("1"),
        )
        val question = expectedQuestion.copy(
            gameSetIds = listOf("2"),
        )
        whenever(questionRepository.getAll())
            .thenReturn(listOf(expectedQuestion, question))

        val actualQuestion = subject.invoke("1", level = Question.Level.Easy)
        assertThat(actualQuestion)
            .isEqualTo(expectedQuestion)
    }

    @Test
    fun `returns question from expected level`() {
        val expectedQuestion = Question(
            id = "1",
            text = "hello?",
            level = Question.Level.Medium,
            gameSetIds = listOf("1"),
        )
        val question = expectedQuestion.copy(
            level = Question.Level.Hard,
        )
        whenever(questionRepository.getAll())
            .thenReturn(listOf(expectedQuestion, question))

        val actualQuestion = subject.invoke("1", level = Question.Level.Medium)
        assertThat(actualQuestion)
            .isEqualTo(expectedQuestion)
    }
}

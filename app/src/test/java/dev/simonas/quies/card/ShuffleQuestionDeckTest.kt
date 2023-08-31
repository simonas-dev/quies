package dev.simonas.quies.card

import com.google.common.truth.Truth.assertThat
import dev.simonas.quies.data.Question
import dev.simonas.quies.data.QuestionRepository
import dev.simonas.quies.utils.SeqRandom
import org.junit.Test
import org.mockito.kotlin.mock

internal class ShuffleQuestionDeckTest {

    val random = SeqRandom()
    val questions = listOf(
        Question(
            text = "a",
            level = Question.Level.Easy,
            levelDescription = "",
            gameSetIds = listOf("1"),
        ),
        Question(
            text = "b",
            level = Question.Level.Easy,
            levelDescription = "",
            gameSetIds = listOf("1"),
        ),
        Question(
            text = "c",
            level = Question.Level.Easy,
            levelDescription = "",
            gameSetIds = listOf("2"),
        ),
        Question(
            text = "d",
            level = Question.Level.Easy,
            levelDescription = "",
            gameSetIds = listOf("2"),
        ),
        Question(
            text = "e",
            level = Question.Level.Easy,
            levelDescription = "",
            gameSetIds = listOf("3"),
        ),
        Question(
            text = "f",
            level = Question.Level.Hard,
            levelDescription = "",
            gameSetIds = listOf("3"),
        ),
        Question(
            text = "g",
            level = Question.Level.Hard,
            levelDescription = "",
            gameSetIds = listOf("3"),
        ),
    )
    val questionRepository: QuestionRepository = mock {
        on { getAll() }.thenReturn(questions)
    }

    val subject = ShuffleQuestionDeck(
        random = random,
        questionRepository = questionRepository,
    )

    @Test
    fun `returns question from expected game set`() {
        random.reset()
        val actualQuestions = subject.invoke("1", level = Question.Level.Easy)
        assertThat(actualQuestions.map { it.text })
            .isEqualTo(listOf("a", "b"))
    }

    @Test
    fun `returns question from expected level`() {
        random.reset()
        val actualQuestions = subject.invoke("3", level = Question.Level.Hard)
        assertThat(actualQuestions.map { it.text })
            .isEqualTo(listOf("f", "g"))
    }
}

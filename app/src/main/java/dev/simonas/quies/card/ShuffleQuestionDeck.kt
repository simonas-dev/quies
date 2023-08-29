package dev.simonas.quies.card

import dev.simonas.quies.data.Question
import dev.simonas.quies.data.QuestionRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
internal class ShuffleQuestionDeck @Inject constructor(
    private val random: Random,
    private val questionRepository: QuestionRepository,
) {

    fun invoke(
        gameSetId: String,
        level: Question.Level,
    ): List<Question> {
        val questions = questionRepository.getAll()
            .filter { question ->
                question.level == level &&
                    question.gameSetIds.contains(gameSetId)
            }
        val sortedQuestions = buildList {
            repeat(questions.size) {
                val index = random.nextInt(questions.size)
                add(questions[index])
            }
        }
        return sortedQuestions
    }
}

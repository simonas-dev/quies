package dev.simonas.quies.card

import dev.simonas.quies.data.Question
import dev.simonas.quies.data.QuestionRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlin.random.Random

@SingleIn(AppScope::class)
@Inject
internal class GetNextQuestion(
    private val random: Random,
    private val questionRepository: QuestionRepository,
) {

    fun invoke(
        gameSetId: String,
        level: Question.Level,
    ): Question {
        return questionRepository.getAll()
            .filter { question ->
                question.level == level &&
                    question.gameSetIds.contains(gameSetId)
            }
            .random(random)
    }
}

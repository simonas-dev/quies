package dev.simonas.quies.card

import dev.simonas.quies.questions.Question
import dev.simonas.quies.questions.QuestionRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
internal class GetNextQuestion @Inject constructor(
    private val random: Random,
    private val questionRepository: QuestionRepository,
) {

    fun invoke(
        gameSetId: String,
    ): Question {
        return questionRepository.getAll()
            .filter { it.gameSetIds.contains(gameSetId) }
            .random(random)
    }
}

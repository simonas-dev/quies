package dev.simonas.quies.questions

import javax.inject.Inject
import kotlin.random.Random

internal interface QuestionRoller {
    fun roll(): Question
}

internal class RandomQuestionRoller @Inject constructor(
    private val random: Random,
    private val questionRepository: QuestionRepository,
) : QuestionRoller {

    override fun roll(): Question = questionRepository
        .getAll()
        .random(random)
}

package dev.simonas.quies.questions

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface QuestionsModule {

    @Binds
    fun questionRepository(
        repository: LocalQuestionRepository
    ): QuestionRepository

    @Binds
    fun randomQuestionRoller(
        repository: RandomQuestionRoller
    ): QuestionRoller
}

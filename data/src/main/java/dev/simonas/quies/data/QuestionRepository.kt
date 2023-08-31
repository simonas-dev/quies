package dev.simonas.quies.data

data class Question(
    val text: String,
    val level: Level,
    val levelDescription: String,
    val gameSetIds: List<String>,
) {
    enum class Level {
        Easy,
        Medium,
        Hard,
    }
}

interface QuestionRepository {
    fun getAll(): List<Question>
}

class SourceQuestionsRepository(
    private val dataSource: DataSource,
) : QuestionRepository {
    private val questions: List<Question> by lazy {
        createQuestions()
    }

    override fun getAll(): List<Question> = questions

    private fun createQuestions(): List<Question> {
        return dataSource.get().gameSets.flatMap { set ->
            listOf(
                set.level1.questions.map { question ->
                    Question(
                        text = question,
                        level = Question.Level.Easy,
                        levelDescription = set.level1.description,
                        gameSetIds = listOf(set.id),
                    )
                },
                set.level2.questions.map { question ->
                    Question(
                        text = question,
                        level = Question.Level.Medium,
                        levelDescription = set.level2.description,
                        gameSetIds = listOf(set.id),
                    )
                },
                set.level3.questions.map { question ->
                    Question(
                        text = question,
                        level = Question.Level.Hard,
                        levelDescription = set.level3.description,
                        gameSetIds = listOf(set.id),
                    )
                }
            )
        }.flatten()
    }
}

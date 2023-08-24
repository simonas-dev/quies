package dev.simonas.quies.data

data class Question(
    val text: String,
    val level: Level,
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
                Question.Level.Easy to set.level1,
                Question.Level.Medium to set.level2,
                Question.Level.Hard to set.level3
            ).flatMap { (level, questions) ->
                questions.map { question ->
                    Question(
                        text = question,
                        level = level,
                        gameSetIds = listOf(set.id),
                    )
                }
            }
        }
    }
}

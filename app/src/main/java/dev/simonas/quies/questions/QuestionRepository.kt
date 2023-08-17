package dev.simonas.quies.questions

import dev.simonas.quies.gamesets.GameSets
import dev.simonas.quies.questions.Question.Level
import javax.inject.Inject

internal data class Question(
    val id: String,
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

internal interface QuestionRepository {
    fun getAll(): List<Question>
}

internal class LocalQuestionRepository @Inject constructor() : QuestionRepository {

    @Suppress("LongMethod")
    override fun getAll(): List<Question> = listOf(
        Question(
            id = "1",
            text = "When I was a kid, what do you think I wanted to become?",
            level = Level.Easy,
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
                GameSets.GAME_SET_FRIENDSHIP,
            ),
        ),
        Question(
            id = "2",
            text = "What could bring us closer together?",
            level = Level.Medium,
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
                GameSets.GAME_SET_FRIENDSHIP,
            ),
        ),
        Question(
            id = "3",
            text = "What aspects of me pique your curiosity?",
            level = Level.Medium,
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
            ),
        ),
        Question(
            id = "4",
            text = "What does my body language right now?",
            level = Level.Medium,
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
            ),
        ),
        Question(
            id = "5",
            text = "Am I someone who loves mornings or prefers staying up late?",
            level = Level.Easy,
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
                GameSets.GAME_SET_FRIENDSHIP,
            ),
        ),
        Question(
            id = "6",
            level = Level.Easy,
            text = "If there's a reality show I'd binge-watch, " +
                "which one do you imagine it would be?",
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
                GameSets.GAME_SET_FRIENDSHIP,
            ),
        ),
        Question(
            id = "7",
            text = "On a scale from 1 to 10, how tidy do you think my rooms is?",
            level = Level.Easy,
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
                GameSets.GAME_SET_FRIENDSHIP,
            ),
        ),
        Question(
            id = "8",
            text = "What kind of vibe does my Instagram give you about me?",
            level = Level.Medium,
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
                GameSets.GAME_SET_FRIENDSHIP,
            ),
        ),
        Question(
            id = "9",
            text = "How inclined am I to go camping?",
            level = Level.Easy,
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
                GameSets.GAME_SET_FRIENDSHIP,
            ),
        ),
        Question(
            id = "10",
            text = "Would I be the type to tattoo someone's name on myself?",
            level = Level.Medium,
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
                GameSets.GAME_SET_FRIENDSHIP,
            ),
        ),
        Question(
            id = "11",
            text = "What aspect of my job do you think is the most challenging?",
            level = Level.Medium,
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
                GameSets.GAME_SET_FRIENDSHIP,
            ),
        ),
        Question(
            id = "12",
            text = "Do you think I am usually late to events?",
            level = Level.Easy,
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
            ),
        ),
        Question(
            id = "13",
            text = "What was your initial impression of me?",
            level = Level.Medium,
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
                GameSets.GAME_SET_FRIENDSHIP,
            ),
        ),
        Question(
            id = "14",
            text = "Do I strike you as more of a coffee or tea person?",
            level = Level.Easy,
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
            ),
        ),
        Question(
            id = "15",
            text = "Any guesses on my go-to karaoke song?",
            level = Level.Easy,
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
                GameSets.GAME_SET_FRIENDSHIP,
            ),
        ),
        Question(
            id = "16",
            text = "What about me is the quirkiest or least familiar to you?",
            level = Level.Medium,
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
                GameSets.GAME_SET_FRIENDSHIP,
            ),
        ),
        Question(
            id = "17",
            text = "Should the father have a say in the pregnancy?",
            level = Level.Hard,
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
                GameSets.GAME_SET_FRIENDSHIP,
                GameSets.GAME_SET_DEBATE,
            ),
        ),
        Question(
            id = "18",
            text = "Should laws focus on individual rights or the greater good?",
            level = Level.Hard,
            gameSetIds = listOf(
                GameSets.GAME_SET_DEBATE,
            ),
        ),
        Question(
            id = "19",
            text = "What's the most important invention of the past century?",
            level = Level.Easy,
            gameSetIds = listOf(
                GameSets.GAME_SET_DEBATE,
            ),
        ),
        Question(
            id = "20",
            text = "Should development of artificial intelligence be banned?",
            level = Level.Hard,
            gameSetIds = listOf(
                GameSets.GAME_SET_DEBATE,
            ),
        ),
        Question(
            id = "21",
            text = "Should sale of human organs should be legalized?",
            level = Level.Hard,
            gameSetIds = listOf(
                GameSets.GAME_SET_DEBATE,
            ),
        ),
        Question(
            id = "22",
            text = "Should social media platforms have the authority to" +
                "censor or fact-check content posted by their users?",
            level = Level.Hard,
            gameSetIds = listOf(
                GameSets.GAME_SET_DEBATE,
            ),
        ),
        Question(
            id = "23",
            text = "Should voting be encouraged by monetary gains in democratic societies?",
            level = Level.Hard,
            gameSetIds = listOf(
                GameSets.GAME_SET_DEBATE,
            ),
        ),
        Question(
            id = "24",
            text = "How do you feel when you’re around me?",
            level = Level.Hard,
            gameSetIds = listOf(
                GameSets.GAME_SET_FRIENDSHIP,
                GameSets.GAME_SET_DATING,
            ),
        ),
        Question(
            id = "25",
            text = "What’s a fear you’ve never shared with me?",
            level = Level.Hard,
            gameSetIds = listOf(
                GameSets.GAME_SET_FRIENDSHIP,
                GameSets.GAME_SET_DATING,
            ),
        ),
    )
}

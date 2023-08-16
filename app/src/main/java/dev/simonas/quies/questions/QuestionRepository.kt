package dev.simonas.quies.questions

import dev.simonas.quies.gamesets.GameSets
import javax.inject.Inject

internal data class Question(
    val id: String,
    val text: String,
    val gameSetIds: List<String>,
)

internal interface QuestionRepository {
    fun getAll(): List<Question>
}

internal class LocalQuestionRepository @Inject constructor() : QuestionRepository {

    @Suppress("LongMethod")
    override fun getAll(): List<Question> = listOf(
        Question(
            id = "1",
            text = "When I was a kid, what do you think I wanted to become?",
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
                GameSets.GAME_SET_FRIENDSHIP,
            ),
        ),
        Question(
            id = "2",
            text = "What could bring us closer together?",
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
                GameSets.GAME_SET_FRIENDSHIP,
            ),
        ),
        Question(
            id = "3",
            text = "What aspects of me pique your curiosity?",
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
            ),
        ),
        Question(
            id = "4",
            text = "What does my body language right now?",
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
            ),
        ),
        Question(
            id = "5",
            text = "Am I someone who loves mornings or prefers staying up late?",
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
                GameSets.GAME_SET_FRIENDSHIP,
            ),
        ),
        Question(
            id = "6",
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
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
                GameSets.GAME_SET_FRIENDSHIP,
            ),
        ),
        Question(
            id = "8",
            text = "What kind of vibe does my Instagram give you about me?",
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
                GameSets.GAME_SET_FRIENDSHIP,
            ),
        ),
        Question(
            id = "9",
            text = "How inclined am I to go camping?",
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
                GameSets.GAME_SET_FRIENDSHIP,
            ),
        ),
        Question(
            id = "10",
            text = "Would I be the type to tattoo someone's name on myself?",
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
                GameSets.GAME_SET_FRIENDSHIP,
            ),
        ),
        Question(
            id = "11",
            text = "What aspect of my job do you think is the most challenging?",
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
                GameSets.GAME_SET_FRIENDSHIP,
            ),
        ),
        Question(
            id = "12",
            text = "Do you think I am usually late to events?",
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
            ),
        ),
        Question(
            id = "13",
            text = "What was your initial impression of me?",
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
                GameSets.GAME_SET_FRIENDSHIP,
            ),
        ),
        Question(
            id = "14",
            text = "Do I strike you as more of a coffee or tea person?",
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
            ),
        ),
        Question(
            id = "15",
            text = "Any guesses on my go-to karaoke song?",
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
                GameSets.GAME_SET_FRIENDSHIP,
            ),
        ),
        Question(
            id = "16",
            text = "What about me is the quirkiest or least familiar to you?",
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
                GameSets.GAME_SET_FRIENDSHIP,
            ),
        ),
        Question(
            id = "17",
            text = "What about me is the quirkiest or least familiar to you?",
            gameSetIds = listOf(
                GameSets.GAME_SET_DATING,
                GameSets.GAME_SET_FRIENDSHIP,
            ),
        ),
        Question(
            id = "18",
            text = "Should the father have a say in the pregnancy?",
            gameSetIds = listOf(
                GameSets.GAME_SET_DEBATE,
            ),
        ),
        Question(
            id = "19",
            text = "Should laws focus on individual rights or the greater good?",
            gameSetIds = listOf(
                GameSets.GAME_SET_DEBATE,
            ),
        ),
        Question(
            id = "20",
            text = "What's the most important invention of the past century?",
            gameSetIds = listOf(
                GameSets.GAME_SET_DEBATE,
            ),
        ),
        Question(
            id = "21",
            text = "What's the most important invention of the past century?",
            gameSetIds = listOf(
                GameSets.GAME_SET_DEBATE,
            ),
        ),
        Question(
            id = "22",
            text = "Should development of artificial intelligence be banned?",
            gameSetIds = listOf(
                GameSets.GAME_SET_DEBATE,
            ),
        ),
        Question(
            id = "23",
            text = "Should sale of human organs should be legalized?",
            gameSetIds = listOf(
                GameSets.GAME_SET_DEBATE,
            ),
        ),
        Question(
            id = "24",
            text = "Should social media platforms have the authority " +
                "to censor or fact-check content posted by their users?",
            gameSetIds = listOf(
                GameSets.GAME_SET_DEBATE,
            ),
        ),
        Question(
            id = "25",
            text = "Should voting be encouraged by monetary gains in democratic societies?",
            gameSetIds = listOf(
                GameSets.GAME_SET_DEBATE,
            ),
        ),
    )
}

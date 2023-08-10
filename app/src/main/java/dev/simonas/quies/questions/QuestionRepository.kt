package dev.simonas.quies.questions

import javax.inject.Inject

internal data class Question(
    val text: String,
)

internal interface QuestionRepository {
    fun getAll(): List<Question>
}

internal class LocalQuestionRepository @Inject constructor() : QuestionRepository {

    private val questions = listOf(
        Question(
            text = "When I was a kid, what do you think I wanted to become?",
        ),
        Question(
            text = "What could bring us closer together?",
        ),
        Question(
            text = "What aspects of me pique your curiosity?",
        ),
        Question(
            text = "What does my body language right now?",
        ),
        Question(
            text = "Am I someone who loves mornings or prefers staying up late?",
        ),
        Question(
            text = "If there's a reality show I'd binge-watch, which one do you imagine it would be?",
        ),
        Question(
            text = "On a scale from 1 to 10, how tidy do you think my rooms is?",
        ),
        Question(
            text = "What kind of vibe does my Instagram give you about me?",
        ),
        Question(
            text = "How inclined am I to go camping?",
        ),
        Question(
            text = "Would I be the type to tattoo someone's name on myself?",
        ),
        Question(
            text = "What aspect of my job do you think is the most challenging?",
        ),
        Question(
            text = "Do you think I am usually late to events?",
        ),
        Question(
            text = "What was your initial impression of me?",
        ),
        Question(
            text = "Do I strike you as more of a coffee or tea person?",
        ),
        Question(
            text = "Any guesses on my go-to karaoke song?",
        ),
        Question(
            text = "What about me is the quirkiest or least familiar to you?",
        ),
    )

    override fun getAll(): List<Question> = questions
}

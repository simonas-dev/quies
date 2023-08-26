package dev.simonas.quies.card

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.simonas.quies.data.Question
import dev.simonas.quies.router.NavRoutes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class CardViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val getNextQuestion: GetNextQuestion,
) : ViewModel() {

    private val gameSetId: String = requireNotNull(stateHandle[NavRoutes.ARG_GAME_SET])

    private val _previousQuestions = MutableStateFlow(emptyList<Question>())

    private val _state = MutableStateFlow<State>(State.Landing)

    val state: StateFlow<State> = _state
    val previousQuestions: StateFlow<List<Question>> = _previousQuestions

    fun closed(question: Question) {
        _previousQuestions.update {
            it.plus(question)
        }
        changeLevel(
            level = question.level,
        )
    }

    fun next(level: Question.Level) {
        val nextQuestion = getNextQuestion.invoke(
            gameSetId = gameSetId,
            level = level,
        )
        _state.value = State.Showing(
            question = nextQuestion
        )
    }

    private fun getNextLevel(currentLevel: Question.Level): Question.Level =
        when (currentLevel) {
            Question.Level.Easy -> Question.Level.Medium
            Question.Level.Medium -> Question.Level.Hard
            Question.Level.Hard -> Question.Level.Easy
        }

    fun changeLevel(level: Question.Level) {
        _state.value = State.Picking(
            currentLevel = level,
            nextLevel = getNextLevel(level),
        )
    }

    sealed class State {

        object Landing : State()

        data class Showing(
            val question: Question,
        ) : State()

        data class Picking(
            val currentLevel: Question.Level,
            val nextLevel: Question.Level,
        ) : State()
    }
}

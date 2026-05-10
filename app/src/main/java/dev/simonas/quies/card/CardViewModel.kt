package dev.simonas.quies.card

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.createSavedStateHandle
import dev.simonas.quies.data.Question
import dev.simonas.quies.router.NavRoutes
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactoryKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@AssistedInject
internal class CardViewModel(
    @Assisted stateHandle: SavedStateHandle,
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

    @AssistedFactory
    @ViewModelAssistedFactoryKey(CardViewModel::class)
    @ContributesIntoMap(AppScope::class)
    fun interface Factory : ViewModelAssistedFactory {
        override fun create(extras: CreationExtras): CardViewModel =
            create(extras.createSavedStateHandle())

        fun create(stateHandle: SavedStateHandle): CardViewModel
    }
}

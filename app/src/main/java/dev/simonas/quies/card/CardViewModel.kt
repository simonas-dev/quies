package dev.simonas.quies.card

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.simonas.quies.questions.Question
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

    private val _state = MutableStateFlow(
        State(
            question = getNextQuestion.invoke(gameSetId),
        )
    )

    val state: StateFlow<State> = _state

    fun next() {
        val nextQuestion = getNextQuestion.invoke(gameSetId)
        _state.update {
            it.copy(
                question = nextQuestion,
            )
        }
    }

    data class State(
        val question: Question,
    )
}

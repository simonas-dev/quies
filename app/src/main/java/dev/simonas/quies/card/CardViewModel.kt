package dev.simonas.quies.card

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.simonas.quies.questions.Question
import dev.simonas.quies.questions.QuestionRoller
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class CardViewModel @Inject constructor(
    private val questionRoller: QuestionRoller,
) : ViewModel() {

    private val _state = MutableStateFlow(
        State(
            question = questionRoller.roll(),
        )
    )

    val state: StateFlow<State> = _state

    fun next() {
        val nextQuestion = questionRoller.roll()
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

package dev.simonas.quies.card

import androidx.compose.runtime.State
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.simonas.quies.data.Question
import dev.simonas.quies.router.NavRoutes
import dev.simonas.quies.utils.Vector
import dev.simonas.quies.utils.moveToBack
import dev.simonas.quies.utils.moveToFront
import dev.simonas.quies.utils.popFirstOrNull
import dev.simonas.quies.utils.replace
import dev.simonas.quies.utils.replaceEach
import dev.simonas.quies.utils.replaceFirst
import dev.simonas.quies.utils.tryReplaceFirst
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class CardViewModel2 @Inject constructor(
    stateHandle: SavedStateHandle,
    private val shuffleQuestionDeck: ShuffleQuestionDeck,
) : ViewModel() {

    val gameSetId: String = requireNotNull(stateHandle[NavRoutes.ARG_GAME_SET])

    private val pool = listOf(
        Question.Level.Easy,
        Question.Level.Medium,
        Question.Level.Hard,
    ).flatMap { level -> shuffleQuestionDeck.invoke(gameSetId, level) }
        .toMutableList()

    private val _questions = MutableStateFlow(
        value = Questions(
            components = listOfNotNull(
                pool.popFirstOrNull { it.level == Question.Level.Easy },
                pool.popFirstOrNull { it.level == Question.Level.Medium },
                pool.popFirstOrNull { it.level == Question.Level.Hard },
            ).map { q ->
                q.withState(QuestionComponent.State.Landing)
            },
        ),
    )

    val questions: StateFlow<Questions> = _questions

    private val _isExitShown = MutableStateFlow(false)
    val isExitShown: StateFlow<Boolean> = _isExitShown

    fun trigger(question: QuestionComponent) {
        when (question.state) {
            QuestionComponent.State.Landing -> {
                selectLevel(question)
            }
            QuestionComponent.State.PrimaryRevealed -> {
                closePrimaryCard(question)
            }
            QuestionComponent.State.OtherCard -> {
                revealPrimaryCard()
            }
            QuestionComponent.State.PrimaryHidden -> {
                revealPrimaryCard()
            }
            QuestionComponent.State.Closed -> {
                showClosedCard(question)
            }
            QuestionComponent.State.NextHidden -> {
                closePrimaryCard(question)
            }
            QuestionComponent.State.Disabled -> {
                // nothing
            }
            QuestionComponent.State.Offscreen -> {
                // nothing
            }
        }
    }

    data class Questions(
        val components: List<QuestionComponent>,
    )

    private fun selectLevel(question: QuestionComponent) {
        val levelsToRemove: List<Question.Level> = when (question.level) {
            QuestionComponent.Level.Easy -> {
                listOf()
            }
            QuestionComponent.Level.Medium -> {
                listOf(Question.Level.Easy)
            }
            QuestionComponent.Level.Hard -> {
                listOf(Question.Level.Easy, Question.Level.Medium)
            }
        }
        pool.removeAll { it.level in levelsToRemove }

        _questions.updateComponents {
            replaceEach { _, q ->
                when {
                    q.id == question.id -> {
                        q.mutate(QuestionComponent.State.PrimaryRevealed)
                    }
                    q.level.toAnother() in levelsToRemove -> {
                        q.mutate(QuestionComponent.State.Disabled)
                    }
                    else -> {
                        q.mutate(QuestionComponent.State.OtherCard)
                    }
                }
            }
            val nextQuestion = pool.popFirstOrNull { it.level.toAnother() == question.level }
            if (nextQuestion != null) {
                add(
                    index = 1,
                    element = nextQuestion
                        .withState(QuestionComponent.State.Landing)
                        .mutate(QuestionComponent.State.NextHidden),
                )
            }
        }
    }

    private fun showClosedCard(question: QuestionComponent) {
        _questions.updateComponents {
            val indexOfRevealed = indexOfFirst { q ->
                q.state == QuestionComponent.State.PrimaryRevealed
            }

            if (indexOfRevealed != -1) {
                val primary = get(indexOfRevealed)
                set(indexOfRevealed, primary.mutate(QuestionComponent.State.Closed))
                if (primary.stateVector.from == QuestionComponent.State.Closed) {
                    moveToBack(indexOfRevealed)
                } else {
                    moveToFront(indexOfRevealed)
                }
            }

            replaceEach { _, q ->
                if (q.state == QuestionComponent.State.PrimaryHidden) {
                    q.mutate(QuestionComponent.State.NextHidden)
                } else {
                    q
                }
            }

            replaceFirst(
                first = { q -> q.id == question.id },
                replace = { q -> q.mutate(QuestionComponent.State.PrimaryRevealed) }
            )
        }
    }

    private fun revealPrimaryCard() {
        _questions.updateComponents {
            replaceFirst(
                first = { q -> q.state == QuestionComponent.State.PrimaryHidden },
                replace = { q -> q.mutate(QuestionComponent.State.PrimaryRevealed) }
            )
            tryReplaceFirst(
                first = { q -> q.state == QuestionComponent.State.OtherCard },
                replace = { q -> q.mutate(QuestionComponent.State.NextHidden) },
            )
        }
    }

    private fun closePrimaryCard(question: QuestionComponent) {
        _questions.updateComponents {
            val indexOfRevealed = indexOfFirst { it.state == QuestionComponent.State.PrimaryRevealed }
            if (indexOfRevealed != -1) {
                val primary = get(indexOfRevealed)
                set(indexOfRevealed, primary.mutate(QuestionComponent.State.Closed))
                if (primary.stateVector.from == QuestionComponent.State.Closed) {
                    moveToBack(indexOfRevealed)
                } else {
                    moveToFront(indexOfRevealed)
                }
            }
            val indexOfNext = indexOfFirst { it.state == QuestionComponent.State.NextHidden }
            if (indexOfNext != -1) {
                replace(
                    index = indexOfNext,
                    replace = { q -> q.mutate(QuestionComponent.State.PrimaryHidden) },
                )
                moveToFront(indexOfNext)
            }
            val indexOfOther = indexOfFirst { q ->
                q.state == QuestionComponent.State.OtherCard && q.level == question.level
            }
            if (indexOfOther == -1) {
                val nextQ = pool.popFirstOrNull { q -> q.level.toAnother() == question.level }
                    ?: pool.popFirstOrNull { true }
                if (nextQ != null) {
                    val q = nextQ.withState(QuestionComponent.State.OtherCard)
                    add(1, q)
                }
            }
        }
    }

    fun toggleMenu() {
        val isMenuVisible = !_isExitShown.value
        _isExitShown.update { isMenuVisible }
        _questions.updateComponents {
            replaceEach { index, questionComponent ->
                if (isMenuVisible) {
                    if (questionComponent.state != QuestionComponent.State.Disabled) {
                        questionComponent.mutate(QuestionComponent.State.Offscreen)
                    } else {
                        questionComponent
                    }
                } else {
                    if (questionComponent.state != QuestionComponent.State.Disabled) {
                        questionComponent.revert()
                    } else {
                        questionComponent
                    }
                }
            }
        }
    }
}

private inline fun MutableStateFlow<CardViewModel2.Questions>.updateComponents(
    function: MutableList<QuestionComponent>.() -> Unit,
) {
    update {
        it.copy(
            components = it.components.toMutableList().apply(function),
        )
    }
}

private fun QuestionComponent.Level.toAnother(): Question.Level =
    when (this) {
        QuestionComponent.Level.Easy -> Question.Level.Easy
        QuestionComponent.Level.Medium -> Question.Level.Medium
        QuestionComponent.Level.Hard -> Question.Level.Hard
    }

private fun Question.Level.toAnother(): QuestionComponent.Level =
    when (this) {
        Question.Level.Easy -> QuestionComponent.Level.Easy
        Question.Level.Medium -> QuestionComponent.Level.Medium
        Question.Level.Hard -> QuestionComponent.Level.Hard
    }

private fun Question.withState(
    state: QuestionComponent.State,
): QuestionComponent =
    QuestionComponent(
        text = text,
        level = level.toAnother(),
        levelDescription = this.levelDescription,
        stateVector = Vector(
            from = state,
            to = state,
        )
    )

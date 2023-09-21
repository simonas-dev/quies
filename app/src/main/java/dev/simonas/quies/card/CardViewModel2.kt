package dev.simonas.quies.card

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.simonas.quies.data.Question
import dev.simonas.quies.millisSinceLaunch
import dev.simonas.quies.router.NavRoutes
import dev.simonas.quies.utils.Vector
import dev.simonas.quies.utils.loge
import dev.simonas.quies.utils.moveToBack
import dev.simonas.quies.utils.moveToFront
import dev.simonas.quies.utils.popFirstOrNull
import dev.simonas.quies.utils.replace
import dev.simonas.quies.utils.replaceEach
import dev.simonas.quies.utils.replaceFirst
import dev.simonas.quies.utils.tryReplaceFirst
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
internal class CardViewModel2 @Inject constructor(
    stateHandle: SavedStateHandle,
    private val shuffleQuestionDeck: ShuffleQuestionDeck,
) : ViewModel() {

    val gameSetId: String = requireNotNull(stateHandle[NavRoutes.ARG_GAME_SET])

    data class Event(
        val type: Type,
        val createdAt: Long,
    ) {
        enum class Type {
            NextQuestion,
        }
    }

    private val _interactionLog = MutableSharedFlow<Event>(extraBufferCapacity = 100)

    private val minNumberOfLowEngagement = 3
    private val minDurationUntilSkip = 1.minutes.inWholeMilliseconds
    private val isNotEngagingWithQuestions = _interactionLog
        .filter { it.type == Event.Type.NextQuestion }
        .runningFold(Long.MIN_VALUE to -1L * minDurationUntilSkip) { lastCreatedAt, event ->
            lastCreatedAt.second to event.createdAt
        }
        .map { (prevCreatedAt, newCreatedAt) -> newCreatedAt - prevCreatedAt }
        .filter { durationUntilSkip -> durationUntilSkip < minDurationUntilSkip }
        .filter { !pool.all { it.level == Question.Level.Hard } }
        .runningFold(0) { skipsCount, _ ->
            skipsCount + 1
        }
        .filter { skipsCount ->
            skipsCount >= minNumberOfLowEngagement
        }
        .map {
            millisSinceLaunch()
        }
        .take(1)

    private val minNumberOfSkips = 5
    private val hasCompletedSomeQuestions = _interactionLog
        .filter { it.type == Event.Type.NextQuestion }
        .runningFold(0) { skipsCount, _ ->
            skipsCount + 1
        }
        .filter { skipsCount ->
            skipsCount >= minNumberOfSkips
        }
        .filter { !pool.all { it.level == Question.Level.Hard } }
        .map {
            millisSinceLaunch()
        }
        .take(1)

    val showLevelSkipNotice = listOf(hasCompletedSomeQuestions, isNotEngagingWithQuestions)
        .merge()
        .take(1)

    private var pool = listOf(
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

    private val _isMenuShown = MutableStateFlow(false)
    val isMenuShown: StateFlow<Boolean> = _isMenuShown

    val isNextLevelShown: StateFlow<Boolean> = _questions
        .map { !pool.all { it.level == Question.Level.Hard } }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    @Suppress("TooGenericExceptionCaught")
    fun trigger(id: Int) {
        // Due to UI changes that now allows to click drag away cards and allow clicking on
        // elements that were not intended to be clicked before due to been behind other ones.
        // This is strictly meant to catch those weird edge-cases that are now possible.
        // In the future might be worth it to refactor this to be more bullet proof.
        // For now, this will do.
        val tempPool = pool.toMutableList()
        val tempQuestions = _questions.value
        try {
            unsafeTrigger(id)
        } catch (e: Exception) {
            loge(e)
            pool = tempPool
            _questions.value = tempQuestions
        }
    }

    private fun unsafeTrigger(id: Int) {
        val question = _questions.value.components.single { it.id == id }
        Log.d(this::class.java.simpleName, question.toString())
        when (question.state) {
            QuestionComponent.State.Landing -> {
                logEvent(Event.Type.NextQuestion)
                increaseLevelTo(question.level)
            }
            QuestionComponent.State.PrimaryRevealed -> {
                closePrimaryCard(question)
            }
            QuestionComponent.State.OtherCard -> {
                logEvent(Event.Type.NextQuestion)
                revealPrimaryCard()
            }
            QuestionComponent.State.PrimaryHidden -> {
                logEvent(Event.Type.NextQuestion)
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
        Log.d(this::class.java.simpleName, questions.value.components.toString())
    }

    data class Questions(
        val components: List<QuestionComponent>,
    )

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
        setIsMenuShown(!isMenuShown.value)
    }

    fun nextLevel() {
        setIsMenuShown(false)
        val levels = pool.map { it.level }
        when {
            Question.Level.Easy in levels -> {
                increaseLevelTo(QuestionComponent.Level.Medium)
            }
            Question.Level.Medium in levels -> {
                increaseLevelTo(QuestionComponent.Level.Hard)
            }
        }
    }

    private fun setIsMenuShown(isShown: Boolean) {
        _isMenuShown.update { isShown }
        _questions.updateComponents {
            replaceEach { index, questionComponent ->
                if (isShown) {
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

    private fun logEvent(type: Event.Type) {
        _interactionLog.tryEmit(
            Event(
                type = type,
                createdAt = millisSinceLaunch(),
            )
        )
    }

    private fun increaseLevelTo(level: QuestionComponent.Level) {
        val levelsToRemove: List<Question.Level> = when (level) {
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
            replaceFirst(
                first = { it.level == level },
                replace = { it.mutate(QuestionComponent.State.PrimaryHidden) },
            )
            replaceAll { q ->
                when {
                    q.level.toAnother() in levelsToRemove -> {
                        when (q.state) {
                            QuestionComponent.State.Closed -> {
                                q
                            }
                            QuestionComponent.State.PrimaryRevealed -> {
                                q.mutate(QuestionComponent.State.Closed)
                            }
                            else -> {
                                q.mutate(QuestionComponent.State.Disabled)
                            }
                        }
                    }
                    q.state == QuestionComponent.State.Landing -> {
                        q.mutate(QuestionComponent.State.OtherCard)
                    }
                    else -> {
                        q
                    }
                }
            }
            val nextQuestion = pool.popFirstOrNull { it.level.toAnother() == level }
            if (nextQuestion != null) {
                add(
                    index = 1,
                    element = nextQuestion
                        .withState(QuestionComponent.State.Offscreen)
                        .mutate(QuestionComponent.State.OtherCard),
                )
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

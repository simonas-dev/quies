package dev.simonas.quies.card

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.simonas.quies.AppTheme
import dev.simonas.quies.data.Question
import dev.simonas.quies.utils.KeepScreenOn
import dev.simonas.quies.utils.QDevices
import dev.simonas.quies.utils.animatePlacement
import dev.simonas.quies.utils.createTestTag
import dev.simonas.quies.utils.vertical
import kotlin.math.sin

internal object CardScreen {
    val TAG_SCREEN = createTestTag("screen")
    val TAG_CLOSE_CARD = createTestTag("close_card")
    val TAG_EXIT = createTestTag("exit")
    val TAG_PREV_QUESTIONS = createTestTag("close_card")
}

private fun closedCardX(index: Int): Dp =
    (-470 - 64 - 4 * sin(index * 1000f) * sin(index * 300f) - 0.5 * index).dp

private fun closedCardY(index: Int): Dp =
    (4 * sin(index * 1000f) * sin(index * 300f)).dp

/**
 * Biggest drawback of [CardScreen] is that it's difficult to create smooth transitions of
 * cards moving to a different states.
 *
 * The model is based on the whole screen transitioning to a different state. When transitioning
 * from one state to another to have a smooth animation the very end of exit transition has to match
 * a start animation of the new state.
 *
 * For example [CardViewModel.State.Landing] after selecting the wanted level starts an exit
 * animation and only after the animation finishes it invokes [onNextQuestion] that finally
 * changes the the state after which the [CardViewModel.State.Showing] start animation has to match
 * the end of the previous state exactly – which it still doesn't.
 *
 * Keeping track of these starts and ends becomes exceedingly hard.
 *
 * Because of this decided to redo this with a different mindset – render every card as an
 * independent component. Then the UI layer is responsible just for rendering the state of cards
 * and view models responsibility is to mutate the states.
 *
 * Keeping this just for visibility since it might be useful to somebody looking at this as a
 * reference.
 */
@Composable
internal fun CardScreen(
    state: CardViewModel.State,
    prevQuestions: List<Question>,
    onNextQuestion: (Question.Level) -> Unit,
    onQuestionClosed: (Question) -> Unit,
    onChangeLevel: (Question.Level) -> Unit,
    onBack: () -> Unit,
) {
    KeepScreenOn()

    AppTheme {
        Scaffold {
            Box(
                modifier = Modifier
                    .testTag(CardScreen.TAG_SCREEN)
                    .padding(it)
                    .fillMaxSize()
            ) {
                prevQuestions.forEachIndexed { index, _ ->
                    Card(
                        modifier = Modifier
                            .testTag(CardScreen.TAG_PREV_QUESTIONS)
                            .offset(
                                x = closedCardX(index),
                                y = closedCardY(index),
                            )
                            .align(Alignment.Center),
                    )
                }

                when (state) {
                    is CardViewModel.State.Landing -> {
                        Landing(
                            onNextQuestion = onNextQuestion,
                        )
                    }
                    is CardViewModel.State.Picking -> {
                        Picking(
                            state = state,
                            onBack = onBack,
                            onChangeLevel = onChangeLevel,
                            onNextQuestion = onNextQuestion,
                        )
                    }
                    is CardViewModel.State.Showing -> {
                        Showing(
                            state = state,
                            prevQuestions = prevQuestions,
                            onQuestionClosed = onQuestionClosed,
                        )
                    }
                }
            }
        }
    }
}

@Suppress("CyclomaticComplexMethod") // I know... later.
@Composable
private fun BoxScope.Landing(
    onNextQuestion: (Question.Level) -> Unit,
) {
    var startupAnimation: Float by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        animate(
            initialValue = 0.6f,
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
            ),
            block = { value, velocity ->
                startupAnimation = value
            }
        )
    }

    var selectedLevel: Question.Level? by remember { mutableStateOf(null) }
    val selectCardAnim: Float by animateFloatAsState(
        animationSpec = tween(
            durationMillis = 400,
        ),
        targetValue = when {
            selectedLevel != null -> 1f
            else -> 0f
        },
        finishedListener = {
            onNextQuestion(requireNotNull(selectedLevel))
        },
        label = "close animation",
    )

    val cardRotate: (Question.Level) -> Float = { level ->
        when {
            selectedLevel == level -> 90f * (1 - selectCardAnim)
            else -> 90f
        }
    }
    val cardOffsetX: (Question.Level) -> Dp = { level ->
        val multiplier = when (level) {
            Question.Level.Easy -> 1
            Question.Level.Medium -> 2
            Question.Level.Hard -> 3
        }
        var x = 0f
        x += -64 * multiplier * (1 - startupAnimation)
        when {
            selectedLevel == null -> Unit
            level == selectedLevel -> {
                x = (x * (1 - selectCardAnim)) + (-64 * 2 * (1 - startupAnimation))
            }
        }
        x.dp
    }

    val cardOffsetY: (Question.Level) -> Dp = { level ->
        val multiplier = when (level) {
            Question.Level.Easy -> 1
            Question.Level.Medium -> 2
            Question.Level.Hard -> 3
        }
        var y = 256f
        y += 32 * multiplier * (1 - startupAnimation)
        y -= (multiplier - 1) * 64
        when {
            selectedLevel == null -> Unit
            level != selectedLevel -> {
                y += 999 * selectCardAnim
            }
            level == selectedLevel -> {
                y *= (1 - selectCardAnim)
            }
        }
        y.dp
    }

    Box(
        modifier = Modifier
            .align(Alignment.Center)
            .width(704.dp),
    ) {
        Card(
            modifier = Modifier
                .animatePlacement()
                .align(
                    when (selectedLevel) {
                        Question.Level.Hard -> Alignment.Center
                        else -> Alignment.CenterEnd
                    }
                )
                .offset(
                    x = cardOffsetX(Question.Level.Hard),
                    y = cardOffsetY(Question.Level.Hard),
                )
                .vertical()
                .rotate(cardRotate(Question.Level.Hard)),
            sideText = "LEVEL 3",
            textAlpha = 1f - selectCardAnim,
            onClick = {
                selectedLevel = Question.Level.Hard
            },
        )
        Card(
            modifier = Modifier
                .animatePlacement()
                .align(
                    when (selectedLevel) {
                        Question.Level.Medium -> Alignment.Center
                        else -> Alignment.Center
                    }
                )
                .offset(
                    x = cardOffsetX(Question.Level.Medium),
                    y = cardOffsetY(Question.Level.Medium),
                )
                .vertical()
                .rotate(cardRotate(Question.Level.Medium)),
            sideText = "LEVEL 2",
            textAlpha = 1f - selectCardAnim,
            onClick = {
                selectedLevel = Question.Level.Medium
            },
        )
        Card(
            modifier = Modifier
                .animatePlacement()
                .align(
                    when (selectedLevel) {
                        Question.Level.Easy -> Alignment.Center
                        else -> Alignment.CenterStart
                    }
                )
                .offset(
                    x = cardOffsetX(Question.Level.Easy),
                    y = cardOffsetY(Question.Level.Easy),
                )
                .vertical()
                .rotate(cardRotate(Question.Level.Easy)),
            sideText = "LEVEL 1",
            textAlpha = 1f - selectCardAnim,
            onClick = {
                selectedLevel = Question.Level.Easy
            },
        )
    }
}

@Composable
private fun BoxScope.Showing(
    state: CardViewModel.State.Showing,
    prevQuestions: List<Question>,
    onQuestionClosed: (Question) -> Unit,
) {
    var startupAnimation: Float by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        animate(
            initialValue = 0.6f,
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
            ),
            block = { value, velocity ->
                startupAnimation = value
            }
        )
    }

    var showCloseQuestionAnimation: Float by remember { mutableStateOf(0f) }

    var isClosed: Boolean by remember { mutableStateOf(false) }
    val closeCardAnim: Float by animateFloatAsState(
        animationSpec = tween(
            durationMillis = 400,
        ),
        targetValue = when {
            isClosed -> 1f
            else -> 0f
        },
        finishedListener = {
            onQuestionClosed(state.question)
        },
        label = "closeCardAnim",
    )

    LaunchedEffect(Unit) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(
                delayMillis = 100,
                durationMillis = 400,
            ),
            block = { value, velocity ->
                showCloseQuestionAnimation = value
            }
        )
    }

    val index = prevQuestions.size
    Card(
        modifier = Modifier
            .offset(
                x = closedCardX(index) * closeCardAnim,
                y = closedCardY(index) * closeCardAnim,
            )
            .align(Alignment.Center),
        centerText = state.question.text,
        textAlpha = startupAnimation,
    )

    Card(
        modifier = Modifier
            .testTag(CardScreen.TAG_CLOSE_CARD)
            .align(Alignment.Center)
            .offset(x = ((470 + 64 + (128 - 128 * showCloseQuestionAnimation)) * (1 - closeCardAnim)).dp),
        sideText = state.question.level.toText(),
        onClick = {
            isClosed = true
        },
    )
}

@Suppress("CyclomaticComplexMethod") // I know... later.
@Composable
private fun BoxScope.Picking(
    state: CardViewModel.State.Picking,
    onBack: () -> Unit,
    onChangeLevel: (Question.Level) -> Unit,
    onNextQuestion: (Question.Level) -> Unit,
) {
    var startupAnimation: Float by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        animate(
            initialValue = 0.0f,
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 400,
            ),
            block = { value, velocity ->
                startupAnimation = value
            }
        )
    }

    var isPicked: Boolean by remember(state.nextLevel) { mutableStateOf(false) }
    val pickedCardAnim: Float by animateFloatAsState(
        animationSpec = tween(
            durationMillis = 400,
        ),
        targetValue = when {
            isPicked -> 1f
            else -> 0f
        },
        finishedListener = {
            onNextQuestion(state.currentLevel)
        },
        label = "close animation",
    )

    val level1Anim: Float by animateFloatAsState(
        animationSpec = tween(
            durationMillis = 400,
        ),
        targetValue = when {
            state.currentLevel == Question.Level.Easy -> 1f
            else -> 0f
        },
        label = "level1Anim",
    )
    val level2Anim: Float by animateFloatAsState(
        animationSpec = tween(
            durationMillis = 400,
        ),
        targetValue = when {
            state.currentLevel == Question.Level.Medium -> 1f
            else -> 0f
        },
        label = "level2Anim",
    )
    val level3Anim: Float by animateFloatAsState(
        animationSpec = tween(
            durationMillis = 400,
        ),
        targetValue = when {
            state.currentLevel == Question.Level.Hard -> 1f
            else -> 0f
        },
        label = "level3Anim",
    )

    Button(
        modifier = Modifier
            .testTag(CardScreen.TAG_EXIT)
            .padding(8.dp)
            .align(Alignment.BottomStart),
        onClick = onBack,
        colors = ButtonDefaults.outlinedButtonColors(),
        content = {
            Text("X")
        }
    )

    Card(
        modifier = Modifier
            .zIndex(
                when {
                    state.currentLevel == Question.Level.Easy -> 3f
                    state.nextLevel == Question.Level.Easy -> 2f
                    else -> 1f
                }
            )
            .offset(x = ((470 + 64) - (470 + 64) * level1Anim).dp)
            .let {
                if (state.currentLevel != Question.Level.Easy) {
                    it.offset(x = (128 * (1 - startupAnimation) + 128 * pickedCardAnim).dp)
                } else {
                    it
                }
            }
            .align(Alignment.Center),
        sideText = Question.Level.Easy.toText(),
        textAlpha = (1f - pickedCardAnim),
        onClick = {
            when {
                level1Anim != 1f && level1Anim != 0f -> Unit
                state.currentLevel == Question.Level.Easy -> {
                    onNextQuestion(Question.Level.Easy)
                }
                else -> {
                    onChangeLevel(Question.Level.Easy)
                }
            }
        },
    )

    Card(
        modifier = Modifier
            .zIndex(
                when {
                    state.currentLevel == Question.Level.Medium -> 3f
                    state.nextLevel == Question.Level.Medium -> 2f
                    else -> 1f
                }
            )
            .offset(x = ((470 + 64) - (470 + 64) * level2Anim).dp)
            .let {
                if (state.currentLevel != Question.Level.Medium) {
                    it.offset(x = (128 * (1 - startupAnimation) + 128 * pickedCardAnim).dp)
                } else {
                    it
                }
            }
            .align(Alignment.Center),
        sideText = Question.Level.Medium.toText(),
        textAlpha = (1f - pickedCardAnim),
        onClick = {
            when {
                level2Anim != 1f && level2Anim != 0f -> Unit
                state.currentLevel == Question.Level.Medium -> {
                    onNextQuestion(Question.Level.Medium)
                }
                else -> {
                    onChangeLevel(Question.Level.Medium)
                }
            }
        },
    )

    Card(
        modifier = Modifier
            .zIndex(
                when {
                    state.currentLevel == Question.Level.Hard -> 3f
                    state.nextLevel == Question.Level.Hard -> 2f
                    else -> 1f
                }
            )
            .offset(x = ((470 + 64) - (470 + 64) * level3Anim).dp)
            .let {
                if (state.currentLevel != Question.Level.Hard) {
                    it.offset(x = (128 * (1 - startupAnimation) + 128 * pickedCardAnim).dp)
                } else {
                    it
                }
            }
            .align(Alignment.Center),
        sideText = Question.Level.Hard.toText(),
        textAlpha = (1f - pickedCardAnim),
        onClick = {
            when {
                level3Anim != 1f && level3Anim != 0f -> Unit
                state.currentLevel == Question.Level.Hard -> {
                    onNextQuestion(Question.Level.Hard)
                }
                else -> {
                    onChangeLevel(Question.Level.Hard)
                }
            }
        },
    )
}

private fun Question.Level.toText(): String =
    when (this) {
        Question.Level.Easy -> {
            "LEVEL 1"
        }
        Question.Level.Medium -> {
            "LEVEL 2"
        }
        Question.Level.Hard -> {
            "LEVEL 3"
        }
    }

@Preview(device = QDevices.LANDSCAPE)
@Composable
internal fun PreviewLanding() {
    CardScreen(
        state = CardViewModel.State.Landing,
        onNextQuestion = {},
        onQuestionClosed = {},
        onChangeLevel = {},
        onBack = {},
        prevQuestions = emptyList(),
    )
}

@Preview(device = QDevices.LANDSCAPE)
@Composable
private fun PreviewShowing() {
    CardScreen(
        state = CardViewModel.State.Showing(
            question = Question(
                text = "If there's a reality show I'd binge-watch, " +
                    "which one do you imagine it would be?",
                level = Question.Level.Easy,
                gameSetIds = listOf("1"),
                levelDescription = "Learn something new."
            )
        ),
        onNextQuestion = {},
        onQuestionClosed = {},
        onChangeLevel = {},
        onBack = {},
        prevQuestions = emptyList(),
    )
}

@Preview(device = QDevices.LANDSCAPE)
@Composable
private fun PreviewPicking() {
    CardScreen(
        state = CardViewModel.State.Picking(
            currentLevel = Question.Level.Medium,
            nextLevel = Question.Level.Hard,
        ),
        onNextQuestion = {},
        onQuestionClosed = {},
        onChangeLevel = {},
        onBack = {},
        prevQuestions = emptyList(),
    )
}

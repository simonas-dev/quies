@file:OptIn(ExperimentalFoundationApi::class)

package dev.simonas.quies.card

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.simonas.quies.data.Question
import dev.simonas.quies.utils.QDevices
import dev.simonas.quies.utils.animatePlacement
import dev.simonas.quies.utils.createTestTag

internal object CardScreen {
    val TAG_SCREEN = createTestTag("screen")
    val TAG_NEXT_CARD = createTestTag("next_card")
    val TAG_NEXT_LEVEL = createTestTag("next_level")
    val TAG_CLOSE_CARD = createTestTag("close_card")
    val TAG_EXIT = createTestTag("exit")
}

@Composable
internal fun CardScreen(
    cardViewModel: CardViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    val state = cardViewModel.state.collectAsStateWithLifecycle()
    CardScreen(
        state = state.value,
        onNextQuestion = { level ->
            cardViewModel.next(level)
        },
        onQuestionClosed = { question ->
            cardViewModel.closed(question)
        },
        onChangeLevel = { level ->
            cardViewModel.changeLevel(level)
        },
        onBack = onBack,
    )
}

@Composable
internal fun CardScreen(
    state: CardViewModel.State,
    onNextQuestion: (Question.Level) -> Unit,
    onQuestionClosed: (Question) -> Unit,
    onChangeLevel: (Question.Level) -> Unit,
    onBack: () -> Unit,
) {
    MaterialTheme {
        Scaffold {
            Box(
                modifier = Modifier
                    .testTag(CardScreen.TAG_SCREEN)
                    .padding(it)
                    .fillMaxSize()
            ) {
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
        label = "close animation",
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

    Card(
        modifier = Modifier
            .animatePlacement()
            .offset(x = ((-470 + 84) * closeCardAnim).dp)
            .align(
                when {
                    isClosed -> Alignment.CenterStart
                    else -> Alignment.Center
                }
            ),
        centerText = state.question.text,
        textAlpha = startupAnimation,
    )

    Card(
        modifier = Modifier
            .testTag(CardScreen.TAG_CLOSE_CARD)
            .animatePlacement()
            .align(
                when {
                    isClosed -> Alignment.Center
                    else -> Alignment.CenterEnd
                }
            )
            .offset(x = ((470 - 84 * showCloseQuestionAnimation) * (1 - closeCardAnim)).dp),
        sideText = state.question.level.toText(),
        onClick = {
            isClosed = true
        }
    )
}

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

    var isPicked: Boolean by remember { mutableStateOf(false) }
    val pickedCardAnim: Float by animateFloatAsState(
        animationSpec = tween(
            durationMillis = 400,
        ),
        targetValue = when {
            isPicked -> 0f
            else -> 1f
        },
        finishedListener = {
            onNextQuestion(state.currentLevel)
        },
        label = "close animation",
    )

    Button(
        modifier = Modifier
            .testTag(CardScreen.TAG_EXIT)
            .padding(8.dp)
            .align(Alignment.BottomStart),
        onClick = onBack,
        colors = ButtonDefaults.outlinedButtonColors(),
        content = {
            Text("exit")
        }
    )

    Card(
        modifier = Modifier
            .animatePlacement()
            .offset(x = (-470 + 84).dp)
            .align(Alignment.CenterStart),
    )

    Card(
        modifier = Modifier
            .testTag(CardScreen.TAG_NEXT_CARD)
            .align(Alignment.Center),
        sideText = state.currentLevel.toText(),
        textAlpha = pickedCardAnim,
        onClick = {
            isPicked = true
        }
    )

    Card(
        modifier = Modifier
            .testTag(CardScreen.TAG_NEXT_LEVEL)
            .align(Alignment.CenterEnd)
            .offset(x = (470 - 84 * startupAnimation * pickedCardAnim).dp),
        sideText = state.nextLevel.toText(),
        onClick = {
            onChangeLevel(state.nextLevel)
        }
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
        onQuestionClosed = {},
        onNextQuestion = {},
        onChangeLevel = {},
        onBack = {},
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
            )
        ),
        onQuestionClosed = {},
        onNextQuestion = {},
        onChangeLevel = {},
        onBack = {},
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
        onQuestionClosed = {},
        onNextQuestion = {},
        onChangeLevel = {},
        onBack = {},
    )
}

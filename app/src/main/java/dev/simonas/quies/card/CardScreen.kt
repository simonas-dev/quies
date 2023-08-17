package dev.simonas.quies.card

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.simonas.quies.card.CardScreen.TAG_LEVEL_EASY
import dev.simonas.quies.card.CardScreen.TAG_LEVEL_HARD
import dev.simonas.quies.card.CardScreen.TAG_LEVEL_MEDIUM
import dev.simonas.quies.card.CardScreen.TAG_QUESTION_CARD
import dev.simonas.quies.card.CardScreen.TAG_SCREEN
import dev.simonas.quies.questions.Question
import dev.simonas.quies.utils.QDevices
import dev.simonas.quies.utils.createTestTag

internal object CardScreen {
    val TAG_SCREEN = createTestTag("screen")
    val TAG_QUESTION_CARD = createTestTag("question_card")
    val TAG_LEVEL_EASY = createTestTag("level_easy")
    val TAG_LEVEL_MEDIUM = createTestTag("level_medium")
    val TAG_LEVEL_HARD = createTestTag("level_hard")
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
        onBack = onBack,
    )
}

@Composable
internal fun CardScreen(
    state: CardViewModel.State,
    onNextQuestion: (Question.Level) -> Unit,
    onBack: () -> Unit,
) {
    MaterialTheme {
        Scaffold {
            Box(
                modifier = Modifier
                    .testTag(TAG_SCREEN)
                    .padding(it)
                    .fillMaxSize()
            ) {
                Button(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.BottomStart),
                    onClick = onBack,
                    content = {
                        Text("exit")
                    }
                )

                Card(
                    modifier = Modifier
                        .testTag(TAG_QUESTION_CARD)
                        .align(Alignment.Center),
                    text = state.question.text,
                )

                Column(
                    modifier = Modifier
                        .width(128.dp)
                        .fillMaxHeight()
                        .align(Alignment.BottomEnd),
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                    ) {
                        Button(
                            modifier = Modifier
                                .testTag(TAG_LEVEL_EASY)
                                .align(Alignment.Center),
                            colors = when {
                                state.question.level == Question.Level.Easy -> {
                                    ButtonDefaults.buttonColors()
                                }
                                else -> {
                                    ButtonDefaults.filledTonalButtonColors()
                                }
                            },
                            onClick = {
                                onNextQuestion(Question.Level.Easy)
                            }
                        ) {
                            Text(
                                text = "1",
                                fontSize = 40.sp,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                    ) {
                        Button(
                            modifier = Modifier
                                .testTag(TAG_LEVEL_MEDIUM)
                                .align(Alignment.Center),
                            colors = when {
                                state.question.level == Question.Level.Medium -> {
                                    ButtonDefaults.buttonColors()
                                }
                                else -> {
                                    ButtonDefaults.filledTonalButtonColors()
                                }
                            },
                            onClick = {
                                onNextQuestion(Question.Level.Medium)
                            }
                        ) {
                            Text(
                                text = "2",
                                fontSize = 40.sp,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                    ) {
                        Button(
                            modifier = Modifier
                                .testTag(TAG_LEVEL_HARD)
                                .align(Alignment.Center),
                            colors = when {
                                state.question.level == Question.Level.Hard -> {
                                    ButtonDefaults.buttonColors()
                                }
                                else -> {
                                    ButtonDefaults.filledTonalButtonColors()
                                }
                            },
                            onClick = {
                                onNextQuestion(Question.Level.Hard)
                            }
                        ) {
                            Text(
                                text = "3",
                                fontSize = 40.sp,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    device = QDevices.LANDSCAPE,
)
@Composable
private fun PreviewCardScreen() {
    CardScreen(
        state = CardViewModel.State(
            question = Question(
                id = "1",
                text = "If there's a reality show I'd binge-watch, " +
                    "which one do you imagine it would be?",
                level = Question.Level.Easy,
                gameSetIds = listOf("1"),
            )
        ),
        onNextQuestion = {},
        onBack = {},
    )
}

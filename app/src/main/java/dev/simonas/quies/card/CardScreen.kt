package dev.simonas.quies.card

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.simonas.quies.card.CardScreen.TAG_SCREEN
import dev.simonas.quies.questions.Question
import dev.simonas.quies.utils.QDevices
import dev.simonas.quies.utils.createTestTag

internal object CardScreen {
    val TAG_SCREEN = createTestTag("screen")
}

@Composable
internal fun CardScreen(
    cardViewModel: CardViewModel = hiltViewModel(),
) {
    val state = cardViewModel.state.collectAsStateWithLifecycle()
    CardScreen(
        state = state.value,
        onNextQuestion = {
            cardViewModel.next()
        }
    )
}

@Composable
internal fun CardScreen(
    state: CardViewModel.State,
    onNextQuestion: () -> Unit,
) {
    MaterialTheme {
        Scaffold {
            Box(
                modifier = Modifier
                    .testTag(TAG_SCREEN)
                    .padding(it)
                    .fillMaxSize()
            ) {
                Card(
                    modifier = Modifier
                        .align(Alignment.Center),
                    text = state.question.text,
                    onClick = onNextQuestion
                )
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
                text = "If there's a reality show I'd binge-watch, which one do you imagine it would be?",
            )
        ),
        onNextQuestion = {
        },
    )
}

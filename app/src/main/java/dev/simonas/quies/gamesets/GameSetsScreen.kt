package dev.simonas.quies.gamesets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.simonas.quies.AppTheme
import dev.simonas.quies.card.Card
import dev.simonas.quies.data.GameSet
import dev.simonas.quies.data.Question
import dev.simonas.quies.questions.getColor
import dev.simonas.quies.utils.QDevices
import dev.simonas.quies.utils.createTestTag
import dev.simonas.quies.utils.vertical

internal object GameSetsScreen {
    val TAG_SCREEN = createTestTag("screen")
}

@Composable
internal fun GameSetsScreen(
    viewModel: GameSetsViewModel = hiltViewModel(),
    onGameSetSelected: (id: String) -> Unit,
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    GameSetsScreen(
        state = state.value,
        onGameSetSelected = onGameSetSelected,
    )
}

@Composable
internal fun GameSetsScreen(
    modifier: Modifier = Modifier,
    state: GameSetsViewModel.State,
    onGameSetSelected: (id: String) -> Unit,
) {
    Box(modifier.testTag(GameSetsScreen.TAG_SCREEN)) {
        Text(
            modifier = Modifier.offset(x = 60.dp, y = 60.dp),
            text = "DEEP QUESTIONS",
            style = AppTheme.Text.primaryBlack,
        )

        Text(
            modifier = Modifier.offset(x = 60.dp, y = 114.dp),
            text = "Create deep and sincere conversations with the people you love.",
            style = AppTheme.Text.secondaryDemiBold,
        )

        LazyRow(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            itemsIndexed(state.gameSets) { index, set ->
                Card(
                    modifier = Modifier
                        .vertical()
                        .zIndex(9999f - index)
                        .offset(
                            x = (-32 * index).dp,
                            y = (470 / 2f).dp,
                        )
                        .rotate(90f),
                    sideText = set.name.uppercase(),
                    color = getColor(
                        gameSetId = set.id,
                        level = Question.Level.Hard,
                    ),
                    onClick = {
                        onGameSetSelected(set.id)
                    },
                )
            }
        }
    }
}

@Preview(
    device = QDevices.LANDSCAPE,
)
@Composable
private fun PreviewGameSetScreen() {
    GameSetsScreen(
        modifier = Modifier.background(color = AppTheme.Color.background),
        state = GameSetsViewModel.State(
            gameSets = listOf(
                GameSet(
                    id = "Dating",
                    name = "dating",
                ),
                GameSet(
                    id = "Friendship",
                    name = "friendship",
                ),
                GameSet(
                    id = "Debate",
                    name = "debate",
                ),
            )
        ),
        onGameSetSelected = {
        }
    )
}

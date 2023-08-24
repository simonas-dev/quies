package dev.simonas.quies.gamesets

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import dev.simonas.quies.card.Card
import dev.simonas.quies.card.vertical
import dev.simonas.quies.data.GameSet
import dev.simonas.quies.utils.QDevices
import dev.simonas.quies.utils.createTestTag

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

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun GameSetsScreen(
    state: GameSetsViewModel.State,
    onGameSetSelected: (id: String) -> Unit,
) {
    MaterialTheme {
        Scaffold {
            LazyRow(
                modifier = Modifier
                    .testTag(GameSetsScreen.TAG_SCREEN)
                    .padding(it)
                    .fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 64.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                itemsIndexed(state.gameSets) { index, set ->
                    Card(
                        modifier = Modifier
                            .vertical()
                            .zIndex(9999f - index)
                            .offset(
                                x = (-32 * index).dp,
                                y = (470 / 2).dp,
                            )
                            .rotate(90f),
                        sideText = set.name.uppercase(),
                        onClick = {
                            onGameSetSelected(set.id)
                        },
                    )
                }
            }
        }
    }
}

@Preview(
    device = QDevices.PORTRAIT,
)
@Composable
private fun PreviewGameSetScreen() {
    GameSetsScreen(
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

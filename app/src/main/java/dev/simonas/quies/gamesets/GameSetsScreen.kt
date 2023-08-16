package dev.simonas.quies.gamesets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.simonas.quies.template.QColors
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

@Composable
internal fun GameSetsScreen(
    state: GameSetsViewModel.State,
    onGameSetSelected: (id: String) -> Unit,
) {
    MaterialTheme {
        Scaffold {
            LazyColumn(
                modifier = Modifier
                    .testTag(GameSetsScreen.TAG_SCREEN)
                    .padding(it)
                    .fillMaxSize(),
                contentPadding = PaddingValues(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(state.gameSets) { set ->
                    Spacer(modifier = Modifier.height(16.dp))
                    GameSet(
                        text = set.name,
                        onClick = {
                            onGameSetSelected(set.id)
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Suppress("MagicNumber")
@Composable
internal fun GameSet(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit = {},
) {
    val cardH = 160
    val goldenRatio = 1.618033
    Surface(
        modifier = modifier
            .width((cardH * goldenRatio).dp)
            .height(cardH.dp)
            .clip(RoundedCornerShape(CornerSize(32.dp))),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xff98061e))
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(32.dp),
                color = QColors.cardTextColor,
                text = text,
                fontSize = 32.sp,
                lineHeight = 32.sp,
            )
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

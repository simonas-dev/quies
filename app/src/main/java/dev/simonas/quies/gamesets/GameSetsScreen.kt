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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.simonas.quies.AppTheme
import dev.simonas.quies.LocalUiGuide
import dev.simonas.quies.analytics.EventTracker
import dev.simonas.quies.analytics.eventTracker
import dev.simonas.quies.card.Card
import dev.simonas.quies.data.GameSet
import dev.simonas.quies.data.Question
import dev.simonas.quies.questions.getColor
import dev.simonas.quies.utils.QDevices
import dev.simonas.quies.utils.createTestTag
import dev.simonas.quies.utils.nthGoldenChildRatio
import dev.simonas.quies.utils.offsetFromFirstBaseline
import dev.simonas.quies.utils.toDp
import dev.simonas.quies.utils.toPx
import dev.simonas.quies.utils.vertical

internal object GameSetsScreen {
    val TAG_SCREEN = createTestTag("screen")
}

@Composable
internal fun GameSetsScreen(
    viewModel: GameSetsViewModel = hiltViewModel(),
    onGameSetSelected: (id: String) -> Unit,
    tracker: EventTracker = eventTracker(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        tracker.send("game_sets_screen_show")
    }

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
    Box(
        modifier
            .testTag(GameSetsScreen.TAG_SCREEN)
            .fillMaxSize(),
    ) {
        val displayHeight = LocalUiGuide.current.displayHeight
        val cardHeight = LocalUiGuide.current.card.x
        val cardPeakHeight = LocalUiGuide.current.card.x.nthGoldenChildRatio(2)

        val cardOffsetYForZeroingTop = when {
            cardHeight > displayHeight -> {
                (cardHeight - displayHeight) / 2f
            }
            else -> {
                0f
            }
        }

        val primarySpacing = cardHeight.nthGoldenChildRatio(3)
        val secondarySpacing = cardHeight.nthGoldenChildRatio(4)

        Text(
            modifier = Modifier
                .offsetFromFirstBaseline(
                    x = 60.dp.toPx(),
                    y = (displayHeight - cardPeakHeight - primarySpacing),
                ),
            text = "Coalesce".uppercase(),
            style = AppTheme.Text.primaryBlack,
        )

        Text(
            modifier = Modifier
                .offsetFromFirstBaseline(
                    x = 60.dp.toPx(),
                    y = (displayHeight - cardPeakHeight - secondarySpacing),
                ),
            text = "Are you looking for the right question?",
            style = AppTheme.Text.secondaryDemiBold,
        )

        LazyRow(
            modifier = Modifier
                .alpha(0.9f)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 32.dp),
        ) {
            itemsIndexed(state.gameSets) { index, set ->
                Card(
                    modifier = Modifier
                        .zIndex(9999f - index)
                        .offset(
                            x = (-cardHeight.nthGoldenChildRatio(6) * index).toDp(),
                            y = (cardOffsetYForZeroingTop + displayHeight - cardPeakHeight).toDp(),
                        )
                        .rotate(90f)
                        .vertical(),
                    sideText = set.name.uppercase(),
                    centerVerticalText = set.description,
                    color = getColor(
                        gameSetId = set.id,
                        level = Question.Level.Hard,
                    ),
                    isTouchScalingEnabled = false,
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
                    id = "dating",
                    name = "dating",
                    description = "Why is the world round baby?"
                ),
                GameSet(
                    id = "friendship",
                    name = "friendship",
                    description = "Let me show you my bunker."
                ),
                GameSet(
                    id = "debate",
                    name = "debate",
                    description = "For real. Let's skip the bullshit. Do you believe in aliens?"
                ),
            )
        ),
        onGameSetSelected = {
        }
    )
}

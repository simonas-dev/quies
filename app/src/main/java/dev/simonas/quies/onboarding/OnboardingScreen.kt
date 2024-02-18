package dev.simonas.quies.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.simonas.quies.AppTheme
import dev.simonas.quies.storypager.StoryPager
import dev.simonas.quies.utils.QDevices
import dev.simonas.quies.utils.createTestTag
import dev.simonas.quies.utils.isTouching
import dev.simonas.quies.utils.shortTap
import kotlin.time.Duration.Companion.seconds

internal object OnboardingScreen {
    val TAG_SCREEN = createTestTag("screen")
}

@Composable
internal fun OnboardingScreen(
    onboardingCompleted: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val state = viewModel
    OnboardingScreen(
        modifier = Modifier.testTag(OnboardingScreen.TAG_SCREEN),
        on = { interaction ->
            viewModel.on(interaction)
            if (interaction == OnboardingViewModel.Interaction.EndOnboarding) {
                onboardingCompleted()
            }
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun OnboardingScreen(
    on: (OnboardingViewModel.Interaction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pageCount = 6
    var overridePage by remember { mutableIntStateOf(0) }
    var isPaused by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.Color.background)
    ) {
        Box(modifier = Modifier.padding(8.dp)) {
            StoryPager(
                index = overridePage,
                isPaused = { isPaused },
                displayDuration = 4.seconds,
                progressUpdate = {
                    progress = it
                },
                onCompleted = {
                    on(OnboardingViewModel.Interaction.EndOnboarding)
                },
                pageCount = pageCount,
            )
        }

        val aFrac = 1f - progress % 1f
        Text(
            modifier = Modifier
                .padding(start = 64.dp)
                .alpha(aFrac)
                .align(Alignment.CenterStart),
            style = AppTheme.Text.primaryBlack,
            text = getText(progress),
        )

        val bFrac = progress % 1f
        Text(
            modifier = Modifier
                .padding(start = 64.dp)
                .alpha(bFrac)
                .align(Alignment.CenterStart),
            style = AppTheme.Text.primaryBlack,
            text = getText(progress + 1f),
        )

        Row {
            Spacer(
                modifier = Modifier
                    .shortTap(
                        tap = {
                            overridePage = (progress.toInt() - 1)
                                .coerceAtLeast(0)
                                .coerceAtMost(pageCount)
                        },
                        isTouching = {
                            isPaused = it
                        },
                    )
                    .weight(1f)
                    .fillMaxHeight(),
            )
            Spacer(
                modifier = Modifier
                    .shortTap(
                        tap = {
                            overridePage = (progress.toInt() + 1)
                                .coerceAtLeast(0)
                                .coerceAtMost(pageCount)
                        },
                        isTouching = {
                            isPaused = it
                        },
                    )
                    .weight(5f)
                    .fillMaxHeight(),
            )
        }
    }
}

@Composable
private fun getText(progress: Float): String {
    return listOf(
        "This game is about creating meaningful dialogues",
        "Slowly getting the conversation to the next level",
        "An excuse to talk about things you’ve never had",
        "Share things both of you have kept to yourselves",
        "Build a bridge between two or more inner worlds",
        "Be honest\nShare your feelings\nThat’s the way to win",
        "",
        "",
    )[progress.toInt()]
}

@Composable
@Preview(
    device = QDevices.LANDSCAPE,
)
private fun PreviewOnboardingScreen() {
    AppTheme {
        OnboardingScreen(
            on = {},
        )
    }
}

package dev.simonas.quies.onboarding

import android.graphics.RenderEffect
import android.graphics.Shader
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.simonas.quies.AppTheme
import dev.simonas.quies.storypager.StoryPager
import dev.simonas.quies.utils.KeepScreenOn
import dev.simonas.quies.utils.QDevices
import dev.simonas.quies.utils.createTestTag
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
    val pageCount = 7
    var overridePage by remember { mutableStateOf(-1 to 0L) }
    var isPaused by remember { mutableStateOf(false) }
    val progress = remember { mutableFloatStateOf(0f) }

    KeepScreenOn()

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.padding(8.dp)) {
            StoryPager(
                indexRequest = overridePage,
                isPaused = { isPaused },
                displayDuration = 4.seconds,
                progressUpdate = {
                    progress.floatValue = it
                },
                onCompleted = {
                    on(OnboardingViewModel.Interaction.EndOnboarding)
                },
                pageCount = pageCount,
            )
        }

        Onboarding(progress)

        Row {
            Spacer(
                modifier = Modifier
                    .shortTap(
                        tap = {
                            val newIndex = (progress.floatValue.toInt() - 1)
                                .coerceAtLeast(0)
                                .coerceAtMost(pageCount)
                            overridePage = newIndex to System.currentTimeMillis()
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
                            val newPage = (progress.floatValue.toInt() + 1)
                                .coerceAtLeast(0)
                                .coerceAtMost(pageCount)
                            overridePage = newPage to System.currentTimeMillis()
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
private fun Onboarding(progress: MutableFloatState) {
    val textMeasurer = rememberTextMeasurer()
    val components = listOf<CanvasComponent>(
        Splash(
            textMeasurer = textMeasurer,
            text = "How have I changed your life for better?",
            textOffset = Offset(56f * -1, 56f * -1f),
            enter = -0.05f..0.1f,
            exit = 0.3f..0.4f,
        ),
        Splash(
            textMeasurer = textMeasurer,
            text = "Are you holding on to something you need to let go of?",
            textOffset = Offset(56f * 3f, 56f * 5f),
            enter = -0.05f..0.15f,
            exit = 0.4f..0.5f,
        ),
        Splash(
            textMeasurer = textMeasurer,
            text = "What changes have you noticed in yourself in the past five years?",
            textOffset = Offset(56f * 0f, 56f * -6f),
            enter = 0.2f..0.3f,
            exit = 0.5f..0.6f,
        ),
        Splash(
            textMeasurer = textMeasurer,
            text = "What is one thing you've done that you're most proud of?",
            textOffset = Offset(56f * 1f, 56f * 3f),
            enter = 0.4f..0.5f,
            exit = 0.7f..0.8f,
        ),
        OpenerDialogLine(textMeasurer),
        ChillOutDialogLine(textMeasurer),
        ExcuseDialogLine(textMeasurer),
        LetItOutDialogLine(textMeasurer),
        ConnectionDialogLine(textMeasurer),
        OutroDialogLine(textMeasurer),
    )
    var lastDraw = System.currentTimeMillis()
    var avgDur = 16f
    Canvas(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        for (c in components) {
            c.draw(progress.floatValue, false)
        }

//        val dur = System.currentTimeMillis() - lastDraw
//        avgDur = avgDur * 0.95f + dur * 0.05f
//        logd("TTT: $avgDur")
//        lastDraw = System.currentTimeMillis()
    }

    val bluriness = remember { mutableFloatStateOf(16f) }

    Canvas(
        modifier = Modifier
            .graphicsLayer {
                renderEffect = RenderEffect
                    .createBlurEffect(
                        bluriness.floatValue.dp.toPx(),
                        bluriness.floatValue.dp.toPx(),
                        Shader.TileMode.DECAL,
                    )
                    .asComposeRenderEffect()
            }
            .fillMaxSize(),
    ) {
        val maxGlitch = components
            .map { c ->
                c.draw(progress.floatValue, true)
            }
            .maxBy { it.glichyness }
        bluriness.floatValue = 12f * maxGlitch.glichyness + 4f

//        val dur = System.currentTimeMillis() - lastDraw
//        avgDur = avgDur * 0.95f + dur * 0.05f
//        logd("TTT: $avgDur")
//        lastDraw = System.currentTimeMillis()
    }
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

package dev.simonas.quies.storypager

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.simonas.quies.AppTheme
import dev.simonas.quies.utils.isTouching
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StoryPager(
    isPaused: () -> Boolean,
    displayDuration: Duration,
    progressUpdate: (Float) -> Unit,
    onCompleted: () -> Unit,
    pageCount: Int,
    modifier: Modifier = Modifier,
    indexRequest: Pair<Int, Long> = -1 to 0,
) {
    var currentPage by remember { mutableIntStateOf(0) }
    var skipFrac by remember { mutableFloatStateOf(0f) }
    var isAnimating by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        ),
        finishedListener = {
            isAnimating = false
        },
        targetValue = currentPage + skipFrac,
        label = "pager progress",
    )

    LaunchedEffect(indexRequest) {
        if (indexRequest.first != -1) {
            currentPage = if (isAnimating) {
                indexRequest.first + 1
            } else {
                indexRequest.first
            }

            skipFrac = 0f
        }
    }

    LaunchedEffect(progress) {
        progressUpdate(progress)
    }
    LaunchedEffect(isPaused) {
        val delta = progress - (currentPage + skipFrac)
        skipFrac -= delta
    }
    LaunchedEffect(currentPage) {
        if (currentPage >= pageCount) {
            onCompleted()
        }

        launch(Dispatchers.Default) {
            while (skipFrac < 1f) {
                if (!isPaused()) {
                    skipFrac += 16f / displayDuration.inWholeMilliseconds
                }
                awaitFrame()
                awaitFrame() // fix for UI test deadlock
            }
            currentPage++
            skipFrac = 0f
        }
    }
    StepIndicators(
        current = progress,
        total = pageCount,
    )
}

@Composable
private fun StepIndicators(
    current: Float,
    total: Int,
) {
    Row {
        repeat(total) { index ->
            LinearProgressIndicator(
                modifier = Modifier
                    .height(2.dp)
                    .weight(1f)
                    .padding(
                        start = when {
                            index == 0 -> 0.dp
                            else -> 2.dp
                        }
                    ),
                color = AppTheme.Color.whiteStrong,
                trackColor = AppTheme.Color.whiteLow,
                progress = when {
                    index < current.toInt() -> 1f
                    index == current.toInt() -> current - current.toInt()
                    else -> 0f
                },
            )
        }
    }
}

@Preview(widthDp = 470, heightDp = 288)
@Composable
fun PreviewStepper() {
    AppTheme {
        var isPaused by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .isTouching {
                    isPaused = it
                }
                .background(Color.Black),
        ) {
            Box(Modifier.padding(4.dp)) {
                StoryPager(
                    modifier = Modifier,
                    displayDuration = 5.seconds,
                    progressUpdate = {},
                    onCompleted = {},
                    pageCount = 5,
                    isPaused = { isPaused },
                )
            }
        }
    }
}

package dev.simonas.quies.sandbox

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.romainguy.kotlin.math.clamp
import dev.simonas.quies.utils.distance
import dev.simonas.quies.utils.pathInterpolator
import dev.simonas.quies.utils.snappyMirrorCurvePath

@Composable
fun SnapperBox(
    modifier: Modifier = Modifier,
    anchorOffset: Offset,
    snapOffset: Offset,
    maxOffset: Offset,
    onSnapEnd: () -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    var dragAmountOffset: Offset by remember { mutableStateOf(anchorOffset) }
    var dragStartOffset: Offset? by remember { mutableStateOf(null) }
    val maxDragOffset = remember(anchorOffset, snapOffset, maxOffset) {
        Offset(
            x = distance(anchorOffset.x, snapOffset.x) + maxOffset.x,
            y = distance(anchorOffset.y, snapOffset.y) + maxOffset.y,
        )
    }
    val dragInterpolator = pathInterpolator(snappyMirrorCurvePath())
    val dragFracOffset = remember(maxDragOffset, dragAmountOffset) {
        val absX = clamp(dragAmountOffset.x, -maxOffset.x, maxDragOffset.x) / maxDragOffset.x
        val absY = clamp(dragAmountOffset.y, -maxOffset.y, maxDragOffset.y) / maxDragOffset.y
        Offset(
            x = dragInterpolator(absX),
            y = dragInterpolator(absY),
        )
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                translationX = snapOffset.x
                translationY = snapOffset.y
                alpha = 0.5f
            },
        content = content,
    )

    Box(
        modifier = modifier
            .drawBehind {
                drawLine(
                    color = Color.Red,
                    start = anchorOffset,
                    end = dragAmountOffset,
                    strokeWidth = 8f,
                )
                drawPath(snappyMirrorCurvePath(600f).asComposePath(), Color.Red, style = Stroke(width = 4f))
                drawLine(Color.Green, Offset(300f, 0f), Offset(300f, 600f))
                drawLine(Color.Green, Offset(0f, 0f), Offset(600f, 600f))
            }
            .graphicsLayer {
                translationX = anchorOffset.x
                translationY = anchorOffset.y
                alpha = 0.5f
            },
        content = content,
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                translationX = dragFracOffset.x * maxDragOffset.x
                translationY = dragFracOffset.y * maxDragOffset.y
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        dragStartOffset = dragAmountOffset
                    },
                    onDrag = { change, dragAmount ->
                        dragAmountOffset += dragAmount
                    },
                    onDragEnd = {
                        dragStartOffset = null
                        dragAmountOffset = anchorOffset
                    },
                    onDragCancel = {
                        dragStartOffset = null
                        dragAmountOffset = anchorOffset
                    },
                )
            },
        content = content,
    )
}

@Preview
@Composable
fun PreviewSnapperSandbox() {
    Box(Modifier.size(800.dp).background(Color.Gray)) {
        SnapperBox(
            modifier = Modifier.align(Alignment.Center),
            anchorOffset = Offset(0f, 0f),
            snapOffset = Offset(0f, 800f),
            maxOffset = Offset(32f, 32f),
            onSnapEnd = {
            },
        ) {
            Box(
                Modifier.size(
                    width = 100.dp,
                    height = 200.dp,
                ).background(Color.Red.copy(alpha = 0.2f))
            )
        }
    }
}

package dev.simonas.quies.card

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.simonas.quies.template.QColors
import dev.simonas.quies.utils.normalSin
import dev.simonas.quies.utils.rescaleNormal
import dev.simonas.quies.utils.timeMillisAsState

object Overflow {
    val width = 56.dp
}

@Composable
fun Overflow(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    var isTouching: Boolean by remember { mutableStateOf(false) }
    val touchScale: Float by animateFloatAsState(
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        targetValue = when {
            isTouching -> 1f
            else -> 0f
        },
        label = "touch animation",
    )

    val time = timeMillisAsState()
    Canvas(
        modifier = modifier
            .height(Overflow.width)
            .width(Overflow.width)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            PointerEventType.Press -> {
                                isTouching = true
                            }
                            PointerEventType.Release -> {
                                isTouching = false
                            }
                            PointerEventType.Exit -> {
                                isTouching = false
                            }
                            PointerEventType.Move -> {
                            }
                        }
                    }
                }
            }
    ) {
        val base = 8.dp.toPx()
        val animMulti = normalSin(time.value / 2000f)
            .rescaleNormal(0.8f, 1f)
        val touchOversize = size.width * touchScale

        drawOval(
            centerX = size.width / 2f,
            centerY = size.height / 2f,
            diameter = base + touchOversize,
        )

        drawOval(
            centerX = (size.width / 2f) - (base * 2 * animMulti) * (1f - touchScale),
            centerY = size.height / 2f,
            diameter = base - (base * (1 - animMulti)) + touchOversize,
        )

        drawOval(
            centerX = (size.width / 2f) + (base * 2 * animMulti) * (1f - touchScale),
            centerY = size.height / 2f,
            diameter = base - (base * (1 - animMulti)) + touchOversize,
        )
    }
}

private fun DrawScope.drawOval(
    centerX: Float,
    centerY: Float,
    diameter: Float,
) {
    drawOval(
        color = QColors.cardPrimaryTextColor,
        topLeft = Offset(
            x = centerX - (diameter / 2),
            y = centerY - (diameter / 2),
        ),
        size = Size(diameter, diameter),
    )
}

@Preview
@Composable
fun PreviewOverflow() {
    Overflow()
}

package dev.simonas.quies.card

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.simonas.quies.AppTheme
import dev.simonas.quies.utils.drawCross
import dev.simonas.quies.utils.drawOval
import dev.simonas.quies.utils.drawTriangle
import dev.simonas.quies.utils.isTouching
import dev.simonas.quies.utils.normalSin
import dev.simonas.quies.utils.rescaleNormal
import dev.simonas.quies.utils.timeMillisAsState

object Overflow {
    val width = 56.dp

    enum class Icon {
        Cross,
        Shapes,
        Burger,
    }
}

@Composable
fun Overflow(
    modifier: Modifier = Modifier,
    icon: Overflow.Icon = Overflow.Icon.Burger,
    onClick: () -> Unit = {},
) {
    when (icon) {
        Overflow.Icon.Cross -> {
            Cross(modifier, onClick)
        }
        Overflow.Icon.Shapes -> {
            Shapes(modifier, onClick)
        }
        else -> {
            Burger(modifier, onClick)
        }
    }
}

@Composable
private fun Shapes(
    modifier: Modifier,
    onClick: () -> Unit,
) {
    var startupAnimation: Float by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(),
            block = { value, velocity ->
                startupAnimation = value
            }
        )
    }

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
            .isTouching { isTouched ->
                isTouching = isTouched
            }
    ) {
        val base = 8.dp.toPx() * startupAnimation
        val animMulti = normalSin(time.value / 2000f)
            .rescaleNormal(0.8f, 1f)
        val touchOversize = 32.dp.toPx() * touchScale

        drawCross(
            centerX = size.width / 2f,
            centerY = size.height / 2f,
            size = base + touchOversize,
            stroke = 2.dp.toPx(),
        )

        drawOval(
            centerX = (size.width / 2f) - (base * 2 * animMulti) * (1f - touchScale),
            centerY = size.height / 2f,
            size = base - (base * (1 - animMulti)) + touchOversize,
        )

        drawTriangle(
            centerX = (size.width / 2f) + (base * 2 * animMulti) * (1f - touchScale),
            centerY = size.height / 2f,
            size = base - (base * (1 - animMulti)) + touchOversize,
        )
    }
}

@Composable
private fun Cross(
    modifier: Modifier,
    onClick: () -> Unit,
) {
    var startupAnimation: Float by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(),
            block = { value, velocity ->
                startupAnimation = value
            }
        )
    }

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

    Canvas(
        modifier = modifier
            .height(Overflow.width)
            .width(Overflow.width)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .isTouching { isTouched ->
                isTouching = isTouched
            }
    ) {
        val base = 16.dp.toPx() * startupAnimation
        val touchOversize = 32.dp.toPx() * touchScale
        val stroke = (2.dp.toPx() + 6.dp.toPx() * touchScale) * startupAnimation

        rotate(90f * (1 - startupAnimation)) {
            drawCross(
                centerX = size.width / 2f,
                centerY = size.height / 2f,
                size = base + touchOversize,
                stroke = stroke
            )
        }
    }
}

@Composable
private fun Burger(
    modifier: Modifier,
    onClick: () -> Unit,
) {
    var startupAnimation: Float by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(),
            block = { value, velocity ->
                startupAnimation = value
            }
        )
    }

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

    Canvas(
        modifier = modifier
            .height(Overflow.width)
            .width(Overflow.width)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .isTouching { isTouched ->
                isTouching = isTouched
            }
    ) {
        val iconWidth = 22.dp.toPx() * startupAnimation + (34.dp.toPx() * touchScale)
        val iconHeight = 2.dp.toPx() - 1.dp.toPx() * touchScale
        val iconSpacing = iconHeight + 3.dp.toPx()
        val iconHorizontalMargin = (size.height - iconWidth) / 2f

        drawRect(
            color = AppTheme.Color.whiteMedium,
            topLeft = Offset(
                x = iconHorizontalMargin,
                y = size.height / 2f - iconHeight / 2f,
            ),
            size = Size(
                width = iconWidth,
                height = iconHeight,
            ),
        )

        drawRect(
            color = AppTheme.Color.whiteMedium,
            topLeft = Offset(
                x = iconHorizontalMargin,
                y = size.height / 2f - iconSpacing - iconHeight / 2f,
            ),
            size = Size(
                width = iconWidth,
                height = iconHeight,
            ),
        )

        drawRect(
            color = AppTheme.Color.whiteMedium,
            topLeft = Offset(
                x = iconHorizontalMargin,
                y = size.height / 2f + iconSpacing - iconHeight / 2f,
            ),
            size = Size(
                width = iconWidth,
                height = iconHeight,
            ),
        )
    }
}

@Preview
@Composable
fun PreviewOverflow() {
    Overflow(
        modifier = Modifier.background(AppTheme.Color.dating)
    )
}

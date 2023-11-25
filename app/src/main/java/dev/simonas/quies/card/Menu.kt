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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.simonas.quies.AppTheme
import dev.simonas.quies.card.Menu.isShowingMessage
import dev.simonas.quies.utils.isTouching
import kotlin.math.pow

object Menu {
    val width = 470.dp
    val height = 24.dp

    val isShowingMessage = SemanticsPropertyKey<Boolean>("isShowingMessage")
    var SemanticsPropertyReceiver.isShowingMessage by isShowingMessage
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun Menu(
    modifier: Modifier = Modifier,
    message: String,
    showMessage: Boolean = false,
    onClick: () -> Unit = {},
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

    val isShowingMessage: Float by animateFloatAsState(
        animationSpec = tween(
            durationMillis = AppTheme.ANIM_DURATION,
        ),
        targetValue = when {
            showMessage -> 1f
            else -> 0f
        },
        label = "text animation",
    )

    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier
            .semantics { this.isShowingMessage = showMessage }
            .height(Menu.height)
            .width(Menu.width)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .isTouching { isTouched ->
                isTouching = isTouched
            }
    ) {
        val touchExpansionWidth = 34.dp.toPx() * touchScale
        val textMeasurements = textMeasurer.measure(
            text = message,
            style = AppTheme.Text.labelDemiBold,
        )
        val textWidth = textMeasurements.size.width
        val textHeight = textMeasurements.size.height
        val textHorizontalMargin = (size.width - textWidth) / 2f
        val textVerticalMargin = (size.height - textHeight) / 2f - 3f
        var textScaleX = isShowingMessage
        textScaleX *= 1f + (touchExpansionWidth / textWidth)
        val textScaleY = isShowingMessage.pow(3)

        var iconWidth = 22.dp.toPx()
        iconWidth += touchExpansionWidth
        iconWidth += textWidth * textScaleX
        iconWidth *= startupAnimation

        var middleIconWidth = 22.dp.toPx()
        middleIconWidth += touchExpansionWidth
        middleIconWidth += (textWidth - 22.dp.toPx()) * textScaleX
        middleIconWidth *= startupAnimation

        var iconHeight = 2.dp.toPx()
        iconHeight *= 1f - (touchScale * 0.25f)
        iconHeight *= 1f - (isShowingMessage * 0.6f)

        var iconSpacing = iconHeight + 3.dp.toPx()
        iconSpacing += isShowingMessage * 8.dp.toPx()

        val iconHorizontalMargin = (size.width - iconWidth) / 2f
        val middleIconHorizontalMargin = (size.width - middleIconWidth) / 2f

        drawRect(
            color = AppTheme.Color.whiteMedium,
            alpha = 1 - isShowingMessage,
            topLeft = Offset(
                x = middleIconHorizontalMargin,
                y = size.height / 2f - iconHeight / 2f,
            ),
            size = Size(
                width = middleIconWidth,
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

        scale(textScaleX, textScaleY) {
            drawText(
                textMeasurer = textMeasurer,
                text = message,
                style = AppTheme.Text.labelDemiBold,
                topLeft = Offset(
                    x = textHorizontalMargin,
                    y = textVerticalMargin,
                ),
            )
        }

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
fun PreviewMenu() {
    Box(
        modifier = Modifier
            .height(100.dp)
            .width(300.dp)
            .background(AppTheme.Color.dating),
    ) {
        Menu(
            modifier = Modifier.align(Alignment.Center).height(20.dp),
            message = "Are you ready to open up?",
            showMessage = true,
        )
    }
}

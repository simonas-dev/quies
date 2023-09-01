package dev.simonas.quies.card

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType.Companion.Exit
import androidx.compose.ui.input.pointer.PointerEventType.Companion.Move
import androidx.compose.ui.input.pointer.PointerEventType.Companion.Press
import androidx.compose.ui.input.pointer.PointerEventType.Companion.Release
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.simonas.quies.AppTheme
import dev.simonas.quies.card.Card.TAG_CENTER_TEXT
import dev.simonas.quies.card.Card.TAG_SIDE_TEXT
import dev.simonas.quies.utils.vertical

internal object Card {
    const val TAG_CENTER_TEXT = "text_center"
    const val TAG_SIDE_TEXT = "text_side"
    val width = 470.dp
    val height = 288.dp
}

@Composable
internal fun Card(
    modifier: Modifier = Modifier,
    shadowElevation: Dp = 4.dp,
    centerText: String? = null,
    sideText: String? = null,
    centerVerticalText: String? = null,
    centerVerticalTextAlpha: Float = 1f,
    textAlpha: Float = 1f,
    sideTextAlpha: Float = 1f,
    color: Color = AppTheme.Color.dating,
    onClick: (() -> Unit)? = null,
) {
    var isTouching: Boolean by remember { mutableStateOf(false) }
    val scale: Float by animateFloatAsState(
        animationSpec = tween(
            durationMillis = 200,
        ),
        targetValue = when {
            onClick == null -> 1f
            isTouching -> 1.05f
            else -> 1f
        },
        label = "touch animation",
    )

    val shape = RoundedCornerShape(CornerSize(64.dp))
    Surface(
        shape = shape,
        color = color,
        border = BorderStroke(16.dp, AppTheme.Color.washoutStrong),
        enabled = onClick != null,
        onClick = { onClick?.invoke() },
        modifier = modifier
            .graphicsLayer {
                this.shape = shape
                this.shadowElevation = shadowElevation.toPx()
            }
            .pointerInput(Unit) {
                awaitEachGesture {
                    while (true) {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            Press -> {
                                isTouching = true
                            }
                            Release -> {
                                isTouching = false
                            }
                            Exit -> {
                                isTouching = false
                            }
                            Move -> {
                                event.changes
                            }
                        }
                    }
                }
            }
            .scale(scale)
            .width(Card.width)
            .height(Card.height),
    ) {
        Box(
            modifier = Modifier.fillMaxHeight(),
        ) {
            if (centerText != null) {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 64.dp)
                        .graphicsLayer {
                            alpha = textAlpha
                        }
                        .testTag(TAG_CENTER_TEXT),
                    style = AppTheme.Text.primaryDemiBold,
                    text = centerText,
                    textAlign = TextAlign.Center,
                )
            }
            if (centerVerticalText != null) {
                Text(
                    modifier = Modifier
                        .vertical()
                        .width(224.dp)
                        .height(263.dp)
                        .align(Alignment.CenterStart)
                        .rotate(-90f)
                        .offset(y = 94.dp)
                        .graphicsLayer {
                            alpha = centerVerticalTextAlpha
                        }
                        .testTag(TAG_SIDE_TEXT),
                    style = AppTheme.Text.secondaryDemiBold,
                    text = centerVerticalText,
                    textAlign = TextAlign.Center,
                )
            }
            if (sideText != null) {
                Text(
                    modifier = Modifier
                        .vertical()
                        .align(Alignment.CenterStart)
                        .rotate(-90f)
                        .graphicsLayer {
                            alpha = sideTextAlpha
                        }
                        .padding(top = 40.dp)
                        .testTag(TAG_SIDE_TEXT),
                    style = AppTheme.Text.primaryBold,
                    text = sideText,
                )
                Text(
                    modifier = Modifier
                        .vertical()
                        .align(Alignment.CenterEnd)
                        .rotate(-90f)
                        .graphicsLayer {
                            alpha = sideTextAlpha
                        }
                        .padding(bottom = 40.dp)
                        .testTag(TAG_SIDE_TEXT),
                    style = AppTheme.Text.primaryBold,
                    text = sideText,
                )
            }
        }
    }
}

@Preview(widthDp = 470, heightDp = 288)
@Composable
private fun PreviewCardSideText() {
    Card(
        sideText = "LEVEL 1",
        onClick = {},
    )
}

@Preview(widthDp = 470, heightDp = 288)
@Composable
private fun PreviewCardCenterText() {
    Card(
        centerText = "Does the influence of technology on social interactions creates social " +
            "pressures to conform that in turn reduces our individuality and leads to more " +
            "psychological issues?",
        onClick = {},
    )
}

@Preview(widthDp = 470, heightDp = 288)
@Composable
private fun PreviewCardVerticalCenterText() {
    Card(
        sideText = "LEVEL 1",
        centerVerticalText = "Questions that will bring you closer together.",
        onClick = {},
    )
}

package dev.simonas.quies.card

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType.Companion.Exit
import androidx.compose.ui.input.pointer.PointerEventType.Companion.Move
import androidx.compose.ui.input.pointer.PointerEventType.Companion.Press
import androidx.compose.ui.input.pointer.PointerEventType.Companion.Release
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.simonas.quies.AppTheme
import dev.simonas.quies.LocalUiGuide
import dev.simonas.quies.card.Card.TAG_CENTER_TEXT
import dev.simonas.quies.card.Card.TAG_SIDE_TEXT
import dev.simonas.quies.utils.lerpNonAlpha
import dev.simonas.quies.utils.mutColor
import dev.simonas.quies.utils.nthGoldenChildRatio
import dev.simonas.quies.utils.toDp
import dev.simonas.quies.utils.vertical

internal object Card {
    const val TAG_CENTER_TEXT = "text_center"
    const val TAG_SIDE_TEXT = "text_side"
}

@Composable
internal fun Card(
    modifier: Modifier = Modifier,
    backgroundActiveness: Float = 1f,
    textActiveness: Float = 1f,
    shadowElevation: Dp = 4.dp,
    centerText: String? = null,
    sideText: String? = null,
    centerVerticalText: String? = null,
    centerVerticalTextAlpha: Float = 1f,
    textAlpha: Float = 1f,
    sideTextAlpha: Float = 1f,
    color: Color = AppTheme.Color.dating,
    onClick: (() -> Unit)? = null,
    pointerInputKey: Any = Unit,
    onDragStop: () -> Unit = {},
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit = { change, _ -> change.consume() },
    onDragStart: (Offset) -> Unit = {},
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

    val descSpacing = LocalUiGuide.current.card.x
        .nthGoldenChildRatio(3)

    val levelSpacing = LocalUiGuide.current.card.x
        .nthGoldenChildRatio(4)

    val cornerSize = LocalUiGuide.current.card.x
        .nthGoldenChildRatio(4)

    val borderSize = LocalUiGuide.current.card.x
        .nthGoldenChildRatio(7)

    val shape = RoundedCornerShape(CornerSize(cornerSize))

    val colorActivenessMut: Color.() -> Color = {
        lerpNonAlpha(Color.Black, 1f - backgroundActiveness)
    }

    val colorTextActivenessMut: Color.() -> Color = {
        lerpNonAlpha(Color.Black, 1f - textActiveness)
    }

    Surface(
        shape = shape,
        color = color.colorActivenessMut(),
        border = BorderStroke(
            width = borderSize.toDp(),
            color = AppTheme.Color.washoutStrong.colorTextActivenessMut(),
        ),
        enabled = onClick != null,
        onClick = { onClick?.invoke() },
        modifier = modifier
            .graphicsLayer {
                this.shape = shape
                this.shadowElevation = shadowElevation.toPx()
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = onDragStop,
                    onDragCancel = onDragStop,
                    onDragStart = onDragStart,
                    onDrag = onDrag,
                )
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
            .width(LocalUiGuide.current.card.x.toDp())
            .height(LocalUiGuide.current.card.y.toDp()),
    ) {
        Box(
            modifier = Modifier.fillMaxHeight(),
        ) {
            if (centerText != null) {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = cornerSize.toDp())
                        .graphicsLayer {
                            alpha = textAlpha
                        }
                        .testTag(TAG_CENTER_TEXT),
                    style = AppTheme.Text.primaryDemiBold
                        .mutColor(colorTextActivenessMut),
                    text = centerText,
                    textAlign = TextAlign.Center,
                )
            }
            if (centerVerticalText != null) {
                Text(
                    modifier = Modifier
                        .vertical()
                        .align(Alignment.CenterStart)
                        .rotate(-90f)
                        .paddingFromBaseline(top = descSpacing.toDp())
                        .graphicsLayer {
                            alpha = centerVerticalTextAlpha
                        }
                        .testTag(TAG_SIDE_TEXT),
                    style = AppTheme.Text.secondaryDemiBold
                        .mutColor(colorTextActivenessMut),
                    text = centerVerticalText,
                    textAlign = TextAlign.Center,
                )
            }
            if (sideText != null) {
                var firstBaselineOffset by remember { mutableFloatStateOf(0f) }

                Text(
                    modifier = Modifier
                        .vertical()
                        .align(Alignment.CenterStart)
                        .rotate(-90f)
                        .graphicsLayer {
                            alpha = sideTextAlpha
                        }
                        .padding(top = (levelSpacing - firstBaselineOffset).toDp())
                        .testTag(TAG_SIDE_TEXT),
                    onTextLayout = {
                        firstBaselineOffset = it.firstBaseline
                    },
                    style = AppTheme.Text.primaryBold
                        .mutColor(colorTextActivenessMut),
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
                        .padding(bottom = (levelSpacing - firstBaselineOffset).toDp())
                        .testTag(TAG_SIDE_TEXT),
                    style = AppTheme.Text.primaryBold
                        .mutColor(colorTextActivenessMut),
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

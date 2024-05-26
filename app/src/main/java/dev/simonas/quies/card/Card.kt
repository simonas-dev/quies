package dev.simonas.quies.card

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.simonas.quies.AppTheme
import dev.simonas.quies.LocalUiGuide
import dev.simonas.quies.card.Card.TAG_CENTER_TEXT
import dev.simonas.quies.card.Card.TAG_SIDE_TEXT
import dev.simonas.quies.utils.alpha
import dev.simonas.quies.utils.lerpNonAlpha
import dev.simonas.quies.utils.mutColor
import dev.simonas.quies.utils.nthGoldenChildRatio
import dev.simonas.quies.utils.offsetFromFirstBaseline
import dev.simonas.quies.utils.toDp
import dev.simonas.quies.utils.vertical

internal object Card {
    const val TAG_CENTER_TEXT = "text_center"
    const val TAG_SIDE_TEXT = "text_side"
}

interface DragListener {
    fun onStop()
    fun onDrag(change: PointerInputChange, dragAmount: Offset)
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
    isCenterTextVisible: Boolean = textAlpha != 0f,
    sideTextAlpha: Float = 1f,
    color: Color = AppTheme.Color.dating,
    onClick: (() -> Unit)? = null,
    pointerInputKey: Any = Unit,
    dragListener: DragListener? = null,
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
                if (dragListener != null) {
                    detectDragGestures(
                        onDragEnd = dragListener::onStop,
                        onDragCancel = dragListener::onStop,
                        onDrag = dragListener::onDrag,
                    )
                }
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
                val centerTextStyle = AppTheme.Text.primaryDemiBold
                    .mutColor(colorTextActivenessMut)
                val revealingText = revealingTextState(
                    text = centerText,
                    isVisible = textAlpha > 0f,
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = cornerSize.toDp())
                        .graphicsLayer {
                            alpha = textAlpha
                        }
                        .testTag(TAG_CENTER_TEXT),
                    style = centerTextStyle,
                    text = buildAnnotatedString {
                        revealingText.value.forEach { (char, alpha) ->
                            val spanStyle = centerTextStyle.toSpanStyle().let {
                                it.copy(
                                    color = it.color.copy(
                                        alpha = it.color.alpha * alpha
                                    ),
                                    shadow = it.shadow?.let { shadow ->
                                        shadow.copy(
                                            color = shadow.color.copy(
                                                alpha = shadow.color.alpha * alpha
                                            ),
                                        )
                                    }
                                )
                            }
                            withStyle(style = spanStyle) {
                                append(char)
                            }
                        }
                    },
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
                Text(
                    modifier = Modifier
                        .vertical()
                        .align(Alignment.CenterStart)
                        .rotate(-90f)
                        .graphicsLayer {
                            alpha = sideTextAlpha
                        }
                        .offsetFromFirstBaseline(
                            x = 0f,
                            y = levelSpacing,
                        )
                        .testTag(TAG_SIDE_TEXT),
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
                        .offsetFromFirstBaseline(
                            x = 0f,
                            y = -levelSpacing,
                        )
                        .testTag(TAG_SIDE_TEXT),
                    style = AppTheme.Text.primaryBold
                        .mutColor(colorTextActivenessMut),
                    text = sideText,
                )
            }
        }
    }
}

@Composable
private fun revealingTextState(
    text: String,
    isVisible: Boolean,
): State<List<Pair<Char, Float>>> {
    val state = remember(text) {
        mutableStateOf(text.toCharArray().map { char -> char to 0f })
    }
    val animation = remember(text) {
        Animatable(0f)
    }
    val animState = animation.asState()
    LaunchedEffect(text, isVisible) {
        if (isVisible) {
            animation.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = (text.length * 40f).toInt(),
                    easing = CubicBezierEasing(0.2f, 0.2f, 0.5f, 1.0f),
                ),
            )
        } else {
            animation.snapTo(0f)
        }
    }
    LaunchedEffect(animState.value) {
        state.value = state.value.mapIndexed { index, (char, _) ->
            val alpha = charAlpha(
                index = index,
                size = state.value.size,
                animFrac = animState.value,
            )
            char to alpha
        }
    }
    return state
}

private fun charAlpha(
    index: Int,
    size: Int,
    animFrac: Float,
): Float {
    val charWindow = 10f
    val animIndex = (size - 1 + charWindow) * animFrac - charWindow
    val delta = index - animIndex
    return when {
        delta <= 0f -> 1f
        delta < charWindow -> 1f - (delta / charWindow)
        else -> 0f
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

package dev.simonas.quies.card

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.PointerEventType.Companion.Exit
import androidx.compose.ui.input.pointer.PointerEventType.Companion.Move
import androidx.compose.ui.input.pointer.PointerEventType.Companion.Press
import androidx.compose.ui.input.pointer.PointerEventType.Companion.Release
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.simonas.quies.card.Card.TAG_CENTER_TEXT
import dev.simonas.quies.card.Card.TAG_SIDE_TEXT
import dev.simonas.quies.template.QColors

internal object Card {
    const val TAG_CENTER_TEXT = "text_center"
    const val TAG_SIDE_TEXT = "text_side"
}

@Composable
internal fun Card(
    modifier: Modifier = Modifier,
    centerText: String? = null,
    sideText: String? = null,
    textAlpha: Float = 1f,
    onClick: (() -> Unit)? = null,
) {
    var isTouching: Boolean by remember { mutableStateOf(false) }
    val scale: Float by animateFloatAsState(
        animationSpec = tween(
            durationMillis = 400,
        ),
        targetValue = when {
            onClick == null -> 1f
            isTouching -> 1.05f
            else -> 1f
        },
        label = "touch animation",
    )

    Surface(
        shape = RoundedCornerShape(CornerSize(64.dp)),
        color = QColors.cardBackground,
        shadowElevation = 4.dp,
        enabled = onClick != null,
        onClick = { onClick?.invoke() },
        modifier = modifier
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
            .width(470.dp)
            .height(288.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxHeight(),
        ) {
            if (centerText != null) {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .width(278.dp)
                        .alpha(textAlpha)
                        .testTag(TAG_CENTER_TEXT),
                    color = QColors.cardPrimaryTextColor,
                    text = centerText,
                    fontSize = 24.sp,
                    lineHeight = 32.sp,
                    textAlign = TextAlign.Center,
                )
            }
            if (sideText != null) {
                Text(
                    modifier = Modifier
                        .vertical()
                        .align(Alignment.CenterStart)
                        .rotate(-90f)
                        .alpha(textAlpha)
                        .padding(top = 40.dp,)
                        .testTag(TAG_SIDE_TEXT),
                    color = QColors.cardSecondaryTextColor,
                    text = sideText,
                    fontSize = 24.sp,
                    lineHeight = 32.sp,
                )
                Text(
                    modifier = Modifier
                        .vertical()
                        .align(Alignment.CenterEnd)
                        .rotate(-90f)
                        .alpha(textAlpha)
                        .padding(bottom = 40.dp)
                        .testTag(TAG_SIDE_TEXT),
                    color = QColors.cardSecondaryTextColor,
                    text = sideText,
                    fontSize = 24.sp,
                    lineHeight = 32.sp,
                )
            }
        }
    }
}

fun Modifier.vertical() =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.height, placeable.width) {
            placeable.place(
                x = -(placeable.width / 2 - placeable.height / 2),
                y = -(placeable.height / 2 - placeable.width / 2)
            )
        }
    }

@Preview(widthDp = 470, heightDp = 288)
@Composable
private fun PreviewCard() {
    Card(
        centerText = "Pick someone in the group you don’t know and guess what they studied in college?",
        sideText = "LEVEL 1"
    )
}

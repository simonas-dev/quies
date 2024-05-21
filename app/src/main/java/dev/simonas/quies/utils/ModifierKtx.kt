package dev.simonas.quies.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.round
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun Modifier.shortTap(
    shortDuration: Duration = 200.milliseconds,
    isTouching: (Boolean) -> Unit = {},
    tap: () -> Unit,
): Modifier = composed {
    var pressAt by remember { mutableLongStateOf(0L) }
    this.pointerInput(Unit) {
        while (true) {
            awaitPointerEventScope {
                val event = awaitPointerEvent()
                when (event.type) {
                    PointerEventType.Press -> {
                        pressAt = System.currentTimeMillis()
                        isTouching(true)
                    }

                    PointerEventType.Release -> {
                        val delta = System.currentTimeMillis() - pressAt
                        if (delta < shortDuration.inWholeMilliseconds) {
                            tap()
                        }
                        isTouching(false)
                    }

                    PointerEventType.Exit -> {
                        isTouching(false)
                    }
                }
            }
        }
    }
}

fun Modifier.isTouching(isTouched: (Boolean) -> Unit): Modifier {
    return this.pointerInput(Unit) {
        while (true) {
            awaitPointerEventScope {
                val event = awaitPointerEvent()
                when (event.type) {
                    PointerEventType.Press -> {
                        isTouched(true)
                    }
                    PointerEventType.Release -> {
                        isTouched(false)
                    }
                    PointerEventType.Exit -> {
                        isTouched(false)
                    }
                }
            }
        }
    }
}

internal fun Modifier.animatePlacement(): Modifier = composed {
    val scope = rememberCoroutineScope()
    var targetOffset by remember { mutableStateOf(IntOffset.Zero) }
    var animatable by remember {
        mutableStateOf<Animatable<IntOffset, AnimationVector2D>?>(null)
    }
    this
        .onPlaced {
            // Calculate the position in the parent layout
            targetOffset = it
                .positionInParent()
                .round()
        }
        .offset {
            // Animate to the new target offset when alignment changes.
            val anim = animatable ?: Animatable(targetOffset, IntOffset.VectorConverter)
                .also { animatable = it }
            if (anim.targetValue != targetOffset) {
                scope.launch {
                    anim.animateTo(targetOffset, spring(stiffness = Spring.StiffnessMediumLow))
                }
            }
            // Offset the child in the opposite direction to the targetOffset, and slowly catch
            // up to zero offset via an animation to achieve an overall animated movement.
            animatable?.let { it.value - targetOffset } ?: IntOffset.Zero
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

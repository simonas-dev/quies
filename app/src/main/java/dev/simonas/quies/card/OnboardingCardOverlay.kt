@file:OptIn(ExperimentalAnimationApi::class)

package dev.simonas.quies.card

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.simonas.quies.AppTheme
import dev.simonas.quies.LocalUiGuide
import dev.simonas.quies.UiGuide
import dev.simonas.quies.seconds
import dev.simonas.quies.utils.Vector
import dev.simonas.quies.utils.nthGoldenChildRatio
import dev.simonas.quies.utils.toPx
import kotlinx.coroutines.launch
import java.lang.Math.toRadians
import java.util.UUID
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

internal fun OverlayComponent.mutate(state: OverlayComponent.State): OverlayComponent =
    copy(
        stateVector = Vector(
            from = this.state,
            to = state,
        ),
        modifiedAtSecsVector = Vector(
            from = this.modifiedAtSecs,
            to = seconds(),
        )
    )

private val blank = OverlayComponent(
    text = null,
    sideText = null,
    stateVector = Vector(
        from = OverlayComponent.State.Offscreen,
        to = OverlayComponent.State.Offscreen,
    ),
)

private val level1 = OverlayComponent(
    text = null,
    sideText = "LEVEL 1",
    stateVector = Vector(
        from = OverlayComponent.State.Offscreen,
        to = OverlayComponent.State.Offscreen,
    ),
)

private val level2 = OverlayComponent(
    text = null,
    sideText = "LEVEL 2",
    stateVector = Vector(
        from = OverlayComponent.State.Offscreen,
        to = OverlayComponent.State.Offscreen,
    ),
)

private val level3 = OverlayComponent(
    text = null,
    sideText = "LEVEL 3",
    stateVector = Vector(
        from = OverlayComponent.State.Offscreen,
        to = OverlayComponent.State.Offscreen,
    ),
)

private val outroLevel = OverlayComponent(
    text = "My joys\nMy hardships\nCan I share it with you?",
    sideText = "COALESCE",
    stateVector = Vector(
        from = OverlayComponent.State.Offscreen,
        to = OverlayComponent.State.Offscreen,
    ),
)

private fun MutableState<List<OverlayComponent>>.mut(
    id: Int,
    mut: (OverlayComponent) -> OverlayComponent,
) {
    value = value.map {
        if (it.id == id) {
            mut(it)
        } else {
            it
        }
    }
}

private fun List<OverlayComponent>.mut(
    id: Int,
    mut: (OverlayComponent) -> OverlayComponent,
): List<OverlayComponent> {
    return this.map {
        if (it.id == id) {
            mut(it)
        } else {
            it
        }
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun OnboardingCardOverlay(
    progress: MutableFloatState,
) {
    val cards = remember {
        mutableStateOf<List<OverlayComponent>>(
            listOf(
                blank,
                level1,
                level2,
                level3,
                outroLevel,
            )
        )
    }

    LaunchedEffect(progress.floatValue.toInt()) {
        when (progress.floatValue.toInt()) {
            0 -> {
                cards.value = cards.value
                    .mut(blank.id, { it.mutate(OverlayComponent.State.Offscreen) })
                    .mut(level1.id, { it.mutate(OverlayComponent.State.Offscreen) })
                    .mut(level2.id, { it.mutate(OverlayComponent.State.Offscreen) })
                    .mut(level3.id, { it.mutate(OverlayComponent.State.Offscreen) })
                    .mut(outroLevel.id, { it.mutate(OverlayComponent.State.Offscreen) })
            }
            1 -> {
                cards.value = cards.value
                    .mut(blank.id, { it.mutate(OverlayComponent.State.Peaking) })
                    .mut(level1.id, { it.mutate(OverlayComponent.State.Offscreen) })
                    .mut(level2.id, { it.mutate(OverlayComponent.State.Offscreen) })
                    .mut(level3.id, { it.mutate(OverlayComponent.State.Offscreen) })
                    .mut(outroLevel.id, { it.mutate(OverlayComponent.State.Offscreen) })
            }
            2 -> {
                cards.value = cards.value
                    .mut(blank.id, { it.mutate(OverlayComponent.State.Peaking) })
                    .mut(level1.id, { it.mutate(OverlayComponent.State.Peaking) })
                    .mut(level2.id, { it.mutate(OverlayComponent.State.Offscreen) })
                    .mut(level3.id, { it.mutate(OverlayComponent.State.Offscreen) })
                    .mut(outroLevel.id, { it.mutate(OverlayComponent.State.Offscreen) })
            }
            3 -> {
                cards.value = cards.value
                    .mut(blank.id, { it.mutate(OverlayComponent.State.Peaking) })
                    .mut(level1.id, { it.mutate(OverlayComponent.State.Peaking) })
                    .mut(level2.id, { it.mutate(OverlayComponent.State.Peaking) })
                    .mut(level3.id, { it.mutate(OverlayComponent.State.Offscreen) })
                    .mut(outroLevel.id, { it.mutate(OverlayComponent.State.Offscreen) })
            }
            4 -> {
                cards.value = cards.value
                    .mut(blank.id, { it.mutate(OverlayComponent.State.Peaking) })
                    .mut(level1.id, { it.mutate(OverlayComponent.State.Peaking) })
                    .mut(level2.id, { it.mutate(OverlayComponent.State.Peaking) })
                    .mut(level3.id, { it.mutate(OverlayComponent.State.Peaking) })
                    .mut(outroLevel.id, { it.mutate(OverlayComponent.State.Offscreen) })
            }
            5 -> {
                cards.value = cards.value
                    .mut(blank.id, { it.mutate(OverlayComponent.State.Peaking) })
                    .mut(level1.id, { it.mutate(OverlayComponent.State.Peaking) })
                    .mut(level2.id, { it.mutate(OverlayComponent.State.Peaking) })
                    .mut(level3.id, { it.mutate(OverlayComponent.State.Peaking) })
                    .mut(outroLevel.id, { it.mutate(OverlayComponent.State.Peaking) })
            }
            6 -> {
                cards.value = cards.value
                    .mut(blank.id, { it.mutate(OverlayComponent.State.Offscreen) })
                    .mut(level1.id, { it.mutate(OverlayComponent.State.Offscreen) })
                    .mut(level2.id, { it.mutate(OverlayComponent.State.Offscreen) })
                    .mut(level3.id, { it.mutate(OverlayComponent.State.Offscreen) })
                    .mut(outroLevel.id, { it.mutate(OverlayComponent.State.Center) })
            }
        }
    }

    val bigSpace = LocalUiGuide.current.bigSpace

    Box(
        modifier = Modifier
            .padding()
            .fillMaxSize(),
    ) {
        val components = cards.value
        val size = components.size
        components.forEachIndexed { index, ques ->
            StatefulCard(
                index = index,
                size = size,
                component = ques,
                bigSpace = bigSpace,
            )
        }
    }
}

@SuppressLint("ClickableViewAccessibility")
@Composable
private fun BoxScope.StatefulCard(
    index: Int,
    size: Int,
    component: OverlayComponent,
    bigSpace: Float,
) {
    val uiGuide = LocalUiGuide.current
    val dragOffsetX = remember { Animatable(0f) }
    val dragOffsetY = remember { Animatable(0f) }

    var isDragThresholdReached by remember { mutableStateOf(false) }

    val rotation = 90f

    val centerTextAlpha by animateFloatAsState(
        animationSpec = tween(
            durationMillis = AppTheme.ANIM_DURATION * 2,
        ),
        targetValue = when (component.state) {
            OverlayComponent.State.Offscreen -> {
                0f
            }
            OverlayComponent.State.Peaking -> {
                0f
            }
            OverlayComponent.State.Center -> {
                1f
            }
        },
        label = "centerTextAlpha",
    )
    val sideTextAlpha by animateFloatAsState(
        animationSpec = tween(
            durationMillis = AppTheme.ANIM_DURATION * 2,
        ),
        targetValue = when (component.state) {
            OverlayComponent.State.Offscreen -> {
                1f
            }
            OverlayComponent.State.Peaking -> {
                1f
            }
            OverlayComponent.State.Center -> {
                0f
            }
        },
        label = "sideTextAlpha",
    )
    val cardScale by animateFloatAsState(
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
        ),
        targetValue = when {
            isDragThresholdReached -> 0.9524f
            else -> 1f
        },
        label = "cardScale",
    )

    val stateOffsetX = remember {
        Animatable(
            computeOffsetX(
                index = index * 1f,
                state = component.stateVector.from,
                uiGuide = uiGuide,
            )
        )
    }
    LaunchedEffect(component) {
        stateOffsetX.animateTo(
            targetValue = computeOffsetX(
                state = component.state,
                index = index * 1f,
                uiGuide = uiGuide,
            ),
            animationSpec = tween(
                durationMillis = AppTheme.ANIM_DURATION * 2,
            ),
        )
    }

    val stateOffsetY = remember {
        Animatable(
            computeOffsetY(
                state = component.stateVector.from,
                index = index * 1f,
                uiGuide = uiGuide,
            )
        )
    }
    LaunchedEffect(component) {
        stateOffsetY.animateTo(
            targetValue = computeOffsetY(
                index = index * 1f,
                state = component.state,
                uiGuide = uiGuide,
            ),
            animationSpec = tween(
                durationMillis = AppTheme.ANIM_DURATION * 2,
            ),
        )
    }

    val coroutineCtx = rememberCoroutineScope()

    val dragTrigger = with(LocalDensity.current) { 64.dp.toPx() }

    val cardDragOffset = {
        val dragOffset = {
            Offset(
                x = dragOffsetX.value,
                y = dragOffsetY.value,
            )
        }
        computeCardDragOffset(
            rotation = rotation,
            dragOffset = dragOffset(),
        )
    }

    val cardTranslationOffset = {
        val dragOffset = cardDragOffset()
        Offset(
            x = stateOffsetX.value + dragOffset.x,
            y = stateOffsetY.value + dragOffset.y,
        )
    }

    Card(
        modifier = Modifier
            .zIndex(index.toFloat())
            .align(Alignment.TopCenter)
            .graphicsLayer {
                val offset = cardTranslationOffset()
                translationX = offset.x
                translationY = offset.y
                scaleX = cardScale
                scaleY = cardScale
            },
        shadowElevation = 4.dp,
        centerText = component.text,
        sideText = component.sideText,
        textAlpha = centerTextAlpha,
        sideTextAlpha = sideTextAlpha,
        color = AppTheme.Color.onboarding,
        onClick = {
//            onClick(component)
        },
        dragListener = object : DragListener {
            override fun onStop() {
                isDragThresholdReached = false
                if (abs(dragOffsetX.value) > dragTrigger) {
                    val cardOffset = cardTranslationOffset()
                    coroutineCtx.launch {
                        dragOffsetX.snapTo(0f)
                        dragOffsetY.snapTo(0f)
                        stateOffsetX.snapTo(cardOffset.x)
                        stateOffsetY.snapTo(cardOffset.y)
//                        onClick(component)
                    }
                } else {
                    coroutineCtx.launch {
                        dragOffsetX.animateTo(0f, spring(0.5f))
                    }
                    coroutineCtx.launch {
                        dragOffsetY.animateTo(0f, spring(0.5f))
                    }
                }
            }

            override fun onDrag(change: PointerInputChange, dragAmount: Offset) {
                isDragThresholdReached = abs(dragOffsetX.value) > dragTrigger ||
                    abs(dragOffsetY.value) > dragTrigger
                coroutineCtx.launch {
                    dragOffsetX.snapTo(dragOffsetX.value + dragAmount.x)
                    dragOffsetY.snapTo(dragOffsetY.value + dragAmount.y)
                }
            }
        },
    )
}

private fun computeOffsetX(
    index: Float,
    state: OverlayComponent.State,
    uiGuide: UiGuide,
): Float {
    var offset = 0f
    val cardHeight = uiGuide.card.x
    when (state) {
        OverlayComponent.State.Peaking -> {
            offset += (uiGuide.displayWidth / 2) + (cardHeight / 2f) - (cardHeight * 0.216f)
            offset += cardHeight.nthGoldenChildRatio(11) * index
//            offset += fbm(seed = index, octaves = 7) * cardHeight.nthGoldenChildRatio(8)
        }
        OverlayComponent.State.Offscreen -> {
            offset += (uiGuide.displayWidth / 2) + (cardHeight / 2f)
        }
        OverlayComponent.State.Center -> {
            val borderWidth = cardHeight.nthGoldenChildRatio(7)
            offset += (uiGuide.displayWidth / 2) + (cardHeight / 2f) - cardHeight - borderWidth
        }
    }
    return offset
}

private fun computeOffsetY(
    index: Float,
    state: OverlayComponent.State,
    uiGuide: UiGuide,
): Float {
    val screenH = uiGuide.displayHeight
    val cardSize = uiGuide.card.x

    var offset = 0f
    when (state) {
        OverlayComponent.State.Peaking -> {
//            offset += fbm(seed = index, octaves = 4) * cardSize.nthGoldenChildRatio(7)
            offset += uiGuide.bigSpace
        }
        else -> {
            offset += uiGuide.bigSpace
        }
    }
    return offset
}

private fun computeCardDragOffset(
    rotation: Float,
    dragOffset: Offset,
): Offset {
    val rotRads = toRadians(rotation.toDouble())
    val absDragX = dragOffset.x * cos(rotRads) + -1 * dragOffset.y * sin(rotRads)
    val absDragY = dragOffset.y * cos(rotRads) + dragOffset.x * sin(rotRads)
    return Offset(
        x = absDragX.toFloat(),
        y = absDragY.toFloat(),
    )
}

internal data class OverlayComponent(
    val id: Int = UUID.randomUUID().hashCode(),
    val text: String? = null,
    val sideText: String? = null,
    val stateVector: Vector<State>,
    val modifiedAtSecsVector: Vector<Float> = Vector(seconds(), seconds())
) {
    val state: State = stateVector.to
    val modifiedAtSecs: Float = modifiedAtSecsVector.to

    enum class State {
        Offscreen,
        Peaking,
        Center,
    }
}

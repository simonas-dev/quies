@file:OptIn(ExperimentalAnimationApi::class)

package dev.simonas.quies.card

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.simonas.quies.AppTheme
import dev.simonas.quies.LocalUiGuide
import dev.simonas.quies.UiGuide
import dev.simonas.quies.card.CardScreen2.questionState
import dev.simonas.quies.data.Question
import dev.simonas.quies.questions.getColor
import dev.simonas.quies.utils.KeepScreenOn
import dev.simonas.quies.utils.OnSystemBackClick
import dev.simonas.quies.utils.createTestTag
import dev.simonas.quies.utils.fbm
import dev.simonas.quies.utils.nthGoldenChildRatio
import dev.simonas.quies.utils.toPx
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.Math.toRadians
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

internal object CardScreen2 {
    val TAG_SCREEN = createTestTag("screen")
    val TAG_MENU_TOGGLE = createTestTag("menu_toggle")
    val TAG_EXIT = createTestTag("exit")
    val TAG_NEXT_LEVEL = createTestTag("next_level")

    val questionState = SemanticsPropertyKey<QuestionComponent.State>("state")
    var SemanticsPropertyReceiver.questionState by questionState
}

@Composable
internal fun CardScreen2(
    cardViewModel: CardViewModel2 = hiltViewModel(),
    onBack: () -> Unit,
) {
    val state = cardViewModel.questions.collectAsStateWithLifecycle()
    val isMenuShown = cardViewModel.isMenuShown.collectAsStateWithLifecycle()
    val isNextLevelShown = cardViewModel.isNextLevelShown.collectAsStateWithLifecycle()

    OnSystemBackClick {
        cardViewModel.toggleMenu()
    }

    CardScreen2(
        gameSetId = cardViewModel.gameSetId,
        questions = state,
        isMenuShown = isMenuShown,
        isNextLevelShown = isNextLevelShown,
        showLevelSkipNotice = cardViewModel.showLevelSkipNotice,
        onClick = {
            cardViewModel.trigger(it.id)
        },
        toggleMenu = cardViewModel::toggleMenu,
        onExit = onBack,
        onNextLevel = cardViewModel::nextLevel,
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun CardScreen2(
    gameSetId: String,
    questions: State<CardViewModel2.Questions>,
    isMenuShown: State<Boolean>,
    isNextLevelShown: State<Boolean>,
    onClick: (QuestionComponent) -> Unit,
    toggleMenu: () -> Unit,
    onExit: () -> Unit,
    onNextLevel: () -> Unit,
    showLevelSkipNotice: Flow<Long>,
) {
    KeepScreenOn()

    var showMenuMessage by remember { mutableStateOf(false) }
    LaunchedEffect(showLevelSkipNotice) {
        showLevelSkipNotice.collectLatest {
            showMenuMessage = true
            delay(5000)
            showMenuMessage = false
        }
    }

    val bigSpace = LocalUiGuide.current.bigSpace

    Box(
        modifier = Modifier
            .testTag(CardScreen2.TAG_SCREEN)
            .padding()
            .fillMaxSize(),
    ) {
        val components = questions.value.components
        val size = components.size
        components.asReversed().forEachIndexed { index, ques ->
            key(ques.id) {
                StatefulCard(
                    index = index,
                    size = size,
                    component = ques,
                    gameSetId = gameSetId,
                    bigSpace = bigSpace,
                    onClick = onClick
                )
            }
        }

        val menuHeight = Menu.height.toPx()
        val menuYOffset = (bigSpace / 2f) - (menuHeight / 2f)
        Menu(
            modifier = Modifier
                .testTag(CardScreen2.TAG_MENU_TOGGLE)
                .align(Alignment.TopCenter)
                .graphicsLayer {
                    translationY = menuYOffset
                },
            message = "Are you ready to move to the next level?",
            showMessage = showMenuMessage,
            onClick = toggleMenu,
        )

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.Center),
            visible = isMenuShown.value,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
        ) {
            LaunchedEffect(Unit) {
                showMenuMessage = false
            }
            Row {
                Button(
                    modifier = Modifier
                        .testTag(CardScreen2.TAG_EXIT),
                    onClick = onExit,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = getColor(gameSetId = gameSetId),
                    ),
                    content = {
                        Text(
                            text = "Exit?",
                            style = AppTheme.Text.primaryBold
                        )
                    }
                )
                if (isNextLevelShown.value) {
                    Spacer(modifier = Modifier.width(64.dp))
                    Button(
                        modifier = Modifier
                            .testTag(CardScreen2.TAG_NEXT_LEVEL),
                        onClick = onNextLevel,
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = getColor(gameSetId = gameSetId),
                        ),
                        content = {
                            Text(
                                text = "Next Level?",
                                style = AppTheme.Text.primaryBold
                            )
                        }
                    )
                }
            }
        }
    }
}

@SuppressLint("ClickableViewAccessibility")
@Composable
private fun BoxScope.StatefulCard(
    index: Int,
    size: Int,
    component: QuestionComponent,
    gameSetId: String,
    bigSpace: Float,
    onClick: (QuestionComponent) -> Unit,
) {
    val uiGuide = LocalUiGuide.current
    val density = LocalDensity.current
    val dragOffsetX = remember { Animatable(0f) }
    val dragOffsetY = remember { Animatable(0f) }

    var isDragThresholdReached by remember { mutableStateOf(false) }

    val rotation = remember(component.stateVector.from) {
        Animatable(
            computeRotation(component.stateVector.from)
        )
    }
    LaunchedEffect(component) {
        rotation.animateTo(
            targetValue = computeRotation(component.state),
            animationSpec = tween(
                durationMillis = AppTheme.ANIM_DURATION,
            ),
        )
    }

    val centerTextAlpha by animateFloatAsState(
        animationSpec = tween(
            durationMillis = AppTheme.ANIM_DURATION,
        ),
        targetValue = when (component.state) {
            QuestionComponent.State.Landing -> 0f
            QuestionComponent.State.PrimaryRevealed -> 1f
            QuestionComponent.State.OtherCard -> 0f
            QuestionComponent.State.Closed -> 1f
            QuestionComponent.State.PrimaryHidden -> 0f
            QuestionComponent.State.NextHidden -> 0f
            QuestionComponent.State.Disabled -> 0f
            QuestionComponent.State.Offscreen -> 0f
        },
        label = "centerTextAlpha",
    )
    val centerVerticalTextAlpha by animateFloatAsState(
        animationSpec = tween(
            durationMillis = AppTheme.ANIM_DURATION,
        ),
        targetValue = when (component.state) {
            QuestionComponent.State.Landing -> 1f
            else -> 0f
        },
        label = "centerTextAlpha",
    )
    val sideTextAlpha by animateFloatAsState(
        animationSpec = tween(
            durationMillis = AppTheme.ANIM_DURATION,
        ),
        targetValue = when (component.state) {
            QuestionComponent.State.Landing -> 1f
            QuestionComponent.State.PrimaryRevealed -> 0f
            QuestionComponent.State.OtherCard -> 1f
            QuestionComponent.State.Closed -> 0f
            QuestionComponent.State.PrimaryHidden -> 1f
            QuestionComponent.State.NextHidden -> 1f
            QuestionComponent.State.Disabled -> 1f
            QuestionComponent.State.Offscreen -> 0f
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

    val config = LocalConfiguration.current

    val stateOffsetX = remember {
        Animatable(
            computeOffsetX(
                index = component.modifiedAtSecs,
                level = component.level,
                state = component.stateVector.from,
                uiGuide = uiGuide,
            )
        )
    }
    LaunchedEffect(component) {
        stateOffsetX.animateTo(
            targetValue = computeOffsetX(
                level = component.level,
                state = component.state,
                index = component.modifiedAtSecs,
                uiGuide = uiGuide,
            ),
            animationSpec = tween(
                durationMillis = AppTheme.ANIM_DURATION,
            ),
        )
    }

    val stateOffsetY = remember {
        Animatable(
            computeOffsetY(
                level = component.level,
                state = component.stateVector.from,
                index = component.modifiedAtSecs,
                uiGuide = uiGuide,
            )
        )
    }
    LaunchedEffect(component) {
        stateOffsetY.animateTo(
            targetValue = computeOffsetY(
                index = component.modifiedAtSecs,
                level = component.level,
                state = component.state,
                uiGuide = uiGuide,
            ),
            animationSpec = tween(
                durationMillis = AppTheme.ANIM_DURATION,
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
            rotation = rotation.value,
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
            .semantics { questionState = component.state }
            .align(Alignment.TopCenter)
            .graphicsLayer {
                val offset = cardTranslationOffset()
                translationX = offset.x
                translationY = offset.y
                rotationZ = rotation.value
                scaleX = cardScale
                scaleY = cardScale
            },
        shadowElevation = 4.dp,
        centerText = component.text,
        sideText = component.level.toText(),
        centerVerticalText = component.levelDescription,
        centerVerticalTextAlpha = centerVerticalTextAlpha,
        textAlpha = centerTextAlpha,
        sideTextAlpha = sideTextAlpha,
        color = getColor(
            gameSetId = gameSetId,
            level = when (component.level) {
                QuestionComponent.Level.Easy -> Question.Level.Easy
                QuestionComponent.Level.Medium -> Question.Level.Medium
                QuestionComponent.Level.Hard -> Question.Level.Hard
            }
        ),
        onClick = {
            onClick(component)
        },
        onDragStop = {
            isDragThresholdReached = false
            if (abs(dragOffsetX.value) > dragTrigger) {
                val cardOffset = cardTranslationOffset()
                coroutineCtx.launch {
                    dragOffsetX.snapTo(0f)
                    dragOffsetY.snapTo(0f)
                    stateOffsetX.snapTo(cardOffset.x)
                    stateOffsetY.snapTo(cardOffset.y)
                    onClick(component)
                }
            } else {
                coroutineCtx.launch {
                    dragOffsetX.animateTo(0f, spring(0.5f))
                }
                coroutineCtx.launch {
                    dragOffsetY.animateTo(0f, spring(0.5f))
                }
            }
        },
        onDrag = { change, dragAmount ->
            isDragThresholdReached = abs(dragOffsetX.value) > dragTrigger ||
                abs(dragOffsetY.value) > dragTrigger
            coroutineCtx.launch {
                dragOffsetX.snapTo(dragOffsetX.value + dragAmount.x)
                dragOffsetY.snapTo(dragOffsetY.value + dragAmount.y)
            }
        }
    )
}

private fun computeOffsetX(
    index: Float,
    level: QuestionComponent.Level,
    state: QuestionComponent.State,
    uiGuide: UiGuide,
): Float {
    var offset = 0f
    val cardHeight = uiGuide.card.x
    when (state) {
        QuestionComponent.State.PrimaryRevealed -> {
            // center
        }
        QuestionComponent.State.Landing -> {
            when (level) {
                QuestionComponent.Level.Easy -> {
                    offset -= uiGuide.card.y
                    offset += cardHeight.nthGoldenChildRatio(6)
                }
                QuestionComponent.Level.Medium -> {
                    // nothing
                }
                QuestionComponent.Level.Hard -> {
                    offset += uiGuide.card.y
                    offset -= cardHeight.nthGoldenChildRatio(6)
                }
            }
        }
        QuestionComponent.State.OtherCard -> {
            offset += cardHeight + uiGuide.bigSpace
        }
        QuestionComponent.State.Closed -> {
            offset -= cardHeight + uiGuide.bigSpace
            offset += fbm(seed = index, octaves = 7) * cardHeight.nthGoldenChildRatio(7)
        }
        QuestionComponent.State.PrimaryHidden -> {
            // center
        }
        QuestionComponent.State.NextHidden -> {
            offset += cardHeight + uiGuide.bigSpace
        }
        QuestionComponent.State.Disabled -> {
            // center
        }
        QuestionComponent.State.Offscreen -> {
            // center
        }
    }
    return offset
}

private fun computeRotation(
    state: QuestionComponent.State
): Float {
    return when (state) {
        QuestionComponent.State.Landing -> 90f
        else -> 0f
    }
}

private fun computeOffsetY(
    index: Float,
    level: QuestionComponent.Level,
    state: QuestionComponent.State,
    uiGuide: UiGuide,
): Float {
    val screenH = uiGuide.displayHeight
    val cardSize = uiGuide.card.x
    val cornerOffset = cardSize.nthGoldenChildRatio(2)

    var offset = 0f
    when (state) {
        QuestionComponent.State.Landing -> {
            // correction due to rotation
            offset += (uiGuide.card.x - uiGuide.card.y) / 2
            offset += screenH
            offset -= cardSize.nthGoldenChildRatio(2)
            offset -= cardSize.nthGoldenChildRatio(4) * level.ordinal
        }
        QuestionComponent.State.Closed -> {
            offset += fbm(seed = index, octaves = 4) * cardSize.nthGoldenChildRatio(5)
            offset += uiGuide.bigSpace
        }
        QuestionComponent.State.Disabled -> {
            offset += screenH + cornerOffset
        }
        QuestionComponent.State.Offscreen -> {
            offset += screenH + 64
        }
        else -> {
            offset += uiGuide.bigSpace
        }
    }
    return offset
}

private fun QuestionComponent.Level.toText(): String =
    when (this) {
        QuestionComponent.Level.Easy -> {
            "LEVEL 1"
        }
        QuestionComponent.Level.Medium -> {
            "LEVEL 2"
        }
        QuestionComponent.Level.Hard -> {
            "LEVEL 3"
        }
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

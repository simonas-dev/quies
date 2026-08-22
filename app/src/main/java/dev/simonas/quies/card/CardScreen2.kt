@file:OptIn(ExperimentalAnimationApi::class)

package dev.simonas.quies.card

import android.annotation.SuppressLint
import android.os.SystemClock
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.simonas.quies.AppTheme
import dev.simonas.quies.AppTheme.SCREEN_SAVER_FADE_FRAC
import dev.simonas.quies.LocalUiGuide
import dev.simonas.quies.R
import dev.simonas.quies.UiGuide
import dev.simonas.quies.analytics.EventTracker
import dev.simonas.quies.analytics.eventTracker
import dev.simonas.quies.analytics.toMeta
import dev.simonas.quies.card.CardScreen2.questionState
import dev.simonas.quies.data.Question
import dev.simonas.quies.questions.getColor
import dev.simonas.quies.utils.KeepScreenOn
import dev.simonas.quies.utils.OnSystemBackClick
import dev.simonas.quies.utils.createTestTag
import dev.simonas.quies.utils.fbm
import dev.simonas.quies.utils.nthGoldenChildRatio
import dev.simonas.quies.utils.toPx
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.Math.toRadians
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

private const val TOUCH_SAMPLING_MILLIS = 1_000L

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
    onBack: () -> Unit,
    cardViewModel: CardViewModel2 = assistedMetroViewModel(),
    tracker: EventTracker = eventTracker(),
) {
    val questions = cardViewModel.questions.collectAsStateWithLifecycle()
    val isMenuShown = cardViewModel.isMenuShown.collectAsStateWithLifecycle()
    val isNextLevelShown = cardViewModel.isNextLevelShown.collectAsStateWithLifecycle()
    val isEndgame = cardViewModel.isEndgame.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        tracker.send(
            key = "card_screen_show",
            meta = mapOf(
                "game_set_id" to cardViewModel.gameSetId,
            )
        )
    }

    OnSystemBackClick {
        tracker.send("card_screen_system_back")
        cardViewModel.toggleMenu()
    }

    CardScreen2(
        gameSetId = cardViewModel.gameSetId,
        questions = questions,
        isMenuShown = isMenuShown,
        isEndgame = isEndgame,
        isNextLevelShown = isNextLevelShown,
        showLevelSkipNotice = cardViewModel.showLevelSkipNotice,
        onClick = {
            tracker.send(
                key = "card_screen_interaction",
                meta = it.toMeta(),
            )
            cardViewModel.trigger(it.id)
        },
        toggleMenu = {
            tracker.send("card_screen_toggle_menu")
            cardViewModel.toggleMenu()
        },
        onExit = {
            tracker.send("card_screen_exit")
            onBack()
        },
        onNextLevel = {
            tracker.send("card_screen_next_level")
            cardViewModel.nextLevel()
        },
    )
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun CardScreen2(
    gameSetId: String,
    questions: State<CardViewModel2.Questions>,
    isMenuShown: State<Boolean>,
    isEndgame: State<Boolean>,
    isNextLevelShown: State<Boolean>,
    onClick: (QuestionComponent) -> Unit,
    toggleMenu: () -> Unit,
    onExit: () -> Unit,
    onNextLevel: () -> Unit,
    showLevelSkipNotice: Flow<Long>,
) {
    var keepScreenOn by remember { mutableStateOf(true) }
    if (keepScreenOn) {
        KeepScreenOn()
    }

    // Monotonic clock: System.currentTimeMillis() can jump backwards (user/NTP time change),
    // which would silence the quantization guard below until the wall clock caught back up.
    var lastTouchEvent by remember { mutableLongStateOf(SystemClock.elapsedRealtime()) }

    var showMenuMessage by remember { mutableStateOf(false) }
    LaunchedEffect(showLevelSkipNotice) {
        showLevelSkipNotice.collectLatest {
            showMenuMessage = true
            delay(5.seconds)
            showMenuMessage = false
        }
    }

    val bigSpace = LocalUiGuide.current.bigSpace

    val uiAlpha = remember {
        Animatable(1f)
    }

    var isIdle by remember { mutableStateOf(false) }

    LaunchedEffect(lastTouchEvent) {
        isIdle = false
        delay(20.seconds)
        isIdle = true
    }

    LaunchedEffect(lastTouchEvent) {
        keepScreenOn = true
        delay(10.minutes)
        keepScreenOn = false
    }

    LaunchedEffect(isIdle) {
        if (isIdle) {
            uiAlpha.animateTo(
                targetValue = SCREEN_SAVER_FADE_FRAC,
                animationSpec = tween(
                    durationMillis = 50_000,
                ),
            )
        } else {
            uiAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 1_000,
                ),
            )
        }
    }

    Box(
        modifier = Modifier
            .testTag(CardScreen2.TAG_SCREEN)
            .pointerInteropFilter {
                // Quantize updates: every write restarts the idle/screen-on effects keyed on
                // this timestamp, so avoid one restart per motion event during a drag.
                val now = SystemClock.elapsedRealtime()
                if (now - lastTouchEvent > TOUCH_SAMPLING_MILLIS) {
                    lastTouchEvent = now
                }
                false
            }
            .padding()
            .fillMaxSize(),
    ) {
        val endgameQuestionAlpha by animateFloatAsState(
            animationSpec = tween(2000),
            targetValue = when {
                isMenuShown.value -> {
                    0f
                }
                isEndgame.value -> {
                    1f
                }
                else -> {
                    0f
                }
            },
            label = "endgameQuestionAlpha",
        )

        Text(
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(endgameQuestionAlpha),
            style = AppTheme.Text.primaryDemiBold,
            text = stringResource(R.string.card_endgame_question),
            textAlign = TextAlign.Center,
        )

        val uiAlphaState = remember { derivedStateOf { uiAlpha.value } }
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
                    secondaryUIAlpha = uiAlphaState,
                    onClick = onClick
                )
            }
        }

        val menuHeight = Menu.height.toPx()
        // Centered in the bigSpace band, but never above the top edge: in width-constrained
        // windows bigSpace clamps to 0 and centering alone would clip the menu's top half.
        val menuYOffset = ((bigSpace / 2f) - (menuHeight / 2f)).coerceAtLeast(0f)
        Menu(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .graphicsLayer {
                    translationY = menuYOffset
                    alpha = uiAlpha.value
                },
            message = stringResource(R.string.card_level_skip_notice),
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
                            text = stringResource(R.string.card_menu_exit),
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
                                text = stringResource(R.string.card_menu_next_level),
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
    secondaryUIAlpha: State<Float>,
    onClick: (QuestionComponent) -> Unit,
) {
    // Read here (not in the caller) so the idle-fade animation only invalidates each card's
    // scope instead of the whole screen.
    val secondaryAlpha = secondaryUIAlpha.value
    val uiGuide = LocalUiGuide.current
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
            QuestionComponent.State.Blank -> 0f
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
        label = "centerVerticalTextAlpha",
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
            QuestionComponent.State.Blank -> 0f
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
                noiseSeed = component.modifiedAtSecs,
                comp = component,
                level = component.level,
                state = component.stateVector.from,
                uiGuide = uiGuide,
            )
        )
    }
    val stateOffsetY = remember {
        Animatable(
            computeOffsetY(
                comp = component,
                level = component.level,
                state = component.stateVector.from,
                noiseSeed = component.modifiedAtSecs,
                uiGuide = uiGuide,
            )
        )
    }
    var animatedUiGuide by remember { mutableStateOf(uiGuide) }
    LaunchedEffect(component, uiGuide) {
        val targetX = computeOffsetX(
            comp = component,
            level = component.level,
            state = component.state,
            noiseSeed = component.modifiedAtSecs,
            uiGuide = uiGuide,
        )
        val targetY = computeOffsetY(
            comp = component,
            noiseSeed = component.modifiedAtSecs,
            level = component.level,
            state = component.state,
            uiGuide = uiGuide,
        )
        if (uiGuide != animatedUiGuide) {
            // Window resized (multi-window divider, fold posture): relocate to the position
            // for the new dimensions without replaying the last state transition.
            animatedUiGuide = uiGuide
            stateOffsetX.snapTo(targetX)
            stateOffsetY.snapTo(targetY)
        } else {
            launch {
                stateOffsetX.animateTo(
                    targetValue = targetX,
                    animationSpec = tween(
                        durationMillis = AppTheme.ANIM_DURATION,
                    ),
                )
            }
            launch {
                stateOffsetY.animateTo(
                    targetValue = targetY,
                    animationSpec = tween(
                        durationMillis = AppTheme.ANIM_DURATION,
                    ),
                )
            }
        }
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

    val stateDesc = component.state.toStateDescription()
    Card(
        modifier = Modifier
            .semantics {
                questionState = component.state
                stateDesc?.let { stateDescription = it }
                // Keep screen readers off cards that are visually absent.
                if (component.isOffscreen ||
                    component.state == QuestionComponent.State.Disabled ||
                    component.state == QuestionComponent.State.Blank
                ) {
                    hideFromAccessibility()
                }
            }
            .align(Alignment.TopCenter)
            .graphicsLayer {
                val offset = cardTranslationOffset()
                translationX = offset.x
                translationY = offset.y
                rotationZ = rotation.value
                scaleX = cardScale
                scaleY = cardScale
            },
        backgroundActiveness = when {
            component.state == QuestionComponent.State.Landing -> {
                1f
            }
            component.state == QuestionComponent.State.PrimaryHidden -> {
                secondaryAlpha.pow(1f / 2f)
            }
            else -> {
                secondaryAlpha
            }
        },
        textActiveness = when {
            component.state == QuestionComponent.State.Landing -> {
                1f
            }
            component.state == QuestionComponent.State.PrimaryRevealed -> {
                secondaryAlpha.pow(1f / 4f)
            }
            component.state == QuestionComponent.State.PrimaryHidden -> {
                secondaryAlpha.pow(1f / 8f)
            }
            else -> {
                secondaryAlpha
            }
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
        dragListener = object : DragListener {
            override fun onStop() {
                isDragThresholdReached = false
                if (abs(dragOffsetX.value) > dragTrigger) {
                    // Let the drag offset decay while the state transition animates the card
                    // to its next position; the two run concurrently so the visual sum stays
                    // continuous from the release point.
                    coroutineCtx.launch {
                        dragOffsetX.animateTo(0f, tween(AppTheme.ANIM_DURATION))
                    }
                    coroutineCtx.launch {
                        dragOffsetY.animateTo(0f, tween(AppTheme.ANIM_DURATION))
                    }
                    onClick(component)
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
                isDragThresholdReached = abs(dragOffsetX.value) > dragTrigger
                coroutineCtx.launch {
                    dragOffsetX.snapTo(dragOffsetX.value + dragAmount.x)
                    dragOffsetY.snapTo(dragOffsetY.value + dragAmount.y)
                }
            }
        },
    )
}

private fun computeOffsetX(
    noiseSeed: Float,
    comp: QuestionComponent,
    level: QuestionComponent.Level,
    state: QuestionComponent.State,
    uiGuide: UiGuide,
): Float {
    var offset = 0f
    val cardWidth = uiGuide.card.x
    when (state) {
        QuestionComponent.State.PrimaryRevealed -> {
            // center
        }
        QuestionComponent.State.Landing -> {
            when (level) {
                QuestionComponent.Level.Easy -> {
                    offset -= uiGuide.card.y
                    offset += cardWidth.nthGoldenChildRatio(6)
                }
                QuestionComponent.Level.Medium -> {
                    // nothing
                }
                QuestionComponent.Level.Hard -> {
                    offset += uiGuide.card.y
                    offset -= cardWidth.nthGoldenChildRatio(6)
                }
            }
        }
        QuestionComponent.State.OtherCard -> {
            offset += cardWidth + uiGuide.bigSpace
        }
        QuestionComponent.State.Closed -> {
            offset -= cardWidth + uiGuide.bigSpace
            offset += fbm(seed = noiseSeed, octaves = 7) * cardWidth.nthGoldenChildRatio(7)
        }
        QuestionComponent.State.PrimaryHidden -> {
            // center
        }
        QuestionComponent.State.NextHidden -> {
            offset += cardWidth + uiGuide.bigSpace
        }
        QuestionComponent.State.Disabled -> {
            // center
        }
        QuestionComponent.State.Blank -> {
            // center
        }
    }
    if (comp.isOffscreen) {
        offset = 0f
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
    noiseSeed: Float,
    level: QuestionComponent.Level,
    state: QuestionComponent.State,
    comp: QuestionComponent,
    uiGuide: UiGuide,
): Float {
    val screenH = uiGuide.displayHeight
    // Deliberately the card *width*: Landing cards are rotated 90 degrees, so their
    // vertical extent on screen is the card's width.
    val cardWidth = uiGuide.card.x
    val cornerOffset = cardWidth.nthGoldenChildRatio(2)

    var offset = 0f
    when (state) {
        QuestionComponent.State.Landing -> {
            // correction due to rotation
            offset += (uiGuide.card.x - uiGuide.card.y) / 2
            offset += screenH
            offset -= cardWidth.nthGoldenChildRatio(2)
            offset -= cardWidth.nthGoldenChildRatio(4) * level.ordinal
        }
        QuestionComponent.State.Closed -> {
            offset += fbm(seed = noiseSeed, octaves = 4) * cardWidth.nthGoldenChildRatio(5)
            offset += uiGuide.bigSpace
        }
        QuestionComponent.State.Disabled -> {
            offset += screenH + cornerOffset
        }
        QuestionComponent.State.Blank -> {
            offset += screenH + cornerOffset
        }
        else -> {
            offset += uiGuide.bigSpace
        }
    }
    if (comp.isOffscreen) {
        offset += screenH + cornerOffset
    }
    return offset
}

@Composable
private fun QuestionComponent.Level.toText(): String =
    stringResource(
        when (this) {
            QuestionComponent.Level.Easy -> {
                R.string.card_level_easy
            }
            QuestionComponent.Level.Medium -> {
                R.string.card_level_medium
            }
            QuestionComponent.Level.Hard -> {
                R.string.card_level_hard
            }
        }
    )

@Composable
private fun QuestionComponent.State.toStateDescription(): String? =
    when (this) {
        QuestionComponent.State.Landing -> stringResource(R.string.card_state_landing)
        QuestionComponent.State.PrimaryRevealed -> stringResource(R.string.card_state_revealed)
        QuestionComponent.State.PrimaryHidden,
        QuestionComponent.State.NextHidden,
        QuestionComponent.State.OtherCard,
        -> stringResource(R.string.card_state_hidden)
        QuestionComponent.State.Closed -> stringResource(R.string.card_state_closed)
        else -> null
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

@file:OptIn(ExperimentalAnimationApi::class)

package dev.simonas.quies.card

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.simonas.quies.AppTheme
import dev.simonas.quies.card.CardScreen2.questionState
import dev.simonas.quies.data.Question
import dev.simonas.quies.questions.getColor
import dev.simonas.quies.utils.KeepScreenOn
import dev.simonas.quies.utils.createTestTag
import dev.simonas.quies.utils.fbm
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

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

    CardScreen2(
        gameSetId = cardViewModel.gameSetId,
        questions = state,
        isMenuShown = isMenuShown,
        isNextLevelShown = isNextLevelShown,
        showLevelSkipNotice = cardViewModel.showLevelSkipNotice,
        onClick = cardViewModel::trigger,
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

    val screenWidth = LocalConfiguration.current.screenHeightDp.dp

    val verticalMargin = remember(screenWidth) {
        (screenWidth - Card.height) / 2f
    }

    Box(
        modifier = Modifier
            .testTag(CardScreen2.TAG_SCREEN)
            .padding()
            .fillMaxSize()
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
                    onClick = {
                        onClick(ques)
                    }
                )
            }
        }

        Menu(
            modifier = Modifier
                .testTag(CardScreen2.TAG_MENU_TOGGLE)
                .align(Alignment.TopCenter)
                .offset(y = verticalMargin - Overflow.width / 2f),
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

@Composable
private fun BoxScope.StatefulCard(
    index: Int,
    size: Int,
    component: QuestionComponent,
    gameSetId: String,
    onClick: () -> Unit,
) {
    val rotation = remember {
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

    val config = LocalConfiguration.current

    val animatedOffsetX = remember {
        Animatable(
            computeOffsetX(
                config = config,
                index = component.modifiedAtSecs,
                level = component.level,
                state = component.stateVector.from,
            )
        )
    }
    LaunchedEffect(component) {
        animatedOffsetX.animateTo(
            targetValue = computeOffsetX(
                config = config,
                level = component.level,
                state = component.state,
                index = component.modifiedAtSecs,
            ),
            animationSpec = tween(
                durationMillis = AppTheme.ANIM_DURATION,
            ),
        )
    }

    val animatedOffsetY = remember {
        Animatable(
            computeOffsetY(
                config = config,
                level = component.level,
                state = component.stateVector.from,
                index = component.modifiedAtSecs,
            )
        )
    }
    LaunchedEffect(component) {
        animatedOffsetY.animateTo(
            targetValue = computeOffsetY(
                config = config,
                index = component.modifiedAtSecs,
                level = component.level,
                state = component.state,
            ),
            animationSpec = tween(
                durationMillis = AppTheme.ANIM_DURATION,
            ),
        )
    }

    Card(
        modifier = Modifier
            .semantics { questionState = component.state }
            .align(Alignment.Center)
            .graphicsLayer {
                translationX = animatedOffsetX.value.dp.toPx()
                translationY = animatedOffsetY.value.dp.toPx()
                rotationZ = rotation.value
            },
        shadowElevation = 4.dp,
        color = getColor(
            gameSetId = gameSetId,
            level = when (component.level) {
                QuestionComponent.Level.Easy -> Question.Level.Easy
                QuestionComponent.Level.Medium -> Question.Level.Medium
                QuestionComponent.Level.Hard -> Question.Level.Hard
            }
        ),
        centerText = component.text,
        centerVerticalText = component.levelDescription,
        centerVerticalTextAlpha = centerVerticalTextAlpha,
        sideText = component.level.toText(),
        textAlpha = centerTextAlpha,
        sideTextAlpha = sideTextAlpha,
        onClick = {
            onClick()
        }
    )
}

private fun computeOffsetX(
    config: Configuration,
    index: Float,
    level: QuestionComponent.Level,
    state: QuestionComponent.State,
): Float {
    var offset = 0f
    when (state) {
        QuestionComponent.State.PrimaryRevealed -> {
            // center
        }
        QuestionComponent.State.Landing -> {
            when (level) {
                QuestionComponent.Level.Easy -> {
                    offset -= 470 / 2
                }
                QuestionComponent.Level.Medium -> {
                    // nothing
                }
                QuestionComponent.Level.Hard -> {
                    offset += 470 / 2
                }
            }
        }
        QuestionComponent.State.OtherCard -> {
            offset += 470 + 64
        }
        QuestionComponent.State.Closed -> {
            offset -= 470 + 64
            offset += fbm(seed = index, octaves = 7) * 32
        }
        QuestionComponent.State.PrimaryHidden -> {
            // center
        }
        QuestionComponent.State.NextHidden -> {
            offset += 470 + 64
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
    config: Configuration,
    index: Float,
    level: QuestionComponent.Level,
    state: QuestionComponent.State,
): Float {
    var offset = 0f
    when (state) {
        QuestionComponent.State.Landing -> {
            offset += 64 * 4
            offset -= 64 * level.ordinal
        }
        QuestionComponent.State.Closed -> {
            offset += fbm(seed = index, octaves = 4) * 32
        }
        QuestionComponent.State.Disabled -> {
            offset += config.screenHeightDp + 64
        }
        QuestionComponent.State.Offscreen -> {
            offset += config.screenHeightDp + 64
        }
        else -> {
            // center
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

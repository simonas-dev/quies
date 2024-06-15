package dev.simonas.quies.uiglitter

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

fun revealingAnnotatedText(
    state: State<List<Pair<Char, Float>>>,
    style: TextStyle,
): AnnotatedString =
    buildAnnotatedString {
        state.value.forEach { (char, alpha) ->
            val spanStyle = style.toSpanStyle().let {
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
    }

@Composable
fun revealingTextState(
    text: String,
    isVisible: Boolean,
    durationPerChar: Float = 40f,
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
                    durationMillis = (text.length * durationPerChar).toInt(),
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

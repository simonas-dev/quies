package dev.simonas.quies.onboarding

import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.simonas.quies.AppTheme
import dev.simonas.quies.onboarding.CanvasComponent.Vals
import dev.simonas.quies.utils.alpha
import dev.simonas.quies.utils.cubicBezier
import dev.simonas.quies.utils.hill
import dev.simonas.quies.utils.lerp
import dev.simonas.quies.utils.middle
import dev.simonas.quies.utils.rangeNorm
import kotlin.math.sin

interface CanvasComponent {

    context(DrawScope)
    fun draw(progress: Float, isGlitch: Boolean): Vals

    data class Vals(
        val glichyness: Float,
        val visibility: Float,
    )
}

private const val DIALOG_HILL_SIDE = 0.125f
private const val GLITCH_HILL_SIDE = 0.2f
private val GLITCH_INTERPOLATOR = cubicBezier(.65f, .45f)

private fun glitch(
    index: Int,
    progress: Float,
    glitchNorm: () -> Float = {
        progress.rangeNorm(
            index - GLITCH_HILL_SIDE..index + GLITCH_HILL_SIDE,
            (index + 1) - GLITCH_HILL_SIDE..(index + 1f)
        )
    },
): Float {
    return glitchNorm()
        .let {
            when {
                it > 0.01f -> 1f - it
                else -> 0f
            }
        }
        .hill(0.49f)
        .let {
            GLITCH_INTERPOLATOR.invoke(it)
        }
}

class Splash(
    private val textMeasurer: TextMeasurer,
    private val text: String,
    private val textOffset: Offset,
    private val enter: ClosedRange<Float>,
    private val exit: ClosedRange<Float>,
) : CanvasComponent {

    context(DrawScope)
    override fun draw(progress: Float, isGlitch: Boolean): Vals {
        val vals = Vals(
            glichyness = progress.rangeNorm(
                enter,
                exit,
            ).let {
                when {
                    it > 0.01f -> 1f - it
                    else -> 0f
                }
            }.hill(0.5f),
            visibility = progress
                .rangeNorm(enter.middle()..exit.middle())
                .hill(0.5f)
                .times(0.8f),
        )

        drawDialogLine(
            textMeasurer,
            isGlitch = isGlitch,
            text = text,
            vals = vals,
            offset = textOffset,
            textSizeMulti = 0.75f,
            glitchPaleness = 0f,
            offsetMultiplierX = 2f,
            offsetMultiplierY = 2f,
        )
        return vals.copy(
            glichyness = vals.glichyness,
        )
    }
}

class OpenerDialogLine(
    private val textMeasurer: TextMeasurer,
) : CanvasComponent {

    context(DrawScope)
    override fun draw(progress: Float, isGlitch: Boolean): Vals {
        val vals = Vals(
            glichyness = glitch(1, progress),
            visibility = progress
                .rangeNorm(0.80f..1.95f)
                .hill(DIALOG_HILL_SIDE),
        )
        drawDialogLine(
            textMeasurer,
            isGlitch = isGlitch,
            text = "This game is about creating meaningful dialogues",
            vals = vals,
        )
        return vals
    }
}

class ChillOutDialogLine(
    private val textMeasurer: TextMeasurer,
) : CanvasComponent {

    context(DrawScope)
    override fun draw(progress: Float, isGlitch: Boolean): Vals {
        val vals = Vals(
            glichyness = glitch(2, progress),
            visibility = progress
                .rangeNorm(2.00f..2.95f)
                .hill(DIALOG_HILL_SIDE),
        )
        drawDialogLine(
            textMeasurer,
            isGlitch = isGlitch,
            text = "Slowly getting the conversation to the next level",
            vals = vals,
        )
        return vals
    }
}

class ExcuseDialogLine(
    private val textMeasurer: TextMeasurer,
) : CanvasComponent {

    context(DrawScope)
    override fun draw(progress: Float, isGlitch: Boolean): Vals {
        val vals = Vals(
            glichyness = glitch(3, progress),
            visibility = progress
                .rangeNorm(3.00f..3.95f)
                .hill(DIALOG_HILL_SIDE),
        )
        drawDialogLine(
            textMeasurer,
            isGlitch = isGlitch,
            text = "An excuse to talk about things you’ve never had",
            vals = vals,
        )
        return vals
    }
}

class LetItOutDialogLine(
    private val textMeasurer: TextMeasurer,
) : CanvasComponent {

    context(DrawScope)
    override fun draw(progress: Float, isGlitch: Boolean): Vals {
        val vals = Vals(
            glichyness = glitch(4, progress),
            visibility = progress
                .rangeNorm(4.00f..4.95f)
                .hill(DIALOG_HILL_SIDE),
        )
        drawDialogLine(
            textMeasurer,
            isGlitch = isGlitch,
            text = "Share things we have kept to ourselves",
            vals = vals,
        )
        return vals
    }
}

class ConnectionDialogLine(
    private val textMeasurer: TextMeasurer,
) : CanvasComponent {

    context(DrawScope)
    override fun draw(progress: Float, isGlitch: Boolean): Vals {
        val vals = Vals(
            glichyness = glitch(5, progress),
            visibility = progress
                .rangeNorm(5.00f..5.95f)
                .hill(DIALOG_HILL_SIDE),
        )
        drawDialogLine(
            textMeasurer,
            isGlitch = isGlitch,
            text = "Build a bridge between two or more inner worlds",
            vals = vals,
        )
        return vals
    }
}

class OutroDialogLine(
    private val textMeasurer: TextMeasurer,
) : CanvasComponent {

    context(DrawScope)
    override fun draw(progress: Float, isGlitch: Boolean): Vals {
        val vals = Vals(
            glichyness = glitch(
                index = 6,
                progress = progress,
                glitchNorm = {
                    progress.rangeNorm(
                        6f - GLITCH_HILL_SIDE..6f + GLITCH_HILL_SIDE,
                        6.8f - GLITCH_HILL_SIDE..6.9f
                    )
                }
            ),
            visibility = progress
                .rangeNorm(6.00f..6.95f)
                .hill(DIALOG_HILL_SIDE),
        )
        drawDialogLine(
            textMeasurer,
            isGlitch = isGlitch,
            text = "Be honest\nShare your feelings\nThat’s the way to win",
            vals = vals,
        )
        return vals
    }
}

@Suppress("LongMethod")
private fun DrawScope.drawDialogLine(
    textMeasurer: TextMeasurer,
    isGlitch: Boolean,
    text: String,
    offset: Offset = Offset(0f, 0f),
    offsetMultiplierX: Float = 12f,
    offsetMultiplierY: Float = 12f,
    textSizeMulti: Float = 1f,
    vals: Vals,
    glitchPaleness: Float = 1f - vals.glichyness,
    glitchTransparency: Float = vals.glichyness.minus(0.1f).coerceAtLeast(0f)

) {
    val style = AppTheme.Text.primaryBlack
        .copy(
            fontSize = (26 * textSizeMulti).sp,
            textMotion = TextMotion.Animated,
            shadow = null,
        )
    val size = textMeasurer.measure(
        text = text,
        style = style,
    )
    val topY = size.getLineTop(0)
    val bottomY = size.getLineBottom(size.lineCount - 1)
    val textH = bottomY - topY
    val textOffset = derivedStateOf {
        Offset(
            x = 64.dp.toPx(),
            y = center.y - (textH / 2f),
        )
    }

    if (isGlitch) {
        drawText(
            textMeasurer = textMeasurer,
            text = text,
            style = style
                .copy(
                    color = AppTheme.Color.dating
                        .lerp(style.color, glitchPaleness)
                        .alpha(glitchTransparency),
                ),
            topLeft = textOffset.value + offset +
                Offset(
                    x = offsetMultiplierX
                        .times(vals.glichyness)
                        .times(sin(vals.glichyness + 56456f + text.hashCode().toFloat()))
                        .dp.toPx(),
                    y = offsetMultiplierY
                        .times(vals.glichyness)
                        .times(sin(vals.glichyness + 56456f + text.hashCode().toFloat()))
                        .dp.toPx(),
                ),
        )

        drawText(
            textMeasurer = textMeasurer,
            text = text,
            style = style
                .copy(
                    color = AppTheme.Color.friends
                        .lerp(style.color, glitchPaleness)
                        .alpha(glitchTransparency),
                ),
            topLeft = textOffset.value + offset +
                Offset(
                    x = offsetMultiplierX
                        .times(vals.glichyness)
                        .times(sin(vals.glichyness + 7456f + text.hashCode().toFloat()))
                        .dp.toPx(),
                    y = offsetMultiplierY
                        .times(vals.glichyness)
                        .times(sin(vals.glichyness + 7658f + text.hashCode().toFloat()))
                        .dp.toPx(),
                ),
        )
        drawText(
            textMeasurer = textMeasurer,
            text = text,
            style = style
                .copy(
                    color = AppTheme.Color.debate
                        .lerp(style.color, glitchPaleness)
                        .alpha(glitchTransparency),
                ),
            topLeft = textOffset.value + offset +
                Offset(
                    x = offsetMultiplierX
                        .times(vals.glichyness)
                        .times(sin(vals.glichyness + 34456f + text.hashCode().toFloat()))
                        .dp.toPx(),
                    y = offsetMultiplierY
                        .times(vals.glichyness)
                        .times(sin(vals.glichyness + 26356f + text.hashCode().toFloat()))
                        .dp.toPx(),
                ),
        )
    } else {
        val color = AppTheme.Text.primaryBlack.color
            .alpha(vals.visibility)
        drawText(
            textMeasurer = textMeasurer,
            text = text,
            style = style.copy(
                color = color,
                shadow = Shadow(
                    color = color,
                    blurRadius = 2.dp.toPx(),
                )
            ),
            topLeft = textOffset.value + offset,
        )
    }
}

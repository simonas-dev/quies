package dev.simonas.quies.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

fun Color.alpha(frac: Float = 0.0f): Color {
    return this.copy(
        alpha = frac,
    )
}

fun Color.lerp(b: Color, frac: Float = 0.5f): Color {
    return Color(
        red = lerp(red, b.red, frac),
        green = lerp(green, b.green, frac),
        blue = lerp(blue, b.blue, frac),
        alpha = lerp(alpha, b.alpha, frac),
    )
}

fun Color.lerpNonAlpha(b: Color, frac: Float = 0.5f): Color {
    return Color(
        red = lerp(red, b.red, frac),
        green = lerp(green, b.green, frac),
        blue = lerp(blue, b.blue, frac),
        alpha = alpha,
    )
}

private fun lerp(a: Float, b: Float, frac: Float = 0.5f): Float {
    return (a * (1f - frac)) + (b * frac)
}

fun TextStyle.mutColor(exec: (Color) -> Color): TextStyle =
    copy(
        color = exec(this.color),
        shadow = this.shadow?.let { shadow ->
            shadow.copy(
                color = exec(shadow.color)
            )
        }
    )

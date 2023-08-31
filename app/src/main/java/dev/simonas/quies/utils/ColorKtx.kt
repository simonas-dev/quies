package dev.simonas.quies.utils

import androidx.compose.ui.graphics.Color

fun Color.lerp(b: Color, frac: Float = 0.5f): Color {
    return Color(
        red = this.red.lerp(b.red, frac),
        green = this.green.lerp(b.green, frac),
        blue = this.blue.lerp(b.blue, frac),
        alpha = this.alpha.lerp(b.alpha, frac),
    )
}

fun Float.lerp(b: Float, frac: Float = 0.5f): Float {
    return this + (b - this) * frac
}

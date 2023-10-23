package dev.simonas.quies

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.dp
import dev.romainguy.kotlin.math.Float2
import dev.simonas.quies.utils.toPx

val LocalUiGuide = compositionLocalOf { UiGuide() }

data class UiGuide(
    val card: Float2 = Float2(0f),
    val smallSpace: Float = 0f,
    val bigSpace: Float = 0f,
    val displayHeight: Float = 0f,
    val displayWidth: Float = 0f,
)

@Composable
fun uiGuide(
    spaceHeight: Float,
    spaceWidth: Float,
): UiGuide {
    val card = Float2(
        x = 428.77.dp.toPx(),
        y = 265.dp.toPx(),
    )
    val smallSpace = smallSpacing(
        screenHeight = spaceHeight,
        cardHeight = card.y,
    )
    val bigSpace = bigSpacing(
        screenHeight = spaceHeight,
        cardHeight = card.y,
    )
    return UiGuide(
        card = card,
        smallSpace = smallSpace,
        bigSpace = bigSpace,
        displayHeight = spaceHeight,
        displayWidth = spaceWidth,
    )
}

private const val GOLDEN_RATIO = 1.61803f

private fun bigSpacing(screenHeight: Float, cardHeight: Float): Float {
    val freeSpace = screenHeight - cardHeight
    return freeSpace / GOLDEN_RATIO
}

private fun smallSpacing(screenHeight: Float, cardHeight: Float): Float {
    val freeSpace = screenHeight - cardHeight
    return freeSpace - (freeSpace / GOLDEN_RATIO)
}

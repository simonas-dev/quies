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
    val maxCardWidth = 428.77.dp.toPx()
    val maxCardHeight = 265.dp.toPx()
    // Shrink the card to fit small windows (multi-window, foldables) preserving its aspect
    // ratio; a no-op on typical landscape phone displays.
    val fitScale = when {
        spaceWidth <= 0f || spaceHeight <= 0f -> 1f
        else -> minOf(
            1f,
            (spaceWidth * CARD_MAX_WIDTH_FRAC) / maxCardWidth,
            (spaceHeight * CARD_MAX_HEIGHT_FRAC) / maxCardHeight,
        )
    }
    val card = Float2(
        x = maxCardWidth * fitScale,
        y = maxCardHeight * fitScale,
    )
    val smallSpace = smallSpacing(
        screenHeight = spaceHeight,
        cardHeight = card.y,
    )
    var bigSpace = bigSpacing(
        screenHeight = spaceHeight,
        cardHeight = card.y,
    )
    val minWidthReq = bigSpace + (card.x / 2f) + (card.x / 5f)
    if (minWidthReq > (spaceWidth / 2f)) {
        val offset = minWidthReq - (spaceWidth / 2f)
        // Never go negative: that would pull "off-screen" cards toward the center and push
        // the menu above the top edge.
        bigSpace = (bigSpace - offset).coerceAtLeast(0f)
    }
    return UiGuide(
        card = card,
        smallSpace = smallSpace,
        bigSpace = bigSpace,
        displayHeight = spaceHeight,
        displayWidth = spaceWidth,
    )
}

private const val GOLDEN_RATIO = 1.61803f
private const val CARD_MAX_WIDTH_FRAC = 0.9f
private const val CARD_MAX_HEIGHT_FRAC = 0.85f

private fun bigSpacing(screenHeight: Float, cardHeight: Float): Float {
    val freeSpace = screenHeight - cardHeight
    return freeSpace / GOLDEN_RATIO
}

private fun smallSpacing(screenHeight: Float, cardHeight: Float): Float {
    val freeSpace = screenHeight - cardHeight
    return freeSpace - (freeSpace / GOLDEN_RATIO)
}

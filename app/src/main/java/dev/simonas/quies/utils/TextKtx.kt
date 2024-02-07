package dev.simonas.quies.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.layout
import kotlin.math.roundToInt

fun Modifier.offsetFromFirstBaseline(
    x: Float,
    y: Float,
): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    check(placeable[FirstBaseline] != AlignmentLine.Unspecified)
    val firstBaseline = placeable[FirstBaseline]

    layout(width = placeable.width, height = placeable.height + firstBaseline) {
        placeable.placeRelative(
            x = x.roundToInt(),
            y = when {
                y > 0 -> y.roundToInt() - firstBaseline
                y < 0 -> y.roundToInt() + (firstBaseline * 2)
                else -> 0
            }
        )
    }
}

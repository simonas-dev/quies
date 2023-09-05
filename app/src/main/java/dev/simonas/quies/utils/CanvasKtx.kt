package dev.simonas.quies.utils

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.simonas.quies.AppTheme

@Composable
@Preview
private fun PreviewCross() {
    Canvas(
        Modifier
            .background(AppTheme.Color.dating)
            .height(64.dp)
            .width(64.dp)
    ) {
        drawCross(
            centerX = size.width / 2f,
            centerY = size.height / 2f,
            size = 8.dp.toPx(),
            stroke = 2.dp.toPx(),
        )
    }
}

fun DrawScope.drawCross(
    centerX: Float,
    centerY: Float,
    size: Float,
    stroke: Float
) {
    val radius = size / 2f
    val path = Path().apply {
        moveTo(
            x = centerX,
            y = centerY - stroke,
        )
        lineTo(
            x = centerX + radius - stroke,
            y = centerY - radius,
        )
        lineTo(
            x = centerX + radius,
            y = centerY - radius + stroke,
        )
        lineTo(
            x = centerX + stroke,
            y = centerY,
        )
        lineTo(
            x = centerX + radius,
            y = centerY + radius - stroke,
        )
        lineTo(
            x = centerX + radius - stroke,
            y = centerY + radius,
        )
        lineTo(
            x = centerX,
            y = centerY + stroke,
        )
        lineTo(
            x = centerX - radius + stroke,
            y = centerY + radius,
        )
        lineTo(
            x = centerX - radius,
            y = centerY + radius - stroke,
        )
        lineTo(
            x = centerX - stroke,
            y = centerY,
        )
        lineTo(
            x = centerX - radius,
            y = centerY - radius + stroke,
        )
        lineTo(
            x = centerX - radius + stroke,
            y = centerY - radius,
        )
        close()
    }
    drawPath(
        path = path,
        color = AppTheme.Color.whiteMedium,
    )
}

fun DrawScope.drawSq(
    centerX: Float,
    centerY: Float,
    size: Float,
) {
    val radius = size / 2f
    drawRect(
        color = AppTheme.Color.whiteMedium,
        topLeft = Offset(
            x = centerX - radius,
            y = centerY - radius,
        ),
        size = Size(size, size),
    )
}

fun DrawScope.drawOval(
    centerX: Float,
    centerY: Float,
    size: Float,
) {
    drawOval(
        color = AppTheme.Color.whiteMedium,
        topLeft = Offset(
            x = centerX - (size / 2),
            y = centerY - (size / 2),
        ),
        size = Size(size, size),
    )
}

fun DrawScope.drawTriangle(
    centerX: Float,
    centerY: Float,
    size: Float,
) {
    val radius = size / 2f
    val path = Path().apply {
        moveTo(
            x = centerX - radius,
            y = centerY + radius,
        )
        lineTo(
            x = centerX - radius,
            y = centerY + radius,
        )
        lineTo(
            x = centerX,
            y = centerY - radius,
        )
        lineTo(
            x = centerX + radius,
            y = centerY + radius,
        )
        lineTo(
            x = centerX - radius,
            y = centerY + radius,
        )
        close()
    }
    drawPath(
        path = path,
        color = AppTheme.Color.whiteMedium,
    )
}

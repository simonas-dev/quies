package dev.simonas.quies.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.simonas.quies.card.Card.TAG_TEXT
import dev.simonas.quies.template.QColors

private const val CARD_HEIGHT = 300
private const val GOLDEN_RATIO = 1.618033

internal object Card {
    const val TAG_TEXT = "text"
}

@Composable
internal fun Card(
    modifier: Modifier = Modifier,
    text: String,
    onClick: (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier
            .width((CARD_HEIGHT * GOLDEN_RATIO).dp)
            .height(CARD_HEIGHT.dp)
            .clip(RoundedCornerShape(CornerSize(64.dp)))
            .background(QColors.cardBackground)
            .let {
                if (onClick != null) {
                    it.clickable(onClick = onClick)
                } else {
                    it
                }
            },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(QColors.cardBackground)
        ) {
            Text(
                modifier = Modifier
                    .testTag(TAG_TEXT)
                    .align(Alignment.Center)
                    .padding(32.dp),
                color = QColors.cardTextColor,
                text = text,
                fontSize = 32.sp,
                lineHeight = 40.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview
@Composable
private fun PreviewCard() {
    Card(
        text = "Can we ship this app already?",
    )
}

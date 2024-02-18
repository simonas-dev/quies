package dev.simonas.quies.card

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import dev.simonas.quies.UITest

internal fun UITest.showsCardScreen() {
    onNodeWithTag(CardScreen2.TAG_SCREEN)
        .assertIsDisplayed()
}

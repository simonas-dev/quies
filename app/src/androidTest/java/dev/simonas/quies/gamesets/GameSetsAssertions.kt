package dev.simonas.quies.gamesets

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import dev.simonas.quies.UITest

internal fun UITest.showsGameSetsScreen() {
    onNodeWithTag(GameSetsScreen.TAG_SCREEN)
        .assertIsDisplayed()
}

internal fun UITest.showsGameSets() {
    onNodeWithText("DATING")
        .assertIsDisplayed()
    onNodeWithText("FRIENDS")
        .assertIsDisplayed()
    onNodeWithText("DEBATE")
        .assertIsDisplayed()
}
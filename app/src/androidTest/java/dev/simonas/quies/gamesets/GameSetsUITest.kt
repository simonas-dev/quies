package dev.simonas.quies.gamesets

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import dev.simonas.quies.UITest
import dev.simonas.quies.card.CardScreen
import org.junit.Test

@HiltAndroidTest
class GameSetsUITest : UITest() {

    @Test
    fun init() {
        showsGameSets()
    }

    @Test
    fun clicksOnGameSet() {
        onNodeWithText("DATING")
            .performClick()

        showsCardScreen()
    }

    private fun showsGameSets() {
        onNodeWithText("DATING")
            .assertIsDisplayed()
        onNodeWithText("FRIENDSHIP")
            .assertIsDisplayed()
        onNodeWithText("DEBATE")
            .assertIsDisplayed()
    }

    private fun showsCardScreen() {
        onNodeWithTag(CardScreen.TAG_SCREEN)
    }
}
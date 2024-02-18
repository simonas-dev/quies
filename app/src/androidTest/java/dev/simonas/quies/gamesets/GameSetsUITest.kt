package dev.simonas.quies.gamesets

import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import dev.simonas.quies.UITest
import dev.simonas.quies.card.showsCardScreen
import org.junit.Test

@HiltAndroidTest
internal class GameSetsUITest : UITest() {

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
}
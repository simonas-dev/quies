package dev.simonas.quies.router

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import dagger.hilt.android.testing.HiltAndroidTest
import dev.simonas.quies.UITest
import dev.simonas.quies.gamesets.GameSetsScreen
import org.junit.Test

@HiltAndroidTest
class RouterUITest : UITest() {

    @Test
    fun init() {
        showsGameSetsScreen()
    }

    private fun showsGameSetsScreen() {
        onNodeWithTag(GameSetsScreen.TAG_SCREEN)
            .assertIsDisplayed()
    }
}

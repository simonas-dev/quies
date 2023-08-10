package dev.simonas.quies.router

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import dev.simonas.quies.MainActivity
import dev.simonas.quies.UITest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
class RouterUITest : UITest() {

    @Test
    fun init() {
        showsRouterScreen()
    }

    private fun showsRouterScreen() {
        onNodeWithTag(RouterScreen.TAG_SCREEN)
            .assertIsDisplayed()
    }
}

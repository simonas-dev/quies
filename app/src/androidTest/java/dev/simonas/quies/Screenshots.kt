package dev.simonas.quies

import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Ignore
import org.junit.Test

@HiltAndroidTest
class Screenshots : UITest() {

    @Ignore("Needs Impl")
    @Test
    fun clicksOnGameSet() {
        captureScreenshot("home")

        onNodeWithText("DATING")
            .performClick()


        captureScreenshot("level_selection")

        onNodeWithText("LEVEL 1")
            .performClick()

        captureScreenshot("show_question")

        onNodeWithText("LEVEL 1")
            .performClick()

        captureScreenshot("pick_question")
    }

    private fun captureScreenshot(key: String) {
        // TODO: capture screenshot
        waitForIdle()
        Thread.sleep(10_000)
    }
}
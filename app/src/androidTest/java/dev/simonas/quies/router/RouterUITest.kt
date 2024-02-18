package dev.simonas.quies.router

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import dagger.hilt.android.testing.HiltAndroidTest
import dev.simonas.quies.UITest
import dev.simonas.quies.gamesets.GameSetsScreen
import dev.simonas.quies.onboarding.OnboardingRepository.Companion.KEY_ONBOARDING_COMPLETED
import dev.simonas.quies.onboarding.OnboardingScreen
import dev.simonas.quies.storage.Store
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import org.junit.Test

@HiltAndroidTest
internal class RouterUITest : UITest() {

    @Test
    fun firstAppLaunch() {
        runBlocking {
            store.reset()
        }
        restartApp()

        showsOnboardingScreen()
    }

    @Test
    fun onboardingCompleted() {
        runBlocking {
            store.set(KEY_ONBOARDING_COMPLETED, "true")
        }
        restartApp()

        showsGameSetsScreen()
    }

    private fun showsGameSetsScreen() {
        onNodeWithTag(GameSetsScreen.TAG_SCREEN)
            .assertIsDisplayed()
    }

    private fun showsOnboardingScreen() {
        onNodeWithTag(OnboardingScreen.TAG_SCREEN)
            .assertIsDisplayed()
    }
}

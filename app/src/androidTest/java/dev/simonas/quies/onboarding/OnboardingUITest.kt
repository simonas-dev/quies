package dev.simonas.quies.onboarding

import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import dagger.hilt.android.testing.HiltAndroidTest
import dev.simonas.quies.UITest
import dev.simonas.quies.gamesets.GameSetsScreen
import dev.simonas.quies.gamesets.showsGameSetsScreen
import dev.simonas.quies.onboarding.OnboardingRepository.Companion.KEY_ONBOARDING_COMPLETED
import dev.simonas.quies.storage.Store
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import org.junit.Test

@HiltAndroidTest
internal class OnboardingUITest : UITest() {

    override val skipsOnboarding: Boolean = false

    @Ignore("TestLab False Negative")
    @Test
    fun completesOnboarding() {
        userCompletesOnboarding()

        showsGameSetsScreen()
    }
}

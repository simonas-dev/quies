package dev.simonas.quies

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import dev.simonas.quies.onboarding.OnboardingRepository
import dev.simonas.quies.storage.Store
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule

internal typealias ActivityRule = AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>

internal abstract class UITest(
    val composeRule: ActivityRule = createAndroidComposeRule<MainActivity>()
) : ComposeContentTestRule by composeRule {

    open val skipsOnboarding: Boolean = true

    @get:Rule
    val rule = composeRule

    val store: Store
        get() = (ApplicationProvider.getApplicationContext<TestApp>()).store

    @Before
    fun setUpTestApp() {
        runBlocking {
            if (skipsOnboarding) {
                store.set(OnboardingRepository.KEY_ONBOARDING_COMPLETED, "true")
            } else {
                store.reset()
            }
        }
        restartApp()
    }

    fun restartApp() {
        composeRule.activityRule.scenario.recreate()
    }
}

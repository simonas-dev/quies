package dev.simonas.quies

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.simonas.quies.onboarding.OnboardingRepository
import dev.simonas.quies.storage.Store
import dev.simonas.quies.utils.instrumentationExec
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Before
import org.junit.Rule
import org.junit.rules.RuleChain

internal typealias ActivityRule = AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>

/**
 * Children must be annotated with [HiltAndroidTest]
 */
internal abstract class UITest(
    val composeRule: ActivityRule = createAndroidComposeRule<MainActivity>()
): ComposeContentTestRule by composeRule {

    open val skipsOnboarding: Boolean = true

    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val rule = RuleChain
        .outerRule(hiltRule)
        .around(composeRule)

    @Inject
    lateinit var store: Store

    @Before
    fun hiltInit() {
        hiltRule.inject()
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

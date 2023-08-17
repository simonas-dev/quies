package dev.simonas.quies

import android.Manifest
import android.content.pm.ActivityInfo
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.tooling.preview.Devices
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.device.DeviceController
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.runner.RunWith

/**
 * Children must be annotated with [HiltAndroidTest]
 */
abstract class ComponentTest(
    private val androidRule: AndroidRule<ComponentActivity> = createAndroidComposeRule(),
    @get:Rule
    val composeRule: ComposeContentTestRule = androidRule,
): ComposeContentTestRule by composeRule {

    @Before
    fun setLandscape() {
        androidRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }
}

private typealias AndroidRule<A> = AndroidComposeTestRule<ActivityScenarioRule<A>, A>
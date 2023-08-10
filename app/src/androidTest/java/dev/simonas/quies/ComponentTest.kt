package dev.simonas.quies

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

/**
 * Children must be annotated with [HiltAndroidTest]
 */
abstract class ComponentTest(
    @get:Rule
    val composeRule: ComposeContentTestRule = createComposeRule(),
): ComposeContentTestRule by composeRule

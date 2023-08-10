package dev.simonas.quies

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.rules.RuleChain

/**
 * Children must be annotated with [HiltAndroidTest]
 */
abstract class UITest(
    val composeRule: ComposeContentTestRule = createAndroidComposeRule<MainActivity>()
): ComposeContentTestRule by composeRule {

    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val rule = RuleChain
        .outerRule(hiltRule)
        .around(composeRule)

    @Before
    fun hiltInit() {
        hiltRule.inject()
    }
}

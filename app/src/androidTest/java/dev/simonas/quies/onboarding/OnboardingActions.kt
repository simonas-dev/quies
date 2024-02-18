package dev.simonas.quies.onboarding

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import dev.simonas.quies.UITest

internal fun UITest.userCompletesOnboarding() {
    onNodeWithTag(OnboardingScreen.TAG_SCREEN)
        .performClick()
}
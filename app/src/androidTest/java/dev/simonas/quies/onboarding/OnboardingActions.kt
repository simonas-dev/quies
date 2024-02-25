package dev.simonas.quies.onboarding

import androidx.compose.ui.test.click
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import dev.simonas.quies.UITest

internal fun UITest.userCompletesOnboarding() {
    repeat(7) {
        onNodeWithTag(OnboardingScreen.TAG_SCREEN)
            .performTouchInput { click(centerRight) }
    }

}
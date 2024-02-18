package dev.simonas.quies.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import dev.simonas.quies.AppTheme
import dev.simonas.quies.utils.QDevices
import dev.simonas.quies.utils.createTestTag

internal object OnboardingScreen {
    val TAG_SCREEN = createTestTag("screen")
}

@Composable
internal fun OnboardingScreen(
    onboardingCompleted: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val state = viewModel
    OnboardingScreen(
        modifier = Modifier.testTag(OnboardingScreen.TAG_SCREEN),
        on = { interaction ->
            viewModel.on(interaction)
            if (interaction == OnboardingViewModel.Interaction.EndOnboarding) {
                onboardingCompleted()
            }
        }
    )
}

@Composable
private fun OnboardingScreen(
    on: (OnboardingViewModel.Interaction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable {
                on(OnboardingViewModel.Interaction.EndOnboarding)
            }
            .background(AppTheme.Color.background)
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            style = AppTheme.Text.primaryBlack,
            text = "This game is about creating meaningful dialogues",
        )
    }
}

@Composable
@Preview(
    device = QDevices.LANDSCAPE,
)
private fun PreviewOnboardingScreen() {
    AppTheme {
        OnboardingScreen(
            on = {},
        )
    }
}

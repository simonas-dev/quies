package dev.simonas.quies.onboarding

import androidx.lifecycle.ViewModel
import dev.simonas.quies.AppCoroutineScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.launch

@ContributesIntoMap(AppScope::class)
@ViewModelKey
@Inject
class OnboardingViewModel(
    private val onboardingRepository: OnboardingRepository,
    private val appScope: AppCoroutineScope,
) : ViewModel() {

    fun on(interaction: Interaction) {
        when (interaction) {
            is Interaction.EndOnboarding -> endOnboarding()
        }
    }

    private fun endOnboarding() {
        appScope.launch {
            onboardingRepository.markCompleted()
        }
    }

    sealed class Interaction {
        data object EndOnboarding : Interaction()
    }
}

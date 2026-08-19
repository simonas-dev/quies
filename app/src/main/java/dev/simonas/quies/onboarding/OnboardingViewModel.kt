package dev.simonas.quies.onboarding

import androidx.lifecycle.ViewModel
import dev.simonas.quies.CoroutineAppScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.launch

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class OnboardingViewModel(
    private val onboardingRepository: OnboardingRepository,
    private val appScope: CoroutineAppScope,
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

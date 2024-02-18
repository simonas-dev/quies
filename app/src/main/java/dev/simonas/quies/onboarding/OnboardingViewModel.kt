package dev.simonas.quies.onboarding

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.simonas.quies.AppScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
    private val appScope: AppScope,
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

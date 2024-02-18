package dev.simonas.quies.router

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.simonas.quies.onboarding.OnboardingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouterScreenViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    init {
        viewModelScope.launch {
            onboardingRepository.isOnboardingCompleted()
                .collectLatest(::onOnboarding)
        }
    }

    private fun onOnboarding(isCompleted: Boolean) {
        val dest = when {
            isCompleted -> State.Dest.GameSet
            else -> State.Dest.Onboarding
        }
        _state.setDest(dest)
    }

    data class State(
        val startDestination: Dest? = null,
    ) {
        sealed class Dest {
            data object GameSet : Dest()
            data object Onboarding : Dest()
        }
    }
}

private fun MutableStateFlow<RouterScreenViewModel.State>.setDest(
    dest: RouterScreenViewModel.State.Dest,
) {
    update {
        it.copy(
            startDestination = dest,
        )
    }
}

package dev.simonas.quies.onboarding

import dev.simonas.quies.storage.Store
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OnboardingRepository @Inject constructor(
    private val store: Store,
) {
    fun isOnboardingCompleted(): Flow<Boolean> {
        return store.get(KEY_ONBOARDING_COMPLETED)
            .map { it == "true" }
    }

    suspend fun markCompleted() {
        store.set(KEY_ONBOARDING_COMPLETED, "true")
    }

    companion object {
        internal const val KEY_ONBOARDING_COMPLETED = "is_onboarding_completed"
    }
}

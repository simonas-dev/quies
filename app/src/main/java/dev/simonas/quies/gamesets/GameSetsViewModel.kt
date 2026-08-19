package dev.simonas.quies.gamesets

import androidx.lifecycle.ViewModel
import dev.simonas.quies.data.GameSet
import dev.simonas.quies.data.GameSetRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
internal class GameSetsViewModel(
    gameSetRepository: GameSetRepository,
) : ViewModel() {

    val state: StateFlow<State> = MutableStateFlow(
        State(
            gameSets = gameSetRepository.getAll(),
        )
    )

    data class State(
        val gameSets: List<GameSet> = emptyList(),
    )
}

package dev.simonas.quies.gamesets

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
internal class GameSetsViewModel @Inject constructor(
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
